package com.sunseed.simtool.embeddables;

import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Embeddable
public class OSInfo{
	private String os_name;
	private String os_version;
	private String os_image;
	private String os_category;
}
