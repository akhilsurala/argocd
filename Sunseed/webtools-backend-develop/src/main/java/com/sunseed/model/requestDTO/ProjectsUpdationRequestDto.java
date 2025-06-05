package com.sunseed.model.requestDTO;

import java.util.List;

import com.sunseed.model.Coordinates;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
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
public class ProjectsUpdationRequestDto {

    @NotBlank(message = "project.name.empty")
    @Size(min = 5, message = "project.name.tooShort")
    @Size(max = 30, message = "project.name.tooLong")
    @Pattern(regexp = "^[a-zA-Z][a-zA-Z0-9 ]*$", message = "project.name.invalid")
    private String projectName;

    private String latitude;

    private String longitude;

    private List<Coordinates> polygonCoordinates;

    private Double area;

    private String comments;
    private Double[] offsetPoint;

    private List<Long> runIds;
}
