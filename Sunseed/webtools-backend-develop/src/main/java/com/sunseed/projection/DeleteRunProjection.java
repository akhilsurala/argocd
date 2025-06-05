package com.sunseed.projection;

import com.sunseed.enums.RunStatus;

public record DeleteRunProjection(
		Long id,
		RunStatus runStatus,
		Boolean isMaster,
	    Boolean variantExist
		) {
	
}
