package com.sunseed.model.requestDTO;

import com.sunseed.model.responseDTO.SimulationResponseDto;
import lombok.*;

import java.util.List;
import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class SimulationRequest {
    private Long userProfileId;
    private Long projectId;
 //   private Map<String, Object> runPayload;
    private List<RunSimulationRequestDto> runPayload;

}

//{
//   "userProfileId":1,
//   "projectId":2,
//   "runPayload":[
//
//   ]
//}