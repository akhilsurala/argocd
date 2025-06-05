package com.sunseed.model.responseDTO;

import java.time.LocalDate;

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
public class BedResponseDto {
	private Long id;
	private LocalDate startDate;
    private String bedName;
    private Long cropId1;
    private Long o1;
    private Long s1;
    private Long o2;
    private Long optionalCropType;
    private Long optionalO1;
    private Long optionalS1;
    private Long optionalO2;
}
