package com.sunseed.simtool.model.response;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;

@Getter
@Setter
public class PlanStatusResponse {

    private int code;
    private List<PlanStatusData> data;
    private Map<String, Object> errors;
    private String message;

    @Getter
    @Setter
    public static class PlanStatusData {
        private long id;
        private String name;
        private String status;
        private String public_ip_address;
        private String private_ip_address;
        private String rescue_mode_status;
        private boolean is_locked;
        private String plan;
        private boolean is_accidental_protection;
    }
}