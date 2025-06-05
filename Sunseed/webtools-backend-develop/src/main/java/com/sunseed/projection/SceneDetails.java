package com.sunseed.projection;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SceneDetails {
    @JsonProperty("min")
    private BigDecimal min;

    @JsonProperty("max")
    private BigDecimal max;

    @JsonProperty("url")
    private String url;
}
