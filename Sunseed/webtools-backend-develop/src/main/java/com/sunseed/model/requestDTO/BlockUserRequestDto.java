package com.sunseed.model.requestDTO;

import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BlockUserRequestDto {

	@Pattern(regexp = "(?i)^(true|false)$", message = "invalid.isActive.value")
	private String isActive;

}