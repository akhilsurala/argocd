package com.sunseed.simtool.embeddables;

import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Embeddable
public class MonitorStatus {
    private String monitorStatus;
    private String monitorReason;
}