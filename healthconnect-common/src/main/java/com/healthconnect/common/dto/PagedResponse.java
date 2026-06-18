package com.healthconnect.common.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

/**
 * Generic paginated wrapper matching the Optum payer-list response shape:
 * { page, pageSize, total, items[] }
 */
@Getter
@Builder
public class PagedResponse<T> {

    private final int page;
    private final int pageSize;
    private final long total;
    private final List<T> items;
}
