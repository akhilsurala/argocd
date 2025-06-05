package com.sunseed.mappers;

import com.sunseed.entity.Currency;
import com.sunseed.entity.EconomicMultiCrop;
import com.sunseed.entity.EconomicParameters;
import com.sunseed.model.requestDTO.EconomicMultiCropRequestDto;
import com.sunseed.model.responseDTO.CurrencyResponse;
import com.sunseed.model.responseDTO.EconomicMultiCropResponse;
import com.sunseed.model.responseDTO.EconomicParametersResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class EconomicParameterModelMapper {


    public EconomicParametersResponseDto getEconomicParameterResponseDto(EconomicParameters economicParameters) {
        // response for economic multicrop
        List<EconomicMultiCropResponse> economicMultiCropResponseList = economicParameters.getEconomicMultiCrop().stream()
        		.filter(economicMultiCrop -> !economicMultiCrop.getCrop().getHide()).map((economicMultiCrop) -> {
            EconomicMultiCropResponse economicMultiCropResponse = EconomicMultiCropResponse.builder()
                    .cropId(economicMultiCrop.getCrop().getId())
                    .crop(economicMultiCrop.getCrop())
                    .id(economicMultiCrop.getId())
                    .minInputCostOfCrop(economicMultiCrop.getMinInputCostOfCrop())
                    .maxInputCostOfCrop(economicMultiCrop.getMaxInputCostOfCrop())
                    .minSellingCostOfCrop(economicMultiCrop.getMinSellingCostOfCrop())
                    .maxSellingCostOfCrop(economicMultiCrop.getMaxSellingCostOfCrop())
                    .minReferenceYieldCost(economicMultiCrop.getMinReferenceYieldCost())
                    .maxReferenceYieldCost(economicMultiCrop.getMaxReferenceYieldCost())
                    .cultivationArea(economicMultiCrop.getCultivationArea())
                    .createdAt(economicMultiCrop.getCreatedAt())
                    .updatedAt(economicMultiCrop.getUpdatedAt())
                    .build();
            return economicMultiCropResponse;
        }).collect(Collectors.toList());

        Currency currency = economicParameters.getCurrency();
        CurrencyResponse currencyResponse = CurrencyResponse.builder().currencyId(currency.getCurrencyId()).currency(currency.getCurrency()).build();
        EconomicParametersResponseDto economicParametersResponseDto = EconomicParametersResponseDto
                .builder()
                .economicId(economicParameters.getEconomicId())
                .economicMultiCropResponseList(economicMultiCropResponseList)
                .economicParameter(economicParameters.isEconomicParameter())
                .currency(currencyResponse)
                .hourlySellingRates(economicParameters.getHourlySellingRates())
                .createdAt(economicParameters.getCreatedAt())
                .updatedAt(economicParameters.getUpdatedAt())
                .build();
        return economicParametersResponseDto;

    }
//    public List<EconomicMultiCrop> getEconomicMultiCropFromEconomicMultiCropRequest(List<EconomicMultiCropRequestDto> economicMultiCropRequestDtoList){
//      List<EconomicMultiCrop> economicMultiCropList= economicMultiCropRequestDtoList.stream().map((economicMultiCrop)->{
//          EconomicMultiCrop economicMultiCrop1=EconomicMultiCrop.builder().
//      }).collect(Collectors.toList());
//    }
}