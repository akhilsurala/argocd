package com.sunseed.model.requestDTO;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.sunseed.entity.*;
import com.sunseed.enums.PreProcessorStatus;
import com.sunseed.enums.TempControl;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class CropParametersSimulationDto {
    private Long id;
    private Long runId;
    List<ProtectionLayerSimulationRequestDto> protectionLayer;
    List<CycleSimulationRequestDto> cycles;
    // private CropBedSection bedSection;
    @JsonProperty("isMulching")
    private boolean isMulching;
    private String irrigationType;
    private TempControl tempControl;
    private Double trail;
    private Double minTemp;
    private Double maxTemp;
    //   private Double protectionLayerHeight;
    private PreProcessorStatus status;
    private Double startPointOffset;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime simulationTimeOfYear;
//    private SoilType soilType;
    private Irrigation typeOfIrrigation;
    private BedParametersSimulationRequestDto bedParameter;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;


}


//"protectionLayer":{
//             "protectionLayerType":"sheet1",
//             "height":2
//           },
//            "cycles":[
//               {
//                 "cycleStartDate":"2024-01-01",
//                 "duration":90,
//                 "interBedPattern":["Bed 1","Bed 2"],
//
//                 "cycleBedDetails":[
//                   {
//                     "bedName":"Bed 1",
//                     "cropDetails":[{
//                       "duration": 60,
//                       "cropName":"sorghum",
//                        "minStage": 1,
//                        "maxStage": 5,
//                       "reflectivity_PAR":0.05,
//                       "reflectivity_NIR":2.0,
//                       "transmissivity_PAR":0.05,
//                       "transmissivity_NIR":2.0,
//                       "o1":2000,
//                     "o2":3000,
//                     "s1":4000
//                     },
//                     {"duration": 60,
//                       "cropName":"sorghum2",
//                        "minStage": 1,
//                        "maxStage": 5,
//                       "reflectivity_PAR":0.05,
//                       "reflectivity_NIR":2.0,
//                       "transmissivity_PAR":0.05,
//                       "transmissivity_NIR":2.0,
//                       "o1":2000,
//                     "o2":3000,
//                     "s1":4000
//                     }
//                     ]
//                   },
//                   {
//                     "bedName":"Bed 2",
//                     "cropDetails":[{
//                       "duration": 60,
//                       "cropName":"sorghum",
//                        "minStage": 1,
//                        "maxStage": 5,
//                       "reflectivity_PAR":0.05,
//                       "reflectivity_NIR":2.0,
//                       "transmissivity_PAR":0.05,
//                       "transmissivity_NIR":2.0,
//                       "o1":2000,
//                     "o2":3000,
//                     "s1":4000
//                     },
//                     {"duration": 60,
//                       "cropName":"sorghum2",
//                        "minStage": 1,
//                        "maxStage": 5,
//                       "reflectivity_PAR":0.05,
//                       "reflectivity_NIR":2.0,
//                       "transmissivity_PAR":0.05,
//                       "transmissivity_NIR":2.0,
//                       "o1":2000,
//                     "o2":3000,
//                     "s1":4000
//                     }
//                     ]
//                   }
//                 ]
//               }],
//            "id":1,
//            "runId":1,
//            "bedSection":{
//               "id":1,
//               "cropType":{
//                  "id":1,
//                  "cropName":"sorghum",
//                  "createdAt":null,
//                  "updatedAt":null
//               },
//               "offsetFromCentreLine":300,
//               "spacing":1000,
//               "offsetFromStart":500,
//               "createdAt":"2024-06-07T14:47:53.215578",
//               "updatedAt":"2024-06-07T14:47:53.215578"
//            },
//            "soilType":{
//               "id":1,
//               "soilType":"Clay",
//               "createdAt":null,
//               "updatedAt":null
//            },
//            "typeOfIrrigation":{
//               "id":2,
//               "irrigationType":"Sprinkle",
//               "createdAt":null,
//               "updatedAt":null
//            },
//           "bedParameter":{
//            "noOfBeds":1,
//            "bedAzimuth":0,
//            "bedAngle":60,
//            "bedWidth":600.0,
//            "bedHeight":100.0
//           },
//           "isMulching":true,
//           "irrigationType":"abc",
//            "protectionLayerHeight":3.3,
//            "status":"created",
//            "startPointOffset":500,
//            "simulationTimeOfYear":"2021-08-01T00:00:00",
//            "preProcessorToggles":{
//               "id":null,
//               "toggle":null,
//               "status":"draft",
//               "createdAt":null,
//               "updatedAt":null
//            },
//            "createdAt":"2024-06-07T14:47:53.210579",
//            "updatedAt":"2024-06-07T14:47:53.266583"
//         },