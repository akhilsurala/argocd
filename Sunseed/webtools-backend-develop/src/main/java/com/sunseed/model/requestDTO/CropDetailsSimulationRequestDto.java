package com.sunseed.model.requestDTO;
import java.util.Date;

import com.sunseed.entity.FarquharParameter;
import com.sunseed.entity.OpticalProperty;
import com.sunseed.entity.StomatalParameter;

import jakarta.persistence.Column;
import lombok.*;


@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class CropDetailsSimulationRequestDto {
    private Double f1;
    private Double f2;
    private Double f3;
    private Double f4;
    private Double f5;
    private Double o1;
    private Double o2;
    private Double s1;
    private String cropName;
    private Integer duration;
    private Long maxStage;
    private Long minStage;
    private Long harvestDays;
    private Long requiredDLI;
    private Long requiredPPFD;
    private OpticalProperty opticalProperty;
    private FarquharParameter farquharParameter;
    private StomatalParameter stomatalParameter;
    private String cropLabel;
    
    private Boolean hasPlantActualDate;
    private String plantActualStartDate;
    private Integer plantMaxAge;
    private Integer maxPlantsPerBed;

}
