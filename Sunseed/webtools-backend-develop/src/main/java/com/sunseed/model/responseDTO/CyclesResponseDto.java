package com.sunseed.model.responseDTO;

import java.time.LocalDate;
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
public class CyclesResponseDto {
	
	private Long id;
	private String cycleName;
    private LocalDate cycleStartDate;
    private List<BedDto> cycleBedDetails;
//    private List<BedResponseDto> cycleBedDetails2;

    private List<String> interBedPattern;

}