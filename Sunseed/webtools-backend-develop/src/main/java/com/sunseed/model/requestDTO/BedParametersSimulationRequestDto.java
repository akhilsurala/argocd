package com.sunseed.model.requestDTO;

import lombok.*;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
public class BedParametersSimulationRequestDto {
    private Double noOfBeds;
    private Double bedWidth;
    private Double bedHeight;
    private Double bedAngle;
    private Double bedAzimuth;

}
//"noOfBeds":1,
//            "bedAzimuth":0,
//            "bedAngle":60,
//            "bedWidth":600.0,
//            "bedHeight":100.0