package com.sunseed.charts;

import java.util.List;

import jakarta.validation.constraints.NotNull;
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
public class ChartsRequestDto {

	@NotNull(message = "runIdList.cant.be.empty")
	private List<Long> runIdList;
}
