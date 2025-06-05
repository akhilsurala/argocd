package com.sunseed.model.responseDTO;

import com.sunseed.enums.PreProcessorStatus;
import com.sunseed.enums.RunStatus;
import com.sunseed.enums.Toggle;
import lombok.*;

import java.time.LocalDate;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class SimulationResponseDto {
    private Long id;
    private Long userProfileId;
    private Long projectId;
    private Long runId;
 //   private RunStatus status;
    private Long taskCount;
    private Long completedTaskCount;
    private Boolean withTracking;
    private Toggle simulationType;
    private Map<String, Object> runPayload;
    private LocalDate startDate;
    private LocalDate endDate;
    private String errorMessage;
}
