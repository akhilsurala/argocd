package com.sunseed.model.requestDTO;

import com.sunseed.enums.PreProcessorStatus;
import com.sunseed.enums.RunStatus;
import lombok.*;

import java.time.Instant;
import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RunSimulationRequestDto {
    private Long id;
    private String latitude;
    private String longitude;
    private PreProcessorToggleRequestDto preProcessorToggles;
    private PvParametersSimulationRequestDto pvParameters;
    private CropParametersSimulationDto cropParameters;
    private RunStatus runStatus;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
//{
//         "id":23,
//         "latitude":"28.357567857801694",
//         "longitude":"77.03613281250001",
//         "preProcessorToggles":{
//         },
//         "pvParameters":{
//         },
//         "cropParameters":{
//           }
//         "runStatus":"holding",
//         "createdAt":"2024-06-07T14:47:53.258578",
//         "updatedAt":"2024-06-07T14:47:53.258578"
//      }