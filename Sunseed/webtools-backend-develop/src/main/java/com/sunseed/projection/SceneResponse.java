package com.sunseed.projection;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SceneResponse {
	
    private Boolean isTracking = Boolean.FALSE;

    @JsonProperty("scenes")
    private List<SceneType> scenes;
    private List<Months> months;
    private List<String> weekIntervals;
    private List<ControlPanel> controlPanel;
    Map<String, Object> simulationGroundArea;

}
