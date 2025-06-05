package com.sunseed.projection;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Months {
    private String name;
    private List<String> weeks;

}
