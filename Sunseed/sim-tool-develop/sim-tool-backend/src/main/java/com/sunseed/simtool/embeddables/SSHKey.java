package com.sunseed.simtool.embeddables;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Embeddable
public class SSHKey {
    private String label;
    
    @Column(name = "ssh_key", length = 2048)  // length increased for long keys
    private String ssh_key;
}