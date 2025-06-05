package com.sunseed.projection;

import java.math.BigDecimal;
import java.sql.Timestamp;

public record SceneList(String sceneType, BigDecimal min, BigDecimal max, String url, Timestamp date,
                        Long simulationTaskId) {

}
