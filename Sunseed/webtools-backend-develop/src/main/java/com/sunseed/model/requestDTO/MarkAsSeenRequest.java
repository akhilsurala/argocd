package com.sunseed.model.requestDTO;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MarkAsSeenRequest {
    private List<Long> notificationIds;
    private Boolean markAllAsSeen;
}

