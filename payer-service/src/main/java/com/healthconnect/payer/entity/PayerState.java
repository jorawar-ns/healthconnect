package com.healthconnect.payer.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "payer_states")
@Getter
@Setter
@NoArgsConstructor
public class PayerState {

    @EmbeddedId
    private PayerStateId id;

    private String label;

    @ManyToOne
    @MapsId("payerId")
    @JoinColumn(name = "payer_id")
    private Payer payer;

}
