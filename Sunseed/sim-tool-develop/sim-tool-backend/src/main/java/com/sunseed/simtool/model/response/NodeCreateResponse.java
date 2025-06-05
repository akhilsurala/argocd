package com.sunseed.simtool.model.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class NodeCreateResponse {
    private int code;
    private ResponseData data;
    private Object errors;
    private String message;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ResponseData {
        private int total_number_of_node_requested;
        private int total_number_of_node_created;
        private List<NodeCreateResult> node_create_response;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class NodeCreateResult {
        private int id;
        private String name;
        private int vm_id;
        private String created_at;
        private String public_ip_address;
        private String private_ip_address;
        private boolean backup;
        private String disk;
        private String status;
        private String vcpus;
        private String memory;
        private String plan;
        private String region;
        
        @JsonProperty("is_locked")
        private boolean is_locked;
        
        private String gpu;
        private String price;
        private List<String> additional_ip;
        private String label;
        private List<SshKey> ssh_keys;
        
        @JsonProperty("is_active")
        private boolean is_active;
        
        private Integer scaler_id;
        private OsInfo os_info;
        
        @JsonProperty("is_monitored")
        private boolean is_monitored;
        
        private BackupStatus backup_status;
        private String location;
        private MonitorStatus monitor_status;
        private EnableBitninjaDetails enable_bitninja_details;
        
        @JsonProperty("is_bitninja_license_active")
        private boolean is_bitninja_license_active;
        
        private LicenseStatus any_license_attached;
        
        @JsonProperty("is_committed")
        private boolean is_committed;
        
        private String audit_log_message;
        private boolean monitoring_tab_enabled;
        private boolean cdp_tab_enabled;
        private boolean alert_tab_enabled;
        private int bitninja_discount_percentage;
        
        @JsonProperty("is_image_deleted")
        private boolean is_image_deleted;
        
        private boolean vpc_enabled;
        
        @JsonProperty("is_snapshot_allowed")
        private boolean is_snapshot_allowed;
        
        @JsonProperty("is_fortigate_vm")
        private boolean is_fortigate_vm;
        
        private String rescue_mode_status;
        
        @JsonProperty("is_upgradable")
        private boolean is_upgradable;
        
        private boolean abuse_flag;
        private String currency;
        private String vm_type;
        
        @JsonProperty("is_accidental_protection")
        private boolean is_accidental_protection;
        
        private String project_name;
        private String resource_type;
        private String label_id;
        private String zabbix_host_id;
        private String zabbix_host_id_v2;

        // Nested Classes

        @Getter
        @Setter
        @NoArgsConstructor
        @AllArgsConstructor
        public static class SshKey {
            private String label;
            private String ssh_key;
        }

        @Getter
        @Setter
        @NoArgsConstructor
        @AllArgsConstructor
        public static class OsInfo {
            private String name;
            private String version;
            private String category;
            private String full_name;
        }

        @Getter
        @Setter
        @NoArgsConstructor
        @AllArgsConstructor
        public static class BackupStatus {
            private String status;
            private String detail;
            private int node_id;
            
            @JsonProperty("is_encryption_enabled")
            private boolean is_encryption_enabled;
        }

        @Getter
        @Setter
        @NoArgsConstructor
        @AllArgsConstructor
        public static class MonitorStatus {
            private String status;
            private String reason;
        }

        @Getter
        @Setter
        @NoArgsConstructor
        @AllArgsConstructor
        public static class EnableBitninjaDetails {
            private boolean show_bitninja;
            private int bitninja_cost;
        }

        @Getter
        @Setter
        @NoArgsConstructor
        @AllArgsConstructor
        public static class LicenseStatus {
        	
        	@JsonProperty("is_license_attached")
            private boolean is_license_attached;
        	
            private String license_deletion_message;
            private boolean mssql_license_attached;
        }
    }
}
