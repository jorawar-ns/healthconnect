package com.healthconnect.payer;

import com.healthconnect.payer.entity.Payer;
import com.healthconnect.payer.specification.PayerSpecification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PayerRepository extends JpaRepository<Payer, String>, JpaSpecificationExecutor<Payer> {
    boolean existsByPayerId(String payerId);
    Optional<Payer> findByPayerId(String payerId);

}
