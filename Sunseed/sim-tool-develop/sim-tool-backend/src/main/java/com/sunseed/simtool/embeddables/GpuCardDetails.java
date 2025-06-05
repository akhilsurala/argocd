package com.sunseed.simtool.embeddables;

import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Embeddable
public class GpuCardDetails{

	private String CARD_NAME;
	private long TEMPLATE_ID;
	private String MEMORY_UNIT;
	private long MEMORY;
	private String CARD_TYPE;
}
