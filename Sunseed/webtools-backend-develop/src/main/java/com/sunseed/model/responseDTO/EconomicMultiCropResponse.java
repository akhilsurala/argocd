package com.sunseed.model.responseDTO;

import lombok.*;

import java.time.Instant;

import com.sunseed.entity.Crop;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class EconomicMultiCropResponse {
    private Long id;

    private Long cropId;
    
    private Crop crop;

    private Double minReferenceYieldCost;

    private Double maxReferenceYieldCost;

    private Double minInputCostOfCrop;

    private Double maxInputCostOfCrop;

    private Double minSellingCostOfCrop;

    private Double maxSellingCostOfCrop;
    private Double cultivationArea;

    private Instant createdAt;
    private Instant updatedAt;
}
