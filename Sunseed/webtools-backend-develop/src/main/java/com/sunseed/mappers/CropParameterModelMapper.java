package com.sunseed.mappers;

import java.util.*;
import java.util.stream.Collectors;

import com.sunseed.entity.*;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.sunseed.model.requestDTO.CropParameterRequestDto;
import com.sunseed.model.responseDTO.AgriPvProtectionHeightResponseDto;
import com.sunseed.model.responseDTO.BedDto;
import com.sunseed.model.responseDTO.CropBedSectionDto;
import com.sunseed.model.responseDTO.CropParametersResponseDto;
import com.sunseed.model.responseDTO.CyclesDto;
import com.sunseed.model.responseDTO.CyclesResponseDto;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class CropParameterModelMapper {
//    private ModelMapper modelMapper = new ModelMapper();
//
//    @Autowired
//    public CropParameterModelMapper(ModelMapper modelMapper) {
//        this.modelMapper = modelMapper;
//
//        // Custom mappings for CropParameters to CropParameterResponseDto
//        modelMapper.typeMap(CropParameters.class, CropParametersResponseDto.class)
//                .addMappings(mapper -> {
//                    mapper.map(src -> src.getProject().getProjectId(), CropParametersResponseDto::setProjectId);
//                    mapper.map(src -> src.getCycles(), CropParametersResponseDto::setCycles);
//
//                    mapper.skip(CropParametersResponseDto::setIsMaster);
//                    mapper.skip(CropParametersResponseDto::setRunId);
//                    mapper.skip(CropParametersResponseDto::setCloneId);
//
//                });
//
//
//        modelMapper.typeMap(Cycles.class, CyclesDto.class)
//                .addMappings(mapper -> {
//                    mapper.map(Cycles::getBeds, CyclesDto::setCycleBedDetails);
//                    mapper.map(Cycles::getStartDate, CyclesDto::setCycleStartDate);
//                    mapper.map(Cycles::getName, CyclesDto::setCycleName);
//                });
//
//        modelMapper.typeMap(Bed.class, BedDto.class)
//                .addMappings(mapper -> {
//                    mapper.map(Bed::getId, BedDto::setCropId1);
//                });
//
//        modelMapper.typeMap(CropBedSection.class, CropBedSectionDto.class)
//                .addMappings(mapper -> {
////            mapper.map(CropBedSection::getCrop, CropBedSectionDto::setCrop);
//                    mapper.map(src -> src.getCrop().getId(), CropBedSectionDto::setCropType);
//                });
//    }

//    public CropParameters cropParameterRequestDtoToEntity(CropParameterRequestDto cropParameterRequestDto) {
//        return this.modelMapper.map(cropParameterRequestDto, CropParameters.class);
//    }

//    public CropParametersResponseDto entityToCropParameterResponseDto(CropParameters cropParameters) {
////    	return CropParametersResponseDto.builder()
////                .cycles(cyclesToCyclesResponseDto(cropParameters.getCycles()))
////                .build();
//        CropParametersResponseDto response = new CropParametersResponseDto();
//        response.setId(cropParameters.getId());
//        response.setRunId(cropParameters.getRun().getRunId());
//        response.setCloneId(cropParameters.getRun().getCloneId());
//        response.setIsMaster(cropParameters.getRun().isMaster());
//        response = this.modelMapper.map(cropParameters, CropParametersResponseDto.class);
//
//        List<Cycles> existingCycles = cropParameters.getCycles();
//        Map<Long, List<BedDto>> cropBedSections = new HashMap<>();
//
//        for (Cycles cycle : existingCycles) {
//            List<Bed> bedListEntity = cycle.getBeds();
//            boolean first = true;
//            List<CropBedSection> cropType;
//
//            List<BedDto> bdeDtoList = new ArrayList<>();
//
//            for (Bed bed1 : bedListEntity) {
//                BedDto bedDto = new BedDto();        // has cropType1 and cropType2
////    			if(first) {
//                bedDto.setId(bed1.getId());
//                bedDto.setBedName(bed1.getBedName());
//
//                CropBedSection c1 = bed1.getCropBed().get(0);
//                bedDto.setCropId1(c1.getCrop().getId());
//                bedDto.setCropName(c1.getCrop().getName());
//                bedDto.setO1(c1.getO1());
//                bedDto.setO2(c1.getO2());
//                bedDto.setS1(c1.getS1());
//                bedDto.setStretch(c1.getStretch());
//
////    				first = false;
////    			} else {
//                int len = bed1.getCropBed().size();
//                if (len <= 1) {
//                    bdeDtoList.add(bedDto);
//                    continue;
//                }
////    				if(bed1 == null || bed1.getCropBed() == null || len < 1) {
////    					continue;
////    				}
//
//                CropBedSection c2 = bed1.getCropBed().get(1);
//                bedDto.setOptionalCropType(c2.getCrop().getId());
//                bedDto.setOptionalO1(c2.getO1());
//                bedDto.setOptionalO2(c2.getO2());
//                bedDto.setOptionalS1(c2.getS1());
//                bedDto.setOptionalStretch(c2.getStretch());
//                bdeDtoList.add(bedDto);
////    				first = true;
////    			}
//            }
//            cropBedSections.put(cycle.getId(), bdeDtoList);
//            // set bedDto in bedDtoList
//
////    		map.set(1, cycle);
//            // now set bedDto in response !
//
//        }
//
//        for (CyclesResponseDto cycle : response.getCycles()) {
//
//            if (cropBedSections.get(cycle.getId()) != null) {
//                cycle.setCycleBedDetails(cropBedSections.get(cycle.getId()));
//            }
//            Optional<Cycles> cropP = cropParameters.getCycles().stream().
//                    filter(e -> e.getId().equals(cycle.getId())).findAny();
//            if (cropP.isPresent()) {
//                cycle.setCycleName(cropP.get().getName());
//                cycle.setCycleStartDate(cropP.get().getStartDate());
//
//            }
//        }
//
//
//        return response;
//    }
//
//    public CropParametersResponseDto entityToCropParametersResponseDto(CropParameters cropParameters, Long runId) {
//
//        CropParametersResponseDto cropParametersDto = new CropParametersResponseDto();
//        if (cropParameters == null) {
////			cropParametersDto.set(runId);
//
//            return cropParametersDto;
//        }
//
//        cropParametersDto = this.modelMapper.map(cropParameters,
//                CropParametersResponseDto.class);
//        cropParametersDto.setId(cropParameters.getId());
//
//        List<Cycles> cycles = cropParameters.getCycles();
//        List<CyclesResponseDto> cycleResposeDto = cycles.stream()
//                .map(cycle -> {
//                    CyclesResponseDto dto = new CyclesResponseDto();
//                    dto.setCycleName(cycle.getName());
//                    dto.setId(cycle.getId());
//                    dto.setCycleStartDate(cycle.getStartDate());
//                    dto.setInterBedPattern(cycle.getInterBedPattern());
//                    List<BedDto> bedDtos = cycle.getBeds().stream()
//                            .map(bed -> {
//                                BedDto bedDto = new BedDto();
//                                bedDto.setId(bed.getId());
//                                if (!bed.getCropBed().isEmpty()) {
//                                    int len = bed.getCropBed().size();
//                                    CropBedSection cropBedSection = bed.getCropBed().get(0); // assuming first CropBedSection is the main one
//                                    bedDto.setCropId1(cropBedSection.getCrop().getId());
//                                    bedDto.setCropName(cropBedSection.getCrop().getName());
//                                    bedDto.setO1(cropBedSection.getO1());
//                                    bedDto.setS1(cropBedSection.getS1());
//                                    bedDto.setO2(cropBedSection.getO2());
//                                    bedDto.setStretch(cropBedSection.getStretch());
//                                    bedDto.setBedName(bed.getBedName());
//                                    if (len > 1) {
//                                        CropBedSection c2 = bed.getCropBed().get(1);
//                                        bedDto.setOptionalCropType(c2.getCrop().getId());
//                                        bedDto.setOptionalCropName(c2.getCrop().getName());
//                                        bedDto.setOptionalO1(c2.getO1());
//                                        bedDto.setOptionalO2(c2.getO2());
//                                        bedDto.setOptionalS1(c2.getS1());
//                                    }
//                                }
////								bedDto.setWidth(bed.getWidth());
////								bedDto.setHeight(bed.getHeight());
//                                // Set other BedDto fields as necessary
//                                return bedDto;
//                            })
//                            .collect(Collectors.toList());
//
//                    dto.setCycleBedDetails(bedDtos);
//                    return dto;
//                })
//                .collect(Collectors.toList());
//
//        cropParametersDto.setCycles(cycleResposeDto);
////		cropParametersDto.setRunId(runId);
//        return cropParametersDto;
//    }


    // new model mapper  manual mapping

    // Convert CropParameterRequestDto to CropParameters entity
    public CropParameters cropParameterRequestDtoToEntity(CropParameterRequestDto cropParameterRequestDto) {
        CropParameters cropParameters = new CropParameters();

        // Manually map the fields from DTO to entity (Assume appropriate setters are present in CropParameters)
        cropParameters.setId(cropParameterRequestDto.getId());
        // Add other fields as required

        return cropParameters;
    }

    // Convert CropParameters entity to CropParametersResponseDto
    public CropParametersResponseDto entityToCropParameterResponseDto(CropParameters cropParameters) {
        CropParametersResponseDto response = new CropParametersResponseDto();

        // Manual field mappings for nested objects
        response.setId(cropParameters.getId());

        // Map 'runId', 'cloneId', 'isMaster' from Run object, if present
        if (cropParameters.getRun() != null) {
            response.setRunId(cropParameters.getRun().getRunId());
            response.setCloneId(cropParameters.getRun().getCloneId());
            response.setIsMaster(cropParameters.getRun().isMaster());
        }

        // Manually map the projectId from Project entity, if present
        if (cropParameters.getProject() != null) {
            response.setProjectId(cropParameters.getProject().getProjectId());
        }

        // Manually map the Cycles list
        List<CyclesResponseDto> cycleResposeDtoList = new ArrayList<>();

        for (Cycles cycle : cropParameters.getCycles()) {
            CyclesResponseDto cycleDto = new CyclesResponseDto();
            cycleDto.setId(cycle.getId());
            cycleDto.setCycleName(cycle.getName());
            cycleDto.setCycleStartDate(cycle.getStartDate());
            cycleDto.setInterBedPattern(cycle.getInterBedPattern());

            // Map bed details
            List<BedDto> bedDtos = new ArrayList<>();
            for (Bed bed : cycle.getBeds()) {
                BedDto bedDto = new BedDto();
                bedDto.setId(bed.getId());
                bedDto.setBedName(bed.getBedName());

                // Map the first crop in the CropBedSection (assuming there are at least 1 crop per bed)
                if (!bed.getCropBed().isEmpty()) {
                    CropBedSection cropBedSection1 = bed.getCropBed().get(0);
                    bedDto.setCropId1(cropBedSection1.getCrop().getId());
                    bedDto.setCropName(cropBedSection1.getCrop().getName());
                    bedDto.setO1(cropBedSection1.getO1());
                    bedDto.setS1(cropBedSection1.getS1());
                    bedDto.setO2(cropBedSection1.getO2());
                    bedDto.setStretch(cropBedSection1.getStretch());

                    // Map optional second crop, if present
                    if (bed.getCropBed().size() > 1) {
                        CropBedSection cropBedSection2 = bed.getCropBed().get(1);
                        bedDto.setOptionalCropType(cropBedSection2.getCrop().getId());
                        bedDto.setOptionalCropName(cropBedSection2.getCrop().getName());
                        bedDto.setOptionalO1(cropBedSection2.getO1());
                        bedDto.setOptionalO2(cropBedSection2.getO2());
                        bedDto.setOptionalS1(cropBedSection2.getS1());
                        bedDto.setOptionalStretch(cropBedSection2.getStretch());
                    }
                }

                bedDtos.add(bedDto);
            }
            cycleDto.setCycleBedDetails(bedDtos);
            cycleResposeDtoList.add(cycleDto);
        }
        cycleResposeDtoList.sort(Comparator.comparing(CyclesResponseDto::getCycleStartDate));
        response.setCycles(cycleResposeDtoList);
        return response;
    }

    // Alternate method with additional parameter (runId)
    public CropParametersResponseDto entityToCropParametersResponseDto(CropParameters cropParameters, Long runId) {
        CropParametersResponseDto response = new CropParametersResponseDto();

        if (cropParameters != null) {
            response.setId(cropParameters.getId());
            // Map 'runId', 'cloneId', 'isMaster' from Run object, if present
            if (cropParameters.getRun() != null) {
                response.setRunId(cropParameters.getRun().getRunId());
                response.setCloneId(cropParameters.getRun().getCloneId());
                response.setIsMaster(cropParameters.getRun().isMaster());
            }
//            response.setRunId(runId); // Assign the runId passed as a parameter

            // Map the projectId from Project entity, if present
            if (cropParameters.getProject() != null) {
                response.setProjectId(cropParameters.getProject().getProjectId());
            }

            // Manually map the Cycles list
            List<CyclesResponseDto> cycleResposeDtoList = new ArrayList<>();
            for (Cycles cycle : cropParameters.getCycles()) {
                CyclesResponseDto cycleDto = new CyclesResponseDto();
                cycleDto.setId(cycle.getId());
                cycleDto.setCycleName(cycle.getName());
                cycleDto.setCycleStartDate(cycle.getStartDate());
                cycleDto.setInterBedPattern(cycle.getInterBedPattern());

                // Map bed details
                List<BedDto> bedDtos = new ArrayList<>();
                for (Bed bed : cycle.getBeds()) {
                    BedDto bedDto = new BedDto();
                    bedDto.setId(bed.getId());
                    bedDto.setBedName(bed.getBedName());


                    // Map first crop in CropBedSection
                    if (!bed.getCropBed().isEmpty()) {
                        CropBedSection cropBedSection1 = bed.getCropBed().get(0);
                        bedDto.setCropId1(cropBedSection1.getCrop().getId());
                        bedDto.setCropName(cropBedSection1.getCrop().getName());
                        bedDto.setO1(cropBedSection1.getO1());
                        bedDto.setS1(cropBedSection1.getS1());
                        bedDto.setO2(cropBedSection1.getO2());
                        bedDto.setStretch(cropBedSection1.getStretch());

                        // Optional second crop, if exists
                        if (bed.getCropBed().size() > 1) {
                            CropBedSection cropBedSection2 = bed.getCropBed().get(1);
                            bedDto.setOptionalCropType(cropBedSection2.getCrop().getId());
                            bedDto.setOptionalCropName(cropBedSection2.getCrop().getName());
                            bedDto.setOptionalO1(cropBedSection2.getO1());
                            bedDto.setOptionalO2(cropBedSection2.getO2());
                            bedDto.setOptionalS1(cropBedSection2.getS1());
                            bedDto.setOptionalStretch(cropBedSection2.getStretch());
                        }
                    }

                    bedDtos.add(bedDto);
                }
                cycleDto.setCycleBedDetails(bedDtos);
                cycleResposeDtoList.add(cycleDto);
            }
            cycleResposeDtoList.sort(Comparator.comparing(CyclesResponseDto::getCycleStartDate));
            response.setCycles(cycleResposeDtoList);
        }

        return response;
    }
}






