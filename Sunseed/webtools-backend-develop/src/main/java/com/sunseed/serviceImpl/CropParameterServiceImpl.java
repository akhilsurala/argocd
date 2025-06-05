package com.sunseed.serviceImpl;

import java.time.LocalDate;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.sunseed.enums.RunStatus;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.sunseed.entity.AgriGeneralParameter;
import com.sunseed.entity.Bed;
import com.sunseed.entity.Crop;
import com.sunseed.entity.CropBedSection;
import com.sunseed.entity.CropParameters;
import com.sunseed.entity.Cycles;
import com.sunseed.entity.EconomicParameters;
import com.sunseed.entity.PreProcessorToggle;
import com.sunseed.entity.Projects;
import com.sunseed.entity.PvParameter;
import com.sunseed.entity.Runs;
import com.sunseed.entity.UserProfile;
import com.sunseed.enums.PreProcessorStatus;
import com.sunseed.enums.Toggle;
import com.sunseed.exceptions.AgriGeneralParametersException;
import com.sunseed.exceptions.ConflictException;
import com.sunseed.exceptions.ResourceNotFoundException;
import com.sunseed.exceptions.UnprocessableException;
import com.sunseed.mappers.CropParameterModelMapper;
import com.sunseed.model.ValidationGroups;
import com.sunseed.model.requestDTO.BedRequestDto;
import com.sunseed.model.requestDTO.CropParameterRequestDto;
import com.sunseed.model.requestDTO.CyclesRequestDto;
import com.sunseed.model.responseDTO.CropParametersResponseDto;
import com.sunseed.repository.AgriGeneralParameterRepo;
import com.sunseed.repository.BedRepo;
import com.sunseed.repository.CropBedSectionRepo;
import com.sunseed.repository.CropParametersRepo;
import com.sunseed.repository.CropRepository;
import com.sunseed.repository.CyclesRepo;
import com.sunseed.repository.EconomicParameterRepository;
import com.sunseed.repository.PreProcessorToggleRepository;
import com.sunseed.repository.ProjectsRepository;
import com.sunseed.repository.PvParameterRepository;
import com.sunseed.repository.RunsRepository;
import com.sunseed.repository.UserProfileRepository;
import com.sunseed.service.CropParameterService;
import com.sunseed.service.PreProcessorToggleService;
import com.sunseed.service.RunService;

import jakarta.transaction.Transactional;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import lombok.Builder;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Builder
public class CropParameterServiceImpl implements CropParameterService {

    private final UserProfileRepository userProfileRepo;
    private final ProjectsRepository projectRepo;
    private final Validator validator;
    private final CropRepository cropRepo;
    private final CropParametersRepo cropParametersRepo;
    private final CyclesRepo cyclesRepo;
    private final BedRepo bedRepo;
    private final CropBedSectionRepo cropBedSectionRepo;
    private final CropParameterModelMapper modelMapper;
    private final PreProcessorToggleRepository preProcessorToggleRepository;
    private final AgriGeneralParameterRepo agriGeneralParameterRepository;
    private final CropParametersRepo cropParametersRepository;
    private final RunsRepository runRepository;
    private final RunService runService;
    private final PreProcessorToggleService preProcessorToggleService;
    private final EconomicParameterRepository economicParameterRepo;
    private final PvParameterRepository pvParameterRepository;

    @Override
    @Transactional
    public CropParametersResponseDto saveCropParameter(CropParameterRequestDto request, Long projectId, Long runId,
                                                       Long userId) {
        if (userId == null || userId <= 0)
            throw new AgriGeneralParametersException("user.not.found", HttpStatus.NOT_FOUND);
        if (projectId == null || projectId <= 0)
            throw new AgriGeneralParametersException("project.not.found", HttpStatus.NOT_FOUND);
        UserProfile userProfile = userProfileRepo.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("user.not.found"));
        System.out.println(" saveCropParameter() method !");
        Projects project = projectRepo.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("project.not.found"));
        if (!userId .equals( project.getUserProfile().getUserId()))
            throw new AgriGeneralParametersException("unauthorized.user", HttpStatus.UNAUTHORIZED);

//        update or create run

        Runs run = null;
        PreProcessorToggle toggles = null;
        if (runId == null) {
            toggles = preProcessorToggleService.getPreProcessorToggles(projectId);
            System.out.println("toggles in crop parameter" + toggles.getToggle().name());
        } else {
            run = runService.getRunById(runId);
            if (run.getRunStatus() != RunStatus.HOLDING) {
                throw new UnprocessableException("run.not.holding");
            }
            toggles = run.getPreProcessorToggle();
        }

        if (runId != null) {
            run = runService.updateRun(runId, toggles);
        } else {
            System.out.println("Helooooooooooooooooooooooo Enter");
//            runId = createRun(projectId, toggles);
            System.out.println("Exit   HEllllllllllllllllllllllll");
        }

        // check toggle when runId is not given
        if (runId == null) {
            String toggleValue = preProcessorToggleRepository
                    .findToggleByProjectProjectIdAndStatus(project.getProjectId(), PreProcessorStatus.DRAFT);
            System.out.println("toggle get from project in crop parameters:" + toggleValue);
            if (toggleValue == null)
                throw new AgriGeneralParametersException("toggle.not.exists", HttpStatus.BAD_REQUEST);
            else if (toggleValue.equalsIgnoreCase(Toggle.ONLY_PV.name())) {
                System.out.println("Enter in only only pv condition in crop parameters");
                agriGeneralParameterRepository
                        .findByProjectProjectIdAndStatus(project.getProjectId(), PreProcessorStatus.DRAFT)
                        .ifPresent(agriGeneralParameterRepository::delete);
                cropParametersRepository
                        .findByProjectProjectIdAndStatus(project.getProjectId(), PreProcessorStatus.DRAFT)
                        .ifPresent(cropParametersRepository::delete);
                throw new AgriGeneralParametersException("crop.cant.only.pv", HttpStatus.UNPROCESSABLE_ENTITY);
            }
            Optional<CropParameters> cropParametersDraft = cropParametersRepo.findByProjectAndStatus(project,
                    PreProcessorStatus.DRAFT);

            if (cropParametersDraft.isPresent()) {
                throw new ConflictException("cropParameters.draftExist");
            }

        }
        // if run Id is given
        else {
            // validating run
            Runs existingRun = runRepository.findById(runId)
                    .orElseThrow(() -> new AgriGeneralParametersException("run.not.found", HttpStatus.NOT_FOUND));
            run = existingRun;

            if (project.getProjectId() != existingRun.getInProject().getProjectId())
                throw new AgriGeneralParametersException("run.not.of.project", HttpStatus.UNPROCESSABLE_ENTITY);

            String toggleValue = null;
            if (existingRun.getPreProcessorToggle() != null && existingRun.getPreProcessorToggle().getToggle() != null)
                toggleValue = existingRun.getPreProcessorToggle().getToggle().name();

            if (toggleValue == null)
                throw new AgriGeneralParametersException("toggle.not.exists", HttpStatus.BAD_REQUEST);
            else if (toggleValue.equalsIgnoreCase(Toggle.ONLY_PV.name())) {
                Optional.ofNullable(existingRun.getAgriGeneralParameters()).ifPresent(agriGeneralParameterRepository::delete);
                Optional.ofNullable(existingRun.getCropParameters()).ifPresent(cropParametersRepository::delete);
                existingRun.setAgriGeneralParameters(null);
                existingRun.setCropParameters(null);
                runRepository.save(existingRun);
                throw new AgriGeneralParametersException("crop.cant.only.pv", HttpStatus.UNPROCESSABLE_ENTITY);
            }
            // checking if already exists
            Optional<CropParameters> optionalExistingAgriGeneralParameter = Optional
                    .ofNullable(existingRun.getCropParameters());
            if (optionalExistingAgriGeneralParameter.isPresent()) {
                throw new ConflictException("cropParameters.draftExist");

            }

        }
        // validate crop parameters
        validateCropParameters(request);

        CropParameters cropParameters = CropParameters.builder().project(project).status(runId == null ? PreProcessorStatus.DRAFT : PreProcessorStatus.CREATED)
                .build();

        List<Cycles> cycles = new ArrayList<>(); // All the cycles selected/created

        List<CyclesRequestDto> reqCycles = request.getCycles();

        for (CyclesRequestDto cycleRequest : reqCycles) {
            Cycles cycle = new Cycles();

            // for each cycle
            List<Bed> beds = new ArrayList<>();

            String name = cycleRequest.getCycleName();
            LocalDate startDate = cycleRequest.getCycleStartDate();

            List<BedRequestDto> bedRequestList = cycleRequest.getCycleBedDetails();
            beds = populateBeds(beds, bedRequestList, name);

            // sorting bed name in ascending order ********* bed1, bed2,bed3
            beds.sort(Comparator.comparingInt(bed -> Integer.parseInt(bed.getBedName().replaceAll("\\D+", ""))));
            beds.forEach(bed -> System.out.println("bed Name in ascending order :" + bed.getBedName()));

            beds.forEach(bed -> bed.setCycle(cycle));

            // add to cycles
            cycle.setName(name);
            cycle.setStartDate(startDate);
            cycle.setBeds(beds);
            cycle.setCropParameters(cropParameters);

            List<String> interBed = cycleRequest.getInterBedPattern();
            cycle.setInterBedPattern(interBed);

            cycles.add(cycle);
        }

// Sort cycles by startDate in ascending order
        cycles.sort(Comparator.comparing(Cycles::getStartDate));

        cropParameters.setCycles(cycles);

        CropParameters savedCropParameters = cropParametersRepo.save(cropParameters);
        System.out.println("Saved crop parameter, mapping them");
        if (runId != null) {
            run.setCropParameters(savedCropParameters);
            run = runRepository.save(run);
            runService.updateRun(run.getRunId(), toggles);
        }

        CropParametersResponseDto cropParametersResponseDto = modelMapper.entityToCropParameterResponseDto(savedCropParameters);

        return cropParametersResponseDto;
    }

    private Long createRun(Long projectId, PreProcessorToggle toggle) {
        Runs run = runService.createRun(projectId, toggle);
        // System.out.println("runId 241" + run.getId());
        Long runId = null;
        if (run != null) {
            runId = run.getRunId();

            if (toggle.getToggle().name().equals("APV")) {
                updatePvParameterStatus(run.getPvParameters());
                updateCropParameterStatus(run.getCropParameters());
                updateAgriGenralParameterStatus(run.getAgriGeneralParameters());
                updateEconomicParameterStatus(run.getEconomicParameters());
            } else if (toggle.getToggle().name().equals("ONLY_PV")) {
                updatePvParameterStatus(run.getPvParameters());
            } else if (toggle.getToggle().name().equals("ONLY_AGRI")) {
                updateAgriGenralParameterStatus(run.getAgriGeneralParameters());
                updateCropParameterStatus(run.getCropParameters());
            } else {
                updateCropParameterStatus(run.getCropParameters());
            }

            updatePreProcessorToggleStatus(run.getPreProcessorToggle());
        } else {
            throw new UnprocessableException("run.not.created");
        }
        return runId;

    }

    private void updatePvParameterStatus(PvParameter pvParameters) {
        pvParameters.setStatus(PreProcessorStatus.CREATED);
        pvParameterRepository.save(pvParameters);
    }

    private void updateEconomicParameterStatus(EconomicParameters economicParameters) {
        economicParameters.setStatus(PreProcessorStatus.CREATED);
        economicParameterRepo.save(economicParameters);
    }

    private void updateCropParameterStatus(CropParameters cropParameters) {
        cropParameters.setStatus(PreProcessorStatus.CREATED);
        cropParametersRepo.save(cropParameters);
    }

    private void updatePreProcessorToggleStatus(PreProcessorToggle preProcessorToggles) {
        preProcessorToggles.setPreProcessorStatus(PreProcessorStatus.CREATED);
        preProcessorToggleRepository.save(preProcessorToggles);
    }

    private void updateAgriGenralParameterStatus(AgriGeneralParameter agriGeneralParameter) {
        agriGeneralParameter.setStatus(PreProcessorStatus.CREATED);
        agriGeneralParameterRepository.save(agriGeneralParameter);
    }

    @Override
    @Transactional
    public CropParametersResponseDto updateCropParameter(CropParameterRequestDto request, Long projectId,
                                                         Long cropParameterId, Long runId, Long userId) {
        if (userId == null || userId <= 0)
            throw new AgriGeneralParametersException("user.not.found", HttpStatus.NOT_FOUND);
        if (projectId == null || projectId <= 0)
            throw new AgriGeneralParametersException("project.not.found", HttpStatus.NOT_FOUND);
        if (cropParameterId == null || cropParameterId <= 0)
            throw new AgriGeneralParametersException("crop.not.null", HttpStatus.NOT_FOUND);

        UserProfile userProfile = userProfileRepo.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("user.not.found"));

        CropParameters cropParameter = cropParametersRepo.findById(cropParameterId)
                .orElseThrow(() -> new ResourceNotFoundException(null, "cropParameter.notFound"));

        Projects project = projectRepo.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project.not.found"));
        if (userId != project.getUserProfile().getUserId())
            throw new AgriGeneralParametersException("unauthorized.user", HttpStatus.UNAUTHORIZED);

//      update or create run

        Runs run = null;
        PreProcessorToggle toggles = null;
//        if (runId == null) {
//            toggles = preProcessorToggleService.getPreProcessorToggles(projectId);
//            System.out.println("toggles in crop parameter" + toggles.getToggle().name());
//        } else {
//            run = runService.getRunById(runId);
//            toggles = run.getPreProcessorToggle();
//        }
//
//        if (runId != null) {
//            run = runService.updateRun(runId, toggles);
//        } else {
//            // System.out.println("Helooooooooooooooooooooooo Enter");
//            // runId = createRun(projectId, toggles);
//            // System.out.println("Exit   HEllllllllllllllllllllllll");
//        }

        // when runId is null
        if (runId == null) {
            // finding the toggle and updating acc.
            String toggleValue = preProcessorToggleRepository
                    .findToggleByProjectProjectIdAndStatus(project.getProjectId(), PreProcessorStatus.DRAFT);

            if (toggleValue == null)
                throw new AgriGeneralParametersException("toggle.not.exists", HttpStatus.BAD_REQUEST);
            else if (toggleValue.equalsIgnoreCase(Toggle.ONLY_PV.getValue().replace(' ', '_'))) {
                agriGeneralParameterRepository
                        .findByProjectProjectIdAndStatus(project.getProjectId(), PreProcessorStatus.DRAFT)
                        .ifPresent(agriGeneralParameterRepository::delete);
                cropParametersRepository
                        .findByProjectProjectIdAndStatus(project.getProjectId(), PreProcessorStatus.DRAFT)
                        .ifPresent(cropParametersRepository::delete);
                throw new AgriGeneralParametersException("crop.cant.only.pv", HttpStatus.UNPROCESSABLE_ENTITY);
            }

        } else {
            // validating run
            Runs existingRun = runRepository.findById(runId)
                    .orElseThrow(() -> new AgriGeneralParametersException("run.not.found", HttpStatus.NOT_FOUND));
            run = existingRun;
            if (run.getRunStatus() != RunStatus.HOLDING) {
                throw new UnprocessableException("run.not.holding");
            }
            if (run.getRunStatus() != RunStatus.HOLDING) {
                throw new UnprocessableException("run.not.holding");
            }
            toggles = existingRun.getPreProcessorToggle();
            if (project.getProjectId() != existingRun.getInProject().getProjectId())
                throw new AgriGeneralParametersException("run.not.of.project", HttpStatus.UNPROCESSABLE_ENTITY);
            String toggleValue = null;
            if (existingRun.getPreProcessorToggle() != null && existingRun.getPreProcessorToggle().getToggle() != null)
                toggleValue = existingRun.getPreProcessorToggle().getToggle().name();

            if (toggleValue == null)
                throw new AgriGeneralParametersException("toggle.not.exists", HttpStatus.BAD_REQUEST);
            else if (toggleValue.equalsIgnoreCase(Toggle.ONLY_PV.name())) {
                Optional.ofNullable(existingRun.getAgriGeneralParameters()).ifPresent(agriGeneralParameterRepository::delete);
                Optional.ofNullable(existingRun.getCropParameters()).ifPresent(cropParametersRepository::delete);
                existingRun.setAgriGeneralParameters(null);
                existingRun.setCropParameters(null);
                runRepository.save(existingRun);
                throw new AgriGeneralParametersException("crop.cant.only.pv", HttpStatus.UNPROCESSABLE_ENTITY);
            }

        }

        // add validating crop parameters
        // validate crop parameters
        validateCropParameters(request);

        cropParameter.setProject(project);

//		List<Cycles> existingCycles = cropParameter.getCycles();
//		System.out.println("Size ***** " + existingCycles.size());

        // delete cycles
        List<Long> deletedCycles = request.getDeletedCyclesId();
        if (!deletedCycles.isEmpty()) {
            for (Long cycleId : deletedCycles) {
                System.out.println("deleted cycle is: " + cycleId);
                cyclesRepo.deleteById(cycleId);
            }
        }

        List<CyclesRequestDto> reqCycles = request.getCycles();
        List<Cycles> cyclesResponse = new ArrayList<>();

        for (CyclesRequestDto cycleRequest : reqCycles) {
            // update cycles
            Long cycleId = cycleRequest.getId();
            System.out.println("Fetching Cycle id : " + cycleId);
            Cycles cycle;

            if (cycleId == null) {
                cycle = new Cycles();
//				//cycle.setName(cycleRequest.getCycleName());
//				//cycle.setStartDate(cycleRequest.getCycleStartDate());
                List<Bed> beds = new ArrayList<>();

                String name = cycleRequest.getCycleName();
                LocalDate startDate = cycleRequest.getCycleStartDate();

                List<BedRequestDto> bedRequestList = cycleRequest.getCycleBedDetails();
                beds = populateBeds(beds, bedRequestList, name);

                // sorting bedName in ascending order ------ ***********
                beds.sort(Comparator.comparingInt(bed -> Integer.parseInt(bed.getBedName().replaceAll("\\D+", ""))));


                cycle.setBeds(beds);
                beds.forEach(bed -> bed.setCycle(cycle));
                cycle.setCropParameters(cropParameter);
                cycle.setName(name);
                cycle.setStartDate(startDate);

                List<String> interBed = cycleRequest.getInterBedPattern();
                cycle.setInterBedPattern(interBed);

            } else {
                cycle = cyclesRepo.findById(cycleId).orElseThrow(() -> new ResourceNotFoundException("cycle.notFound"));


                List<Bed> beds = new ArrayList<>();

                System.out.println("cycle name :" + cycleRequest.getCycleName());
                cycle.setName(cycleRequest.getCycleName());
                cycle.setStartDate(cycleRequest.getCycleStartDate());

                // delete beds
                List<Long> deletedBeds = cycleRequest.getDeletedBedDetailsId();
                if (deletedBeds != null) {
                    for (Long bedId : deletedBeds) {
                        bedRepo.deleteById(bedId);
                    }
                }
                List<BedRequestDto> bedRequestList = cycleRequest.getCycleBedDetails();
                System.out.println("Cycle bed size " + cycle.getBeds().size());
                beds = updateBeds(cycle.getBeds(), bedRequestList, cycle.getName());
                // sorting ******************
                beds.sort(Comparator.comparingInt(bed -> Integer.parseInt(bed.getBedName().replaceAll("\\D+", ""))));
                beds.forEach(bed -> bed.setCycle(cycle));

                // add to cycles
                cycle.setBeds(beds);
                cycle.setCropParameters(cropParameter);

                List<String> interBed = cycleRequest.getInterBedPattern();
                cycle.setInterBedPattern(interBed);
            }
            cyclesResponse.add(cycle);
        }

// Sort cycles by startDate in ascending order
        cyclesResponse.sort(Comparator.comparing(Cycles::getStartDate));
        // for testing only  --
        for(Cycles cycletest:cyclesResponse)
            System.out.println("local date of cycle is :"+cycletest.getStartDate());

        cropParameter.setCycles(cyclesResponse);
        CropParameters savedCropParameters = cropParametersRepo.save(cropParameter);

        if (runId != null) {
            run.setCropParameters(savedCropParameters);
            run = runRepository.save(run);
            runService.updateRun(run.getRunId(), toggles);
        }

        CropParametersResponseDto cropParametersResponseDto = modelMapper.entityToCropParameterResponseDto(savedCropParameters);
        if (runId != null) {
            cropParametersResponseDto.setRunId(runId);
            cropParametersResponseDto.setIsMaster(run.isMaster());
            cropParametersResponseDto.setCloneId(run.getCloneId());
        }
        return cropParametersResponseDto;
    }

    private List<Bed> updateBeds(List<Bed> existingBeds, List<BedRequestDto> bedRequestList, String name) {
        // map of id and Bed
        Map<Long, Bed> existingBedsMap = existingBeds.stream()
                .collect(Collectors.toMap(Bed::getId, Function.identity()));
        List<Bed> updatedBeds = new ArrayList<>();

        for (BedRequestDto bedRequest : bedRequestList) {
            Bed bed;
            if (bedRequest.getId() != null && existingBedsMap.containsKey(bedRequest.getId())) {
                // If bed exists, retrieve it
                bed = existingBedsMap.get(bedRequest.getId());
            } else {
                System.out.println("Creating new beds !!!!!!!");
                // If bed does not exist, create a new bed
                bed = new Bed();
            }
            bed.setBedName(bedRequest.getBedName());
            List<CropBedSection> cropBedSections = new ArrayList<>();

            // Making cropBedSection1
            CropBedSection cropBedSection1 = createCropBedSection(bedRequest.getCropId1(), bedRequest.getO1(),
                    bedRequest.getS1(), bedRequest.getO2(), bedRequest.getStretch(), name);
            cropBedSections.add(cropBedSection1);
            cropBedSection1.setBed(bed);

            Long cropId2 = bedRequest.getOptionalCropType();
            if (cropId2 != null) {
                // Make cropBedSection2
                CropBedSection cropBedSection2 = createCropBedSection(bedRequest.getOptionalCropType(),
                        bedRequest.getOptionalO1(), bedRequest.getOptionalS1(), bedRequest.getOptionalO2(), bedRequest.getOptionalStretch(), name);
                cropBedSections.add(cropBedSection2);
                cropBedSection2.setBed(bed);
            }

            bed.setCropBed(cropBedSections);

            updatedBeds.add(bed);
        }


        return updatedBeds;
    }


    private List<Bed> populateBeds(List<Bed> beds, List<BedRequestDto> bedRequestList, String name) {


        for (BedRequestDto bedRequest : bedRequestList) {
            // Making cropBedSection1
            List<CropBedSection> cropBedSections = new ArrayList<CropBedSection>();
            Bed bed = new Bed();
            // ************ add bed Name in bed *************
            bed.setBedName(bedRequest.getBedName());
            CropBedSection cropBedSection1 = createCropBedSection(bedRequest.getCropId1(), bedRequest.getO1(),
                    bedRequest.getS1(), bedRequest.getO2(), bedRequest.getStretch(), name);
//            cropBedSection1.setStretch(bedRequest.getStretch());
            cropBedSections.add(cropBedSection1);

            Long cropId2 = bedRequest.getOptionalCropType();
            if (cropId2 != null) {
                // Make cropBedSection2
                CropBedSection cropBedSection2 = createCropBedSection(bedRequest.getOptionalCropType(),
                        bedRequest.getOptionalO1(), bedRequest.getOptionalS1(), bedRequest.getOptionalO2(), bedRequest.getOptionalStretch(), name);
                cropBedSections.add(cropBedSection2);

                cropBedSection2.setBed(bed);
            }

            bed.setCropBed(cropBedSections);
            cropBedSection1.setBed(bed);

            beds.add(bed);
        }

        return beds;
    }

    private CropBedSection createCropBedSection(Long cropId, Double o1, Double s1, Double o2, Double stretch, String name) {
        System.out.println("Cycle name: " + name + "---->  Fetching Crop from DB, cropiD: " + cropId);
        Crop crop = cropRepo.findById(cropId).orElseThrow(() -> new ResourceNotFoundException("crop.notFound"));
        System.out.println("---->  Fetched Crop from DB");
        CropBedSection cropBedSection = new CropBedSection();
        cropBedSection.setCrop(crop);

        cropBedSection.setO1(o1 < 50.0 ? 0.0 : o1);
        cropBedSection.setS1(s1);
        cropBedSection.setO2(o2);
        cropBedSection.setStretch(stretch);

        return cropBedSection;
    }

    @Override
    public CropParametersResponseDto getCropParameters(Long projectId, Long userId, Long runId) {
        UserProfile userProfile = userProfileRepo.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("user.not.found"));
        Projects project = projectRepo.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project.not.found"));
        Runs existingRun = null;
        Optional<CropParameters> cropParameters = null;

        if (runId == null) {
            if (project.getUserProfile().getUserProfileId() != userProfile.getUserProfileId()) {
                throw new UnprocessableException("project.mismatch");
            }

            cropParameters = cropParametersRepo.findByProjectAndStatus(project, PreProcessorStatus.DRAFT);
        } else {
            existingRun = runRepository.findById(runId)
                    .orElseThrow(() -> new AgriGeneralParametersException("run.not.found", HttpStatus.NOT_FOUND));

            if (project.getProjectId() != existingRun.getInProject().getProjectId())
                throw new AgriGeneralParametersException("run.not.of.project", HttpStatus.UNPROCESSABLE_ENTITY);

            cropParameters = Optional.ofNullable(existingRun.getCropParameters());
        }
        if (cropParameters.isPresent()) {
            CropParametersResponseDto cropParametersResponseDto = modelMapper.entityToCropParameterResponseDto(cropParameters.get());
            if (runId != null) {
                cropParametersResponseDto.setRunId(runId);
                cropParametersResponseDto.setIsMaster(existingRun.isMaster());
                cropParametersResponseDto.setCloneId(existingRun.getCloneId());
            }
            return cropParametersResponseDto;
        }
        // private Long id;
        // private Long projectId;
        // private List<CyclesResponseDto> cycles;
        return null;
    }

    // method to validate crop parameters
    private void validateCropParameters(CropParameterRequestDto request) {
        System.out.println("Enter in crop parameters validation method");
        Set<ConstraintViolation<CropParameterRequestDto>> cropParametersViolations = validator.validate(request,
                ValidationGroups.CropParametersGroup.class);

        if (!cropParametersViolations.isEmpty()) {
            System.out.println("Enter in violation group of crop parameters:");
            ConstraintViolation<CropParameterRequestDto> violation = cropParametersViolations.iterator().next();
            throw new AgriGeneralParametersException(violation.getMessage(), HttpStatus.BAD_REQUEST);
        }

        // checking if in cycles inter bed pattern is null or empty then proceed else size should be between 2 to 15
        request.getCycles().forEach(cycle -> {
            if (!(cycle.getInterBedPattern() == null || cycle.getInterBedPattern().isEmpty())) {
                if (cycle.getInterBedPattern().size() < 1 || cycle.getInterBedPattern().size() > 15)
                    throw new AgriGeneralParametersException("bedPattern.size", HttpStatus.BAD_REQUEST);
            }
        });
        //  Check if the cycle names are unique
        Set<String> cycleNames = new HashSet<>();
        for (var cycle : request.getCycles()) {
            if (!cycleNames.add(cycle.getCycleName())) {
                throw new ConflictException("duplicate.cycle.name");
            }
        }

    }

}
