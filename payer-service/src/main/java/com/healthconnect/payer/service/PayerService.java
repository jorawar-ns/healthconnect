package com.healthconnect.payer.service;

import com.healthconnect.payer.PayerRepository;
import com.healthconnect.payer.dto.PayerResponse;
import com.healthconnect.payer.dto.PayerSearchCriteria;
import com.healthconnect.payer.entity.Payer;
import com.healthconnect.payer.mapper.PayerMapper;
import com.healthconnect.payer.specification.PayerSpecification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class PayerService {
    @Autowired
    private PayerRepository payerRepository;
    @Autowired
    private PayerMapper payerMapper;

    public Page<PayerResponse> searchPayers(PayerSearchCriteria criteria, Pageable pageable) {
        Page<Payer> payers = payerRepository.findAll(PayerSpecification.from(criteria), pageable);
        return payers.map(payerMapper::toDto);
    }
}
