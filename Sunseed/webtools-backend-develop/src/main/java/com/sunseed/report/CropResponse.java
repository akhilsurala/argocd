package com.sunseed.report;

import java.util.List;

import com.sunseed.entity.Cycles;
import com.sunseed.model.responseDTO.CropParametersResponseDto;
import com.sunseed.model.responseDTO.CyclesResponseDto;

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
public class CropResponse {


    private Long id;
    private Long runId;
    private Long cloneId;
    private Boolean isMaster;
    private Long projectId;
    private List<Cycles> cycles;

//    private List<CyclesDto> cycles;

}
