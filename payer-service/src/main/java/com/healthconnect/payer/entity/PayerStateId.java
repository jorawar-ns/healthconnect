package com.healthconnect.payer.entity;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

@Embeddable
@Data
public class PayerStateId implements Serializable {

    @Column(name = "payer_id")
    private String payerId;
    private String value;
}
