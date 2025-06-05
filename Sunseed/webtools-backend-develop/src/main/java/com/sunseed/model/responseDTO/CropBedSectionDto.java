package com.sunseed.model.responseDTO;

import java.time.Instant;

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
public class CropBedSectionDto {
	
	private Long id;
//    private CropDto crop;
	private Long cropType;
    
    private Long o1;
    private Long s1;
    private Long o2;
    
    private Instant createdAt;
    private Instant updatedAt;
}