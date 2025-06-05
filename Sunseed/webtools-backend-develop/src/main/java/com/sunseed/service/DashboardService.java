package com.sunseed.service;

import com.sunseed.model.responseDTO.DashBoardResponseDto;

public interface DashboardService {

	DashBoardResponseDto getDashboardDetails(Long userId);

}
