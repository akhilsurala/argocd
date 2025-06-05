package com.sunseed.model.responseDTO;

import java.time.Instant;

import com.sunseed.entity.Crop;

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
public class CropDto {
	
	private Long id;
    private String name;
    private Crop crop;
    
    private Instant createdAt;
    private Instant updatedAt;
}