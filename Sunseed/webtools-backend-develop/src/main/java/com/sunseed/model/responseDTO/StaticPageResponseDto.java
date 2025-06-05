package com.sunseed.model.responseDTO;

import com.sunseed.enums.PageType;
import lombok.*;

import java.time.Instant;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class StaticPageResponseDto {
    private Long id;
    private String title;
    private String description;
    private String summary;
    private PageType pageType;
    private Boolean hide;
    private Instant createdAt;
    private Instant updatedAt;
}