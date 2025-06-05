package com.sunseed.model.requestDTO;

import com.sunseed.entity.SoilType;
import com.sunseed.enums.PreProcessorStatus;
import com.sunseed.enums.Toggle;
import lombok.*;

import java.time.Instant;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class PreProcessorToggleRequestDto {
    private Long id;
    private Toggle toggle;
    private Double pitchOfRows;
    private Double azimuth;
    private Double lengthOfOneRow;
    private PreProcessorStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private SoilType soilType;

}
//"id":1,
//            "toggle":"Only Agri",
//            "pitchOfRows":2,
//            "azimuth":0,
//            "lengthOfOneRow":5,
//            "status":"created",
//            "createdAt":"2024-06-07T14:46:55.50312",
//            "updatedAt":"2024-06-07T14:47:53.270578"