package com.sunseed.simtool.embeddables;

import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Embeddable
public class LicenseInfo {
    private boolean is_license_attached;
    private String license_deletion_message;
    private boolean mssql_license_attached;
}