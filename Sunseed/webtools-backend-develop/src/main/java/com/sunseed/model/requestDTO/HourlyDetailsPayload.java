package com.sunseed.model.requestDTO;

import lombok.*;

import java.lang.module.Configuration;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HourlyDetailsPayload {
    private List<Long> runIds;
    private List<String> cycles;
    private List<String> weeks;
    private List<String> beds;
    private List<String> crops;
    private List<String> configurations;
    private Integer from;
    private Integer to;
}
