package com.sunseed.enums;

import com.sunseed.exceptions.InvalidDataException;

public enum ModeOfOperations {
    FIXED_TILT("Fixed Tilt"),
    SINGLE_AXIS_TRACKING("Single Axis Tracking");

    private final String value;

    ModeOfOperations(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static ModeOfOperations fromString(String text) {
        for (ModeOfOperations mode : ModeOfOperations.values()) {
            if (mode.value.equalsIgnoreCase(text)) {
                return mode;
            }
        }
        throw new InvalidDataException("invalid.modeOfOperation");
    }
}
