package com.sunseed.simtool.serviceimpl;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sunseed.simtool.constant.SimulationType;
import com.sunseed.simtool.constant.Status;
import com.sunseed.simtool.entity.CropYield;
import com.sunseed.simtool.entity.Simulation;
import com.sunseed.simtool.entity.SimulationTask;
import com.sunseed.simtool.exception.InvalidRequestBodyArgumentException;
import com.sunseed.simtool.helper.SimulationHelper;
import com.sunseed.simtool.model.request.SimulationStatusDto;
import com.sunseed.simtool.model.response.SimulationTaskDto;
import com.sunseed.simtool.model.response.SimulationTaskStatusDto;
import com.sunseed.simtool.rabbitmq.MessageProducer;
import com.sunseed.simtool.repository.SimulationRepository;
import com.sunseed.simtool.repository.SimulationTaskRepository;
import com.sunseed.simtool.service.SimulationService;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class SimulationServiceImpl implements SimulationService {
	
	private final SimulationRepository simulationRepository;
	private final SimulationTaskRepository simulationTaskRepository;
	private final MessageProducer messageProducer;
    private final String exchangeName;
	private final String routingKeyAgri;
	private final ObjectMapper objectMapper;
	private final SimulationHelper simulationHelper;
	
	@Value("${file.upload-dir}")
    private String uploadDir;
	
	@Value("${minimum.length}")
	private Double minLength;
	
	@Value("${number.pitch}")
	private Integer numberPitch;
	
	@Value("${minimum.modules}")
	private Double minModules;
	
	@Value("${minimum.ground.length}")
	private Double minimumGroundLength;
	
	@Value("#{'${server.path}'.split(',')}")
	private Set<String> serverPaths;

	
	public SimulationServiceImpl(SimulationRepository simulationRepository,
			SimulationTaskRepository simulationTaskRepository, MessageProducer messageProducer,
			@Value("${rabbitmq.exchange}") String exchangeName,
			@Value("${rabbitmq.routingkey.agri}") String routingKeyAgri,
			ObjectMapper objectMapper,SimulationHelper simulationHelper) {
		super();
		this.simulationRepository = simulationRepository;
		this.simulationTaskRepository = simulationTaskRepository;
		this.messageProducer = messageProducer;
		this.exchangeName = exchangeName;
		this.routingKeyAgri = routingKeyAgri;
		this.objectMapper = objectMapper;
		this.simulationHelper = simulationHelper;
	}
	
	@Override
	@Transactional
	public Map<String,List<SimulationTaskStatusDto>> updateStatus(SimulationStatusDto statusDto) {
		log.debug("Enter into updateStatus");
		
		Simulation simulation = simulationRepository.findById(statusDto.getSimulationId()).orElse(null);
		
		if(simulation == null)
			throw new InvalidRequestBodyArgumentException("No such simulation exists");
		
		boolean simulationHasFailedTasks = simulation.getSimulationTasks().stream()
				.filter(st -> Arrays.asList(Status.FAILED).contains(st.getPvStatus()) || 
						Arrays.asList(Status.FAILED).contains(st.getAgriStatus()))
				.collect(Collectors.toList()).isEmpty() ? false : true;
		
		if(statusDto.getStatus().equalsIgnoreCase("Cancel") || statusDto.getStatus().equalsIgnoreCase("Pause"))
		{
			log.info("Fetching simulation tasks ....");
			
			List<SimulationTask> simulationTasks = simulation.getSimulationTasks().stream()
					.filter(st -> Arrays.asList(Status.QUEUED, Status.RUNNING, Status.PENDING).contains(st.getPvStatus()) || 
							Arrays.asList(Status.QUEUED, Status.RUNNING, Status.PENDING).contains(st.getAgriStatus()))
					.collect(Collectors.toList());
			
			log.info("{} the tasks .... ", statusDto.getStatus());
			
			simulationTasks.forEach(st -> {
				boolean isCancelled = simulationHelper.cancelSimulation(st.getId());
				if(isCancelled)
				{
					log.debug("Simulation Task ID#{}\nTask:\t{}", st.getId(), st);
					
					if(statusDto.getStatus().equalsIgnoreCase("Cancel")) {
						if(st.getPvStatus().equals(Status.PENDING) || st.getPvStatus().equals(Status.QUEUED) || 
								st.getPvStatus().equals(Status.RUNNING))
							st.setPvStatus(Status.CANCELLED);
						if(st.getAgriStatus().equals(Status.PENDING) || st.getAgriStatus().equals(Status.QUEUED) || 
								st.getAgriStatus().equals(Status.RUNNING))
							st.setAgriStatus(Status.CANCELLED);
						
						if(!simulationHasFailedTasks)
							simulation.setStatus(Status.CANCELLED);
						
					}
					else if(statusDto.getStatus().equalsIgnoreCase("Pause")) {
						if(st.getPvStatus().equals(Status.PENDING) || st.getPvStatus().equals(Status.QUEUED) || 
								st.getPvStatus().equals(Status.RUNNING))
							st.setPvStatus(Status.PAUSED);
						if(st.getAgriStatus().equals(Status.PENDING) || st.getAgriStatus().equals(Status.QUEUED) || 
								st.getAgriStatus().equals(Status.RUNNING))
							st.setAgriStatus(Status.PAUSED);
						
						if(!simulationHasFailedTasks)
							simulation.setStatus(Status.PAUSED);
					}
				}
			});
			
			simulationRepository.save(simulation);
		}
		else if(statusDto.getStatus().equalsIgnoreCase("Resume"))
		{
			log.info("Fetching simulation tasks ....");
			
			List<SimulationTask> simulationTasks = simulation.getSimulationTasks().stream()
					.filter(st -> Arrays.asList(Status.PAUSED, Status.FAILED, Status.CANCELLED).contains(st.getPvStatus()) || 
							Arrays.asList(Status.PAUSED, Status.FAILED, Status.CANCELLED).contains(st.getAgriStatus()))
					.collect(Collectors.toList());
			
			if(simulationTasks != null && !simulationTasks.isEmpty()) {
				simulation.setStatus(Status.QUEUED);
				simulationRepository.save(simulation);
			}
			
			log.info("Resuming the tasks .... ");
			
			List<SimulationTask> tasksForRequeueing = new ArrayList<>();
			for(SimulationTask task : simulationTasks) {
				boolean hasPvResult = hasPvResults(task);
				boolean hasAgriResult = hasAgriResults(task);
				
				if (task.getSimulationBlock().getBlockSimulationType() == SimulationType.ONLY_PV) {
					if (hasPvResult) {
						task.setPvStatus(Status.SUCCESS);
					} else {
						task.setPvStatus(Status.QUEUED);
						task.setEnqueuedAt(LocalDateTime.now());
						tasksForRequeueing.add(task);
					}
				}else if(task.getSimulationBlock().getBlockSimulationType() == SimulationType.ONLY_AGRI) {
					if(hasAgriResult) {
						task.setAgriStatus(Status.SUCCESS);
					} else {
						task.setAgriStatus(Status.QUEUED);
						task.setEnqueuedAt(LocalDateTime.now());
						tasksForRequeueing.add(task);
					}
				} else {
					if (hasPvResult && hasAgriResult) {
						task.setPvStatus(Status.SUCCESS);
						task.setAgriStatus(Status.SUCCESS);
					} else {
						task.setPvStatus(Status.QUEUED);
						task.setAgriStatus(Status.QUEUED);
						task.setEnqueuedAt(LocalDateTime.now());
						tasksForRequeueing.add(task);
					}
				}
				
				task = simulationTaskRepository.saveAndFlush(task);
			}
			
			// updating simulation table completed task count and status 
			simulationTaskRepository.updateSimulationCountAndStatusWhileRequequeing(simulation.getId());
			
			// now requeueing the tasks which needs to be requeued
			tasksForRequeueing.stream().forEach(t->{
				registerTransactionSynchronization(t, exchangeName, routingKeyAgri);
			});
		}
		
		log.info("Fetching task status history .... ");
		
		List<SimulationTaskStatusDto> simulationTaskStatusPvDto = simulationTaskRepository.countTaskByPvStatus(simulation);
		List<SimulationTaskStatusDto> simulationTaskStatusAgriDto = simulationTaskRepository.countTaskByAgriStatus(simulation);
		
		log.debug("Exit from updateStatus");
		return Map.of("pv", simulationTaskStatusPvDto, "agri", simulationTaskStatusAgriDto);
	}
	
	private Boolean hasPvResults(SimulationTask simulationTask) {
		
		if(simulationTask.getPvYields() != null)
			return true;
		return false;
	}
	
	private Boolean hasAgriResults(SimulationTask simulationTask) {
		
		if(simulationTask.getCropYields() != null && !simulationTask.getCropYields().isEmpty())
			return true;
		return false;
	}

	@Override
	public Map<String, List<SimulationTaskDto>> getSimulationResult(Long id) {
		log.debug("Enter into getSimulationResult");
		
		Optional<Simulation> optionalSimulation = simulationRepository.findById(id);
		
		if(optionalSimulation.isPresent())
		{
			Simulation simulation = optionalSimulation.get();
			
			List<SimulationTaskDto> simulationTasks = new ArrayList<>();
			simulation.getSimulationTasks().forEach(st -> {
				simulationTasks.add(convertToSimulationTaskDto(st));
			});
			
			Collections.sort(simulationTasks, Comparator.comparing(SimulationTaskDto :: getDateTime));
			
			log.debug("Exit from getSimulationResult");			
			return Map.of("data", simulationTasks);
		}
		else
			throw new InvalidRequestBodyArgumentException("Simulation with id: " + id + " does not exist");
	}

	private SimulationTaskDto convertToSimulationTaskDto(SimulationTask simulationTask) {
		
		SimulationTaskDto simulationTaskDto = new SimulationTaskDto();
		
		simulationTaskDto.setDateTime(simulationTask.getDate());
		
		if(simulationTask.getCropYields() != null) {
			simulationTaskDto.setCarbonAssimilation(simulationTask.getCropYields().stream().collect(Collectors.groupingBy(CropYield :: getCropName,
					Collectors.summingDouble(CropYield :: getCarbonAssimilation))));
		}
		
		if(simulationTask.getPvYields() != null) {
			simulationTaskDto.setPvYield(simulationTask.getPvYields().getPvYield());
			simulationTaskDto.setFrontGain(simulationTask.getPvYields().getFrontGain());
			simulationTaskDto.setRearGain(simulationTask.getPvYields().getRearGain());
			simulationTaskDto.setAlbedo(simulationTask.getPvYields().getAlbedo());
		}
		
		return simulationTaskDto;
	}
	
	public byte[] getFileFromSunseedGiz(String filename) throws IOException {
	    // List of directories to check, starting with the direct file path
	    List<String> directories = getDirectories(filename);

	    // Iterate through the directories and check if the file exists
	    for (String directory : directories) {
	        File file = new File(directory);
	        if (file.exists() && file.isFile()) {
	            // Return the file's content as a byte array immediately upon finding it
	            return FileCopyUtils.copyToByteArray(file);
	        }
	    }

	    // If the file is not found in any directory
	    throw new IOException("File not found: " + filename);
	}
	
	public Resource getLargeFileFromSunseedGiz(String filename) throws IOException {
		// List of directories to check
		List<String> directories = getDirectories(filename);

		for (String directory : directories) {
			File file = new File(directory);
			if (file.exists() && file.isFile()) {
				// Return a streaming InputStreamResource
				return new InputStreamResource(Files.newInputStream(Path.of(directory)));
			}
		}

		throw new IOException("File not found: " + filename);
	}
	
	public String getFileContentType(String filename) throws IOException {
	    // List of directories to check, starting with the direct file path
	    List<String> directories = getDirectories(filename);

	    // Iterate through the directories and check if the file exists
	    for (String directory : directories) {
	        File file = new File(directory);
	        if (file.exists() && file.isFile()) {
	            // Get and return the content type for the found file
	            Path filePath = file.toPath();
	            return Files.probeContentType(filePath);
	        }
	    }

	    // If the file is not found in any directory
	    throw new IOException("File not found: " + filename);
	}
	
	// getting directories for extracting files
	public List<String> getDirectories(String filename) {
		List<String> directories = new ArrayList<>();

		directories.add(filename);
		serverPaths.stream().forEach(path -> {
			directories.add(path + filename);
		});
		return directories;
	}
	
	public String uploadTextureFile(MultipartFile file) throws IOException, InvalidRequestBodyArgumentException
	{
        // Validate if the uploaded file is a PNG
        if (file.isEmpty()) {
            throw new InvalidRequestBodyArgumentException("File is empty");
        }

        // Check the file type (content type must be image/png)
        String contentType = file.getContentType();
        if (!isValidImageType(contentType)) {
            throw new InvalidRequestBodyArgumentException("Only PNG and JPG files are allowed");
        }

        // Save the file
        return saveTextureFile(file);
    }
	
	private boolean isValidImageType(String contentType) {
//        return contentType.equalsIgnoreCase("image/png") || contentType.equalsIgnoreCase("image/jpeg");
		return contentType.equalsIgnoreCase("image/png") 
        		|| contentType.equalsIgnoreCase("image/csv")
        		|| contentType.equalsIgnoreCase("application/xml")
        		|| contentType.equalsIgnoreCase("image/jpeg");

        
    }

    private String saveTextureFile(MultipartFile file) throws IOException {
        // Define the file's path
        Path filePath = Paths.get(uploadDir, file.getOriginalFilename());

        // Create directories if they don't exist
        Files.createDirectories(filePath.getParent());
        
        File existingFile = filePath.toFile();
        if (existingFile.exists()) {
            // If the file exists, delete it first
            boolean deleted = existingFile.delete();
            if (!deleted) {
                throw new IOException("Failed to delete existing file: " + file.getOriginalFilename());
            }
        }

        // Write the file to the specified directory
        Files.write(filePath, file.getBytes());

        // Return the file path as a string
        return filePath.toString();
    }
    
    public String deleteTextureFile(String fileName) throws IOException {
        // Define the file's path
        Path filePath = Paths.get(uploadDir, fileName);

        File file = filePath.toFile();
        if (!file.exists()) {
            throw new IOException("File not found: " + fileName);
        }

        // Delete the file
        boolean deleted = file.delete();
        if (!deleted) {
            throw new IOException("Failed to delete file: " + fileName);
        }

        return "File deleted successfully: " + fileName;
    }
	
	private void registerTransactionSynchronization(SimulationTask task, String exchangeName, String routingKey) {
		
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                try {
                    String taskMessage = objectMapper.writeValueAsString(task);
                    messageProducer.sendMessage(exchangeName, routingKey, taskMessage);
                } catch (JsonProcessingException e) {
                	log.error("{}", e);
                }
            }
        });
    }

}
