package com.healthconnect.payer.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "payer_additional_info")
@Getter
@Setter
@NoArgsConstructor
public class PayerAdditionalInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "payer_id", referencedColumnName = "id")
    private Payer payer;

    @Column(length = 100)
    private String key;

    private String value;
}
