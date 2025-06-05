package com.sunseed.simtool.model.request;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NodeRequest {
    private String label;
    private String name;
    private String region;
    private String plan;
    private String image;
    private List<String> ssh_keys;
    private List<String> start_scripts;
    private boolean backups;
    private boolean enable_bitninja;
    private boolean disable_password;
    
    @JsonProperty("is_saved_image")
    private boolean is_saved_image;
    
    private String saved_image_template_id;
    private String reserve_ip;
    
    @JsonProperty("is_ipv6_availed")
    private boolean is_ipv6_availed;
    
    private boolean default_public_ip;
    private String ngc_container_id;
    private int number_of_instances;
    private int security_group_id;
}
