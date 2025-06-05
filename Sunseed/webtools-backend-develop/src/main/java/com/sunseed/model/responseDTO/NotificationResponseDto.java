package com.sunseed.model.responseDTO;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationResponseDto {
    private Long id;
    private String message;

    private String link;
    @JsonProperty("isSuccess")
    private Boolean isSuccess;

    private Boolean markAsRead ;

    private Instant createdAt;

    private Instant updatedAt;


}
