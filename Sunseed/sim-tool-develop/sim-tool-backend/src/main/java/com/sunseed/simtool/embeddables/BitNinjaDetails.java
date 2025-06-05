package com.sunseed.simtool.embeddables;

import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Embeddable
public class BitNinjaDetails {
    private boolean show_bitninja;
    private double bitninja_cost;
}