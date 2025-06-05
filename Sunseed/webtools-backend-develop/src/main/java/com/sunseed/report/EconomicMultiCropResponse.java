package com.sunseed.report;

import java.time.Instant;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class EconomicMultiCropResponse {
    private Long id;

    private Long cropId;

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

