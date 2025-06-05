package com.sunseed.model.requestDTO;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
public class PvModuleConfigurationRequestDto {
    private Long id;

    private String moduleConfig;

}
