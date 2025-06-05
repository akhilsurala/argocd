package com.sunseed.simtool.embeddables;

import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Embeddable
public class BackupStatus {
    private String backupStatus;
    private String backupDetail;
    private long backupNodeId;
    private boolean backup_is_encryption_enabled;
}