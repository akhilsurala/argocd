package com.sunseed.model.responseDTO;

import com.sunseed.enums.Toggle;
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
public class RunNameListResponseDto {

	private Long id;
	private String name;
	private Toggle toggle;
}
