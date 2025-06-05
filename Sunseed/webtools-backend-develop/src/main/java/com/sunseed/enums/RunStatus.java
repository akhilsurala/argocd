package com.sunseed.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.sunseed.deserializers.RunStatusDeserializer;

//@JsonDeserialize(using = RunStatusDeserializer.class)
public enum RunStatus {

    COMPLETED("completed"), CANCELLED("cancelled"), HOLDING("holding"), RUNNING("running"), FAILED("failed"), PAUSED("pause"), QUEUED("queued");

    private String value;

    private RunStatus(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    @JsonCreator
    public static RunStatus fromValue(String value) {
        for (RunStatus runStatus : RunStatus.values()) {
            if (runStatus.getValue().equalsIgnoreCase(value)) {
                return runStatus;
            }
        }
        throw new IllegalArgumentException("Invalid value for RunStatus: " + value);
    }
}
