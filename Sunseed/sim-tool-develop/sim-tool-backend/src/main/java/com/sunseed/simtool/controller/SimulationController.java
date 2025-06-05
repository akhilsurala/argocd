package com.sunseed.simtool.controller;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.sunseed.simtool.exception.InvalidRequestBodyArgumentException;
import com.sunseed.simtool.model.request.SimulationStatusDto;
import com.sunseed.simtool.model.response.SimulationTaskDto;
import com.sunseed.simtool.model.response.SimulationTaskStatusDto;
import com.sunseed.simtool.service.SimulationService;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@AllArgsConstructor
@RequestMapping("/v1")
@Slf4j
public class SimulationController {
	
	private final SimulationService simulationService;
	
	@PutMapping("/simulation/status")
	private ResponseEntity<Map<String,List<SimulationTaskStatusDto>>> updateSimulationTaskStatus(@Valid @RequestBody SimulationStatusDto statusDto){
		log.debug("Enter into updateSimulationTaskStatus");
		Map<String,List<SimulationTaskStatusDto>> simulationTaskStatusDto = simulationService.updateStatus(statusDto);
		log.debug("Exit from updateSimulationTaskStatus");
		return new ResponseEntity<>(simulationTaskStatusDto, HttpStatus.OK);
	}
	
	@GetMapping("/simulation")
	private ResponseEntity<Map<String, List<SimulationTaskDto>>> getSimulation(@RequestParam(required = true) Long id)
	{
		log.debug("Enter into getSimulation");
		Map<String, List<SimulationTaskDto>> simulation = simulationService.getSimulationResult(id);
		log.debug("Exit from getSimulation");
		return new ResponseEntity<>(simulation, HttpStatus.OK);
	}
	
	@GetMapping("/download")
    public ResponseEntity<?> getFileFromSunseedGiz(@RequestParam String filename) {
        try {
            // Fetch file data and content type
            byte[] fileData = simulationService.getFileFromSunseedGiz(filename);
            String contentType = simulationService.getFileContentType(filename);

            // Prepare headers for the response
            HttpHeaders headers = new HttpHeaders();
            if (contentType != null) {
                headers.setContentType(MediaType.parseMediaType(contentType));
            } else {
                headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);  // Fallback for unknown file types
            }
            headers.setContentDispositionFormData("inline", filename);

            // Return the file as the response body
            return new ResponseEntity<>(fileData, headers, HttpStatus.OK);
        } catch (IOException e) {
            return new ResponseEntity<>("File not found or could not be read: " + filename, HttpStatus.NOT_FOUND);
        }
    }
	
	@PostMapping("/file")
    public ResponseEntity<Object> uploadTextureFile(@RequestParam("file") MultipartFile file) {
        try {
            // Call service layer to handle the file upload logic
            String filePath = simulationService.uploadTextureFile(file);
            
            // Return success response
            return new ResponseEntity<>("File uploaded successfully: " + filePath, HttpStatus.OK);

        } catch (InvalidRequestBodyArgumentException e) {
            // Handle invalid file type (not PNG)
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);

        } catch (Exception e) {
            // Catch any other unexpected errors
            return new ResponseEntity<>("Internal server error: Unable to upload file", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
	
	@DeleteMapping("/file/{fileName}")
    public ResponseEntity<String> deleteImage(@PathVariable String fileName) {
        try {
            String message = simulationService.deleteTextureFile(fileName);
            return new ResponseEntity<>(message, HttpStatus.OK);
        } catch (IOException e) {
            return new ResponseEntity<>("Internal Server Error: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
