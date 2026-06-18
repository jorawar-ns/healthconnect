package com.healthconnect.claims.repository;

import com.healthconnect.claims.entity.Claim;
import com.healthconnect.claims.entity.ClaimStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ClaimRepository extends JpaRepository<Claim, Long> {

    boolean existsByClaimNumber(String claimNumber);

    Optional<Claim> findByClaimNumber(String claimNumber);

    Page<Claim> findByMemberId(String memberId, Pageable pageable);

    Page<Claim> findByPayerId(String payerId, Pageable pageable);

    Page<Claim> findByStatus(ClaimStatus status, Pageable pageable);

    @Query("SELECT c FROM Claim c WHERE c.memberId = :memberId AND c.status = :status")
    Page<Claim> findByMemberIdAndStatus(
            @Param("memberId") String memberId,
            @Param("status") ClaimStatus status,
            Pageable pageable);

    Optional<Claim> findByControlNumber(String controlNumber);

    Optional<Claim> findByPayerClaimNumber(String payerClaimNumber);
}
