package com.sunseed.model.responseDTO;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

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
public class CyclesDto {

    private Long id;
    private String cycleName;

    private LocalDate cycleStartDate;

    private List<BedDto> cycleBedDetails;

    private List<String> interBedPattern;
    private List<String> weeks;
    private Set<CropDto> crops;
}