package az.ingress.model.mapper;

import az.ingress.model.request.BalanceUpdateDto;

import java.math.BigDecimal;

public enum BalanceUpdateMapper {
    BALANCE_UPDATE_MAPPER;

    public static BalanceUpdateDto buildBalanceUpdateDto(BigDecimal newBalance){
        return  BalanceUpdateDto.builder()
                .newBalance(newBalance)
                .build();
    }
}
