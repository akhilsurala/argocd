package com.sunseed.model.requestDTO.masterTables;

import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PvModuleOpticalRequestDto {
	
	@NotNull(message = "type.not.null")
    private String type; // Expected values: "front" or "back"

    private Double reflectance_PAR;
    private Double reflectance_NIR;
    private Double transmissivity_PAR;
    private Double transmissivity_NIR;

//    private MultipartFile reflectanceFile; // File input for reflectance


}