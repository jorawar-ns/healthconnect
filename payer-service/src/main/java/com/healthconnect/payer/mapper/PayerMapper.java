package com.healthconnect.payer.mapper;

import com.healthconnect.payer.dto.PayerResponse;
import com.healthconnect.payer.entity.Payer;
import org.mapstruct.Mapper;
import org.springframework.data.domain.Page;

import java.util.List;

@Mapper(componentModel = "spring")
public interface PayerMapper {
    PayerResponse toDto(Payer payer);
}
