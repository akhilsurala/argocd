package com.sunseed.projection;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SceneType {

    private String startTime;
    private String week;

    @JsonProperty("geometry")
    private SceneDetails geometry;

    @JsonProperty("material")
    private SceneDetails material;

    @JsonProperty("carbon_assimilation")
    private SceneDetails carbonAssimilation;

    @JsonProperty("temperature")
    private SceneDetails temperature;

    @JsonProperty("radiation")
    private SceneDetails radiation;
    
    @JsonProperty("dli_output")
    private SceneDetails dliOutput;
}
