package com.sunseed.model.requestDTO;

import java.time.LocalDate;
import java.util.List;

import com.sunseed.model.ValidationGroups;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class CyclesRequestDto {

    private Long id;

    @NotNull(message = "field.required.cycleName", groups = ValidationGroups.CropParametersGroup.class)
//	private String name;
    private String cycleName;
    @NotNull(message = "field.required.cycleStartDate", groups = ValidationGroups.CropParametersGroup.class)
//	private Instant startDate;
    private LocalDate cycleStartDate;

    @NotNull(message = "field.required.cycleBedDetails", groups = ValidationGroups.CropParametersGroup.class)
//	List<BedRequestDto> beds;
    private List<@Valid BedRequestDto> cycleBedDetails;

    private List<Long> deletedBedDetailsId;

//    @NotNull(message = "field.required.interBedPattern", groups = ValidationGroups.CropParametersGroup.class)
//    @Size(message = "bedPattern.size", min = 2, max = 15, groups = ValidationGroups.CropParametersGroup.class)
    private List<String> interBedPattern;
}