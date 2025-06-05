package com.sunseed.model.requestDTO;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.sunseed.model.ValidationGroups;

import jakarta.validation.constraints.*;
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
public class AgriPvProtectionHeightRequestDto {

    private Long agriPvProtectionHeightId;

    @JsonProperty("Deleted")
    @Builder.Default
    private boolean Deleted = false;

    @NotNull(message = "select.value", groups = ValidationGroups.AgriGeneralParametersGroup.class)
    private Long protectionId;

    @NotNull(message = "field.required", groups = ValidationGroups.AgriGeneralParametersGroup.class)
    @DecimalMin(value = "0.0", message = "height.range", groups = ValidationGroups.AgriGeneralParametersGroup.class)
    @DecimalMax(value = "10.0", message = "height.range", groups = ValidationGroups.AgriGeneralParametersGroup.class)
    private Double height;
}
