package com.sunseed.service;

import com.sunseed.model.requestDTO.MarkAsSeenRequest;
import com.sunseed.model.responseDTO.NotificationResponseDto;

import java.util.List;

public interface NotificationService {
    String changeMarkAsReadStatus(MarkAsSeenRequest request, Long userId);

    List<NotificationResponseDto> getAllNotificationByUserProfileId(Long userProfileId);

    NotificationResponseDto saveNotification(String message, Long destinationId, Long senderId,Boolean success);

}
