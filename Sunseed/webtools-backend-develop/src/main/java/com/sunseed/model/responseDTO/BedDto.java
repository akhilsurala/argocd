package com.sunseed.model.responseDTO;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BedDto {

    private Long id;

    //	private List<CropBedSectionDto> cropBedSections;
    private String bedName;
    private Long cropId1;
    private String cropName;
    private Double o1;
    private Double s1;
    private Double o2;

    private Long optionalCropType;
    private String optionalCropName;
    private Double optionalO1;
    private Double optionalS1;
    private Double optionalO2;

    private Double stretch;
    private Double optionalStretch;
//	private CropTypeResponse cropType;
}