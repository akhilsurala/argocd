package com.sunseed.model.responseDTO;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CropParametersResponseDto {


    private Long id;
    private Long runId;
    private Long cloneId;
    private Boolean isMaster;
    private Long projectId;
    private List<CyclesResponseDto> cycles;

//    private List<CyclesDto> cycles;

}