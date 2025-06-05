package com.sunseed.serviceImpl;

import com.sunseed.config.MyWebSocketHandler;
import com.sunseed.entity.Notification;
import com.sunseed.entity.UserProfile;
import com.sunseed.exceptions.ResourceNotFoundException;
import com.sunseed.exceptions.UnAuthorizedException;
import com.sunseed.model.requestDTO.MarkAsSeenRequest;
import com.sunseed.model.responseDTO.NotificationResponseDto;
import com.sunseed.repository.NotificationRepository;
import com.sunseed.repository.UserProfileRepository;
import com.sunseed.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserProfileRepository userProfileRepo;
    private final UserProfileRepository userProfileRepository;
    private final MyWebSocketHandler myWebSocketHandler;


    @Override
    public String changeMarkAsReadStatus(MarkAsSeenRequest request, Long userId) {
    	if (request.getNotificationIds().isEmpty() || request.getNotificationIds().size() < 1) {
            if (userId == null || userId <= 0)
                throw new UnAuthorizedException(null, "user.not.found");

            Optional<UserProfile> userProfile = userProfileRepository.findByUserId(userId);

            if (userProfile.isEmpty())
                throw new ResourceNotFoundException("user.not.found");

            List<Notification> allUnreadNotification = notificationRepository. findUnreadNotificationsByDestinationId(userProfile.get().getUserProfileId());
            allUnreadNotification.forEach((notification) -> {
                notification.setMarkAsRead(Boolean.TRUE);
                notificationRepository.save(notification);
            });


        } else {
            List<Long> notificationIds = request.getNotificationIds();

            notificationIds.forEach((notificationId) -> {
                Notification notification = notificationRepository.findById(notificationId).orElseThrow(() -> new ResourceNotFoundException("notification.not.found"));
                notification.setMarkAsRead(Boolean.TRUE);
                notificationRepository.save(notification); // Save the updated notification
            });
        }
        return "notification status changed successfully";

    }

    @Override
    public List<NotificationResponseDto> getAllNotificationByUserProfileId(Long userProfileId) {
        UserProfile userProfile = userProfileRepo.findByUserProfileId(userProfileId).orElseThrow(() -> new ResourceNotFoundException("user.not.found"));
        List<Notification> allNotificationByUser = notificationRepository.getAllNotificationsForUserById(userProfileId);
        List<NotificationResponseDto> notificationResponse = allNotificationByUser.stream().map((notification) -> {
            NotificationResponseDto notificationResponseDto = NotificationResponseDto.builder().id(notification.getId()).message(notification.getMessage()).markAsRead(notification.getMarkAsRead()).createdAt(notification.getCreatedAt()).link(notification.getLink()).isSuccess(notification.getIsSuccess()).build();
            return notificationResponseDto;
        }).collect(Collectors.toList());
//        Long countOfNotification = notificationRepository.countByDestinationId(userProfile.getEmailId());
//        System.out.println(countOfNotification);
        return notificationResponse;
    }

    @Override
    public NotificationResponseDto saveNotification(String message, Long destinationId, Long senderId,Boolean success) {
        UserProfile receiver = userProfileRepo.findByUserProfileId(destinationId).orElseThrow(() -> new ResourceNotFoundException("user.not.found"));
        UserProfile sender = userProfileRepo.findByUserProfileId(senderId).orElseThrow(() -> new ResourceNotFoundException("user.not.found"));
        Notification notification = new Notification();
        notification.setLink("/analysis-manager");
        notification.setDestination(receiver);
        notification.setSource(sender);
        notification.setMessage(message);
        notification.setIsSuccess(success);

        Notification savedNotification = notificationRepository.save(notification);

        NotificationResponseDto notificationResponseDto = NotificationResponseDto.builder().id(savedNotification.getId()).message(savedNotification.getMessage()).link(savedNotification.getLink()).createdAt(savedNotification.getCreatedAt()).markAsRead(savedNotification.getMarkAsRead()).updatedAt(savedNotification.getUpdatedAt()).build();
        // send count of notification
        myWebSocketHandler.sendNotificationCountToUser(receiver.getEmailId());

        return notificationResponseDto;
    }


}

