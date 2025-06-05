package com.sunseed.model.responseDTO;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class MasterRunResponse {

    private Long id;
    private String runName;
}
