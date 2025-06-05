package com.sunseed.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;


public enum PageType {
    LICENSE_MANAGEMENT("license management"), DOCUMENTATION("documentation"), TUTORIAL("tutorial"),SUPPORT("support");

    private final String value;

    private PageType(String value) {
        this.value = value;
    }

    public String getValue()
    {
        return value;
    }

    @JsonCreator
    public static PageType fromString(String value) {
        if(value.equalsIgnoreCase("license management"))
            return LICENSE_MANAGEMENT;
        else if(value.equalsIgnoreCase("documentation"))
            return DOCUMENTATION;
        else if(value.equalsIgnoreCase("tutorial"))
            return TUTORIAL;
        else if(value.equalsIgnoreCase("support"))
            return SUPPORT;
        return LICENSE_MANAGEMENT ;
    }

    @JsonValue
    public String toJson()
    {
        return value;
    }

}