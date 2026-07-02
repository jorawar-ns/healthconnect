package com.healthconnect.payer.controller;

import com.healthconnect.common.constants.HealthConnectConstants;
import com.healthconnect.common.dto.ApiResponse;
import com.healthconnect.payer.dto.PayerResponse;
import com.healthconnect.payer.dto.PayerSearchCriteria;
import com.healthconnect.payer.service.PayerService;
import com.sun.tools.javac.util.DefinedBy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.time.LocalDate;

@RestController
@RequestMapping("/payerlist/v1")
public class PayerController {
    @Autowired
    private PayerService payerService;

    @GetMapping
    public ApiResponse<Page<PayerResponse>> searchPayers(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String payerPlanName,
            @RequestParam(required = false) Boolean chiPayer,
            @RequestParam(required = false) Boolean assurancePayer,
            @RequestParam(required = false) Boolean enrollmentRequired,
            @RequestParam(required = false) LocalDate activationDateFrom,
            @RequestParam(required = false) LocalDate activationDateTo,
            @RequestParam(required = false) Instant lastUpdatedAfter,
            @PageableDefault(size = 20, sort="payerPlanName") Pageable pageable
//            @RequestParam(defaultValue = "0") int page,
//            @RequestParam(defaultValue = "20") int size
//            @RequestParam(required = false, defaultValue = "payerPlanName", name = "sort") String sortCol
    ){
        if(pageable.getPageSize() >= 100) {
            return ApiResponse.error("Max page size is 99", "400");
        }

        PayerSearchCriteria payerSearchCriteria = new PayerSearchCriteria();
        payerSearchCriteria.setStatus(status);
        payerSearchCriteria.setPayerPlanName(payerPlanName);
        payerSearchCriteria.setChiPayer(chiPayer);
        payerSearchCriteria.setAssurancePayer(assurancePayer);
        payerSearchCriteria.setEnrollmentRequired(enrollmentRequired);
        payerSearchCriteria.setActivationDateFrom(activationDateFrom);
        payerSearchCriteria.setActivationDateTo(activationDateTo);
        payerSearchCriteria.setLastUpdatedAfter(lastUpdatedAfter);

        Page<PayerResponse> payerResponsePage =
                payerService.searchPayers(
                        payerSearchCriteria,
                        pageable
                );

        return ApiResponse.ok(payerResponsePage);
    }
}
