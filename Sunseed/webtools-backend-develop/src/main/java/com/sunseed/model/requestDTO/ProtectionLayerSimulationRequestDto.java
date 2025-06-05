package com.sunseed.model.requestDTO;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.sunseed.entity.OpticalProperty;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class ProtectionLayerSimulationRequestDto {
    private Double f1;
    private Double f2;
    private Double f3;
    private Double f4;
    private Boolean isActive;
    private Boolean hide;
    private String protectionLayerType;
    private Double height;
    private String polysheets;
    private Double voidPercentage;
    private String linkToTexture;
    private Double diffusionFraction;
    private Double transmissionPercentage;
    private OpticalProperty opticalProperty;


}
