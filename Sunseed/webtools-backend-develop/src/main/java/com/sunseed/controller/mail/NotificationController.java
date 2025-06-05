package com.sunseed.controller.mail;

import com.sunseed.config.MyWebSocketHandler;
import com.sunseed.model.requestDTO.MarkAsSeenRequest;
import com.sunseed.model.responseDTO.NotificationResponseDto;
import com.sunseed.response.ApiResponse;
import com.sunseed.service.NotificationService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1")
public class NotificationController {

    private final ApiResponse apiResponse;
    private final NotificationService notificationService;
    private final MyWebSocketHandler webSocketHandler;

    @PostMapping("/notification/status")
    public ResponseEntity<Object> chnageNotificationSeenStatus(@RequestBody MarkAsSeenRequest request, HttpServletRequest httpRequest) {
        Long userId = (Long) httpRequest.getAttribute("userId");

        String res = notificationService.changeMarkAsReadStatus(request, userId);
        return apiResponse.ResponseHandler(true, "success", HttpStatus.OK, res);

    }

    @GetMapping("/notification/{userProfileId}")
    public ResponseEntity<Object> getAllNotificationByUserId(@PathVariable Long userProfileId) {
        List<NotificationResponseDto> allNotification = notificationService.getAllNotificationByUserProfileId(userProfileId);
        return apiResponse.ResponseHandler(true, "success.list", HttpStatus.OK, allNotification);
    }


    // only for testing
    @GetMapping("/start-process/{username}")
    public String startProcess(@PathVariable String username) {
        System.out.println("username in start process controller :" + username);
        try {
            String message = "hello" + username;
            webSocketHandler.sendNotificationToUser(username, message);
            Thread.sleep(3000);
            webSocketHandler.sendNotificationToUser(username, "Bye" + username);

            return "Process started for user: " + username;
        } catch (Exception e) {
            return "Error starting process: " + e.getMessage();
        }
    }

}

