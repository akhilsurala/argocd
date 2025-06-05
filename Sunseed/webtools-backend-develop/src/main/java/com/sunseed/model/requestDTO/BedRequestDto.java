package com.sunseed.model.requestDTO;

import com.sunseed.model.ValidationGroups;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class BedRequestDto {
    private Long id;        // TODO change to bedId
    private Long optionalCropBedId;

    private String bedName;

    @NotNull(message = "field.required.cropId", groups = ValidationGroups.CropParametersGroup.class)
    // @NotBlank(message = "field.required", groups = ValidationGroups.CropParametersGroup.class)
    private Long cropId1;
    @NotNull(message = "field.required.o1", groups = ValidationGroups.CropParametersGroup.class)
    //@NotBlank(message = "field.required", groups = ValidationGroups.CropParametersGroup.class)
    private Double o1;
    // @NotBlank(message = "field.required", groups = ValidationGroups.CropParametersGroup.class)
    @NotNull(message = "field.required.s1", groups = ValidationGroups.CropParametersGroup.class)
    private Double s1;
    //  @NotBlank(message = "field.required", groups = ValidationGroups.CropParametersGroup.class)
    @NotNull(message = "field.required.o2", groups = ValidationGroups.CropParametersGroup.class)
    private Double o2;

    @DecimalMin(value = "0", message = "stretch.smaller")
    @DecimalMax(value = "100", message = "stretch.greater")
    private Double stretch;
//	private List<Long> deletedCropBedSectionIds;

    // optional crop bed
//	private Long cropId2;
    private Long optionalCropType;

    //	private Long o12;
    private Double optionalO1;
    //	private Long s12;
    private Double optionalS1;
    //	private Long o22;
    private Double optionalO2;
    private Double optionalStretch;

}