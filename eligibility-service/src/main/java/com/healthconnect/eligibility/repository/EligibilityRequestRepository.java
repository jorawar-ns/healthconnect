package com.healthconnect.eligibility.repository;

import com.healthconnect.eligibility.entity.EligibilityRequest;
import com.healthconnect.eligibility.entity.EligibilityStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EligibilityRequestRepository extends JpaRepository<EligibilityRequest, Long> {

    Optional<EligibilityRequest> findByControlNumber(String controlNumber);

    List<EligibilityRequest> findByMemberIdAndStatus(String memberId, EligibilityStatus status);

    boolean existsByControlNumber(String controlNumber);
}
