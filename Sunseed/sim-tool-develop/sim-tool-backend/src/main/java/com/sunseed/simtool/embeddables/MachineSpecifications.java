package com.sunseed.simtool.embeddables;

import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Embeddable
public class MachineSpecifications{

	private double RAM;
	private int CPU;
	private long disk_space;
	private double price_per_month;
	private double price_per_hour;
	
}
