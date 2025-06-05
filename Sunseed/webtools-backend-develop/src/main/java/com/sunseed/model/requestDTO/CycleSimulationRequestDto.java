package com.sunseed.model.requestDTO;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class CycleSimulationRequestDto {
    //  private LocalDate cycleStartDate;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate cycleStartDate;
    private String cycleName;
    private Integer duration;
    private List<String> interBedPattern;
    private List<CycleBedDetailsSimulationDto> cycleBedDetails;
}
