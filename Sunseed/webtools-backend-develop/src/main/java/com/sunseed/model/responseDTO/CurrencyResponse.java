package com.sunseed.model.responseDTO;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CurrencyResponse {
    private Long currencyId;
    private String currency;

}
