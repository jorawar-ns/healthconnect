package com.healthconnect.claims.entity;

/**
 * Claim type — drives validation rules and clearinghouse routing.
 *
 * PROFESSIONAL = CMS-1500 / 837P
 * INSTITUTIONAL = UB-04 / 837I
 * DENTAL = ADA-2019 / 837D
 */
public enum ClaimType {
    PROFESSIONAL,
    INSTITUTIONAL,
    DENTAL
}
