package com.healthconnect.common.constants;

public final class HealthConnectConstants {

    private HealthConnectConstants() {}

    // HTTP headers
    public static final String AUTHORIZATION_HEADER  = "Authorization";
    public static final String BEARER_PREFIX          = "Bearer ";
    public static final String X_USER_ID_HEADER       = "X-User-Id";
    public static final String X_USER_ROLES_HEADER    = "X-User-Roles";
    public static final String X_CORRELATION_ID       = "X-Correlation-Id";

    // Kafka topics
    public static final String TOPIC_CLAIM_SUBMITTED   = "claim.submitted";
    public static final String TOPIC_CLAIM_ADJUDICATED = "claim.adjudicated";
    public static final String TOPIC_AUTH_APPROVED     = "auth.approved";
    public static final String TOPIC_AUTH_DENIED       = "auth.denied";
    public static final String TOPIC_ELIGIBILITY_CHECKED = "eligibility.checked";

    // Cache key prefixes
    public static final String CACHE_PAYER_LIST    = "payer:list:";
    public static final String CACHE_PAYER_FIELDS  = "payer:fields";
    public static final String CACHE_ELIGIBILITY   = "eligibility:";

    // Pagination defaults
    public static final int DEFAULT_PAGE      = 1;
    public static final int DEFAULT_PAGE_SIZE = 50;
    public static final int MAX_PAGE_SIZE     = 500;

    // Error codes
    public static final String ERR_UNAUTHORIZED       = "UNAUTHORIZED";
    public static final String ERR_FORBIDDEN          = "FORBIDDEN";
    public static final String ERR_NOT_FOUND          = "RESOURCE_NOT_FOUND";
    public static final String ERR_VALIDATION         = "VALIDATION_ERROR";
    public static final String ERR_INTERNAL           = "INTERNAL_ERROR";
}
