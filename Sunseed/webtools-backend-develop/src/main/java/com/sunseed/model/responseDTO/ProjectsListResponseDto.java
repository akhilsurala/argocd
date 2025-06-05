package com.sunseed.model.responseDTO;

import java.time.Instant;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ProjectsListResponseDto {

    private Long projectId;
    private String projectName;
    private String latitude;
    private String longitude;
    private Instant createdOn;
    private Instant lastEdited;
    private Long numberOfRuns;
    private String comments;
    private String location;
    private List<Long> runIds;
    private Double[] offsetPoint;
}
