package com.sunseed.model.requestDTO;

import com.sunseed.entity.Crop;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class CycleBedDetailsSimulationDto {

    private String bedName;
    private List<CropDetailsSimulationRequestDto> cropDetails;
}
