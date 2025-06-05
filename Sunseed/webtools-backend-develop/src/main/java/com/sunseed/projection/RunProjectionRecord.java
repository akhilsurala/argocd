package com.sunseed.projection;

import java.time.Instant;

import com.sunseed.entity.AgriGeneralParameter;
import com.sunseed.entity.CropParameters;
import com.sunseed.entity.EconomicParameters;
import com.sunseed.entity.PreProcessorToggle;
import com.sunseed.entity.PvParameter;
import com.sunseed.entity.SimulatedRun;
import com.sunseed.enums.RunStatus;

public record RunProjectionRecord(
    Long id,
    String runName,
    Long projectId,
    PreProcessorToggle preProcessorToggle,
    PvParameter pvParameters,
    CropParameters cropParameters,
    AgriGeneralParameter agriGeneralParameters,
    EconomicParameters economicParameters,
    RunStatus runStatus,
    SimulatedRun simulatedRun,  // Assuming this is the ID from the SimulatedRun entity
    Instant createdAt,
    Instant updatedAt,
    Long cloneId,
    Boolean isMaster,
    boolean agriControl,
    boolean pvControl,
    Boolean variantExist
) {}
