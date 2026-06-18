package com.healthconnect.claims.entity;

/**
 * Lifecycle states for a professional/institutional claim.
 *
 * Maps loosely to X12 claim status categories (TR3 277CA).
 * PARTIALLY_PAID added per stakeholder request — HCEP-1874.
 */
public enum ClaimStatus {
    RECEIVED,
    PENDING_REVIEW,
    ADJUDICATED,
    PAID,
    DENIED,
    PARTIALLY_PAID,
    VOIDED,
    RESUBMITTED,
    ERROR
}
