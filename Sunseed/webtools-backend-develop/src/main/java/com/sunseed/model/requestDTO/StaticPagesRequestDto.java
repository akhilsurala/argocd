package com.sunseed.model.requestDTO;

import com.sunseed.enums.PageType;
import jakarta.validation.constraints.NotNull;
import lombok.*;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class StaticPagesRequestDto {
    private Long id;
    @NotNull(message = "title.notnull")
    private String title;
    @NotNull(message = "pageType.notnull")
    private PageType pageType;
    @NotNull(message = "description.notnull")
    private String description;
    @NotNull(message = "summary.notnull")
    private String summary;
    @NotNull(message="hide.notnull")
    private Boolean hide;
}