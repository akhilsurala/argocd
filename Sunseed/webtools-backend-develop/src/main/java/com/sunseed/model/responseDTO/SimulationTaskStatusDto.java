package com.sunseed.model.responseDTO;

import com.sunseed.enums.RunStatus;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Map;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class SimulationTaskStatusDto {
    private String status;
    private Long taskCount;
}
