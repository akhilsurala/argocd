package com.sunseed.model.responseDTO;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostProcessingDetailsResponse {
private List<Double> b1C1Sunlit;
private List<Double> b1C1Shaded;
private List<Double> b1AllSunlit;
private List<Double> b2AllSunlit;
private List<Double> allAllAll;
}
