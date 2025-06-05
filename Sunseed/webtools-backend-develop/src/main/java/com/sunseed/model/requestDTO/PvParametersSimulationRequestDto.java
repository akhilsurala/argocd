package com.sunseed.model.requestDTO;

import com.sunseed.entity.ModeOfPvOperation;
import com.sunseed.entity.PvModule;
import com.sunseed.enums.PreProcessorStatus;
import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class PvParametersSimulationRequestDto {
    private Long id;
    private Long runId;
    private Long projectId;
    private Double tiltIfFt;
    private Double height;
    private Double maxAngleOfTracking;
    private String moduleMaskPattern;
    private Double gapBetweenModules;
    private PreProcessorStatus status;
//    private PvModuleSimulationDto pvModule;
    private PvModule pvModule;
    private ModeOfPvOperation modeOfOperationId;
    private Boolean tracking=Boolean.FALSE;
    private List<PvModuleConfigurationRequestDto> moduleConfigs; // eg: { {1,P1}, {2,P2}, {3,P1-P2}, {4,L1} }


}
//"id":1,
//            "runId":1,
//            "projectId":1,
//            "tiltIfFt":30,
//            "maxAngleOfTracking":20.0,
//            "moduleMaskPattern":"01",
//            "gapBetweenModules":1.1,
//            "height":2.5,
//            "status":"created",
//            "pvModule":{
//               "id":2,
//               "moduleType":"num inverters",
//               "length":1400,
//               "width":800,
//               "pdc0": 0.2,
//               "gamma_pdc":0.5,
//               "temp_ref":25
//            },
//            "modeOfOperationId":{
//               "id":2,
//               "modeOfOperation":"single axis"
//            },
//"moduleConfigs":[
//               {
//                  "id":3,
//                  "moduleConfig":"1P"
//               }
//            ]