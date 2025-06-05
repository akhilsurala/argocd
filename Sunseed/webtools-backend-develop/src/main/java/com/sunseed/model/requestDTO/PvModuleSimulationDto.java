package com.sunseed.model.requestDTO;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class PvModuleSimulationDto {
    private Long id;
    private String moduleType;
    private Double length;
    private Double width;
    private Double pdc0;
    private Double gamma_pdc;
    private Long temp_ref;

}
//"id":2,
//               "moduleType":"num inverters",
//               "length":1400,
//               "width":800,
//               "pdc0": 0.2,
//               "gamma_pdc":0.5,
//               "temp_ref":25