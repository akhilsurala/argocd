package com.sunseed.model.responseDTO;

import lombok.*;

import java.util.List;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PostProcessingDetailsResponseDto {
    private Long id;
    private String runName;
    private List<PostCycleResponse> cycles;

    private List<String> weeks;
    private List<String> beds;
    private Set<String> crops;

    private List<String> pvWeeks;
    private Set<String> cropName;
    private Set<String> controlCropName;
    private List<String> weekIntervals;
// private Set<CropDto> crops;
}
