package com.sunseed.model.requestDTO;

import java.util.List;

import com.sunseed.model.ValidationGroups;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class CropParameterRequestDto {

    private Long id;
   // @Size(message = "cycles.size", min = 1, max = 3, groups = ValidationGroups.CropParametersGroup.class)
    private List<@Valid CyclesRequestDto> cycles;
//    private Long[] masterCycleId;
    private List<Long> deletedCyclesId;

}