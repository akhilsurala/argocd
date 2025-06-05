package com.sunseed.projection;

import lombok.*;

import java.util.List;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ControlPanel {
    private String cycleName;
    private Map<String,Object> weeks;

}
