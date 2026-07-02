package com.healthconnect.payer.specification;

import com.healthconnect.payer.dto.PayerSearchCriteria;
import com.healthconnect.payer.entity.Payer;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;


public class PayerSpecification {
    public static Specification<Payer> from(PayerSearchCriteria criteria){
        return ((root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if(criteria.getPayerPlanName() != null && criteria.getPayerPlanName().trim().isEmpty()){
                predicates.add(
                        cb.like(
                                cb.lower(root.get("payerPlanName")),
                        "%" + criteria.getPayerPlanName().toLowerCase() + "%")
                );
            }

            if(criteria.getStatus() != null){
                predicates.add(
                        cb.equal(root.get("status"), criteria.getStatus())
                );
            }

            if(criteria.getChiPayer() != null){
                predicates.add(
                  cb.equal(root.get("chiPayer"), criteria.getChiPayer())
                );
            }

            if(criteria.getAssurancePayer() != null){
                predicates.add(
                        cb.equal(root.get("assurancePayer"), criteria.getAssurancePayer())
                );
            }

            if(criteria.getEnrollmentRequired() != null){
                predicates.add(
                        cb.equal(root.get("getEnrollmentRequired"), criteria.getEnrollmentRequired())
                );
            }

            if(criteria.getActivationDateFrom() != null){
                predicates.add(
                        cb.greaterThanOrEqualTo(root.get("activationDate"), criteria.getActivationDateFrom())
                );
            }

            if(criteria.getActivationDateTo() != null){
                predicates.add(
                        cb.lessThanOrEqualTo(root.get("activationDate"), criteria.getActivationDateTo())
                );
            }


            return cb.and(predicates.toArray(new Predicate[0]));
        });
    }
}
