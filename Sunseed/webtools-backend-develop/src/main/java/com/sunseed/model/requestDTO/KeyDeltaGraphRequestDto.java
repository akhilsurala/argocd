package com.sunseed.model.requestDTO;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class KeyDeltaGraphRequestDto {
    
    private List<Long> runId;
    private List<Long> cropId;
    private String typeGraph;
    private Boolean isAbsolute;
}
