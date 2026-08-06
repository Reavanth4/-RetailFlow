package com.retailflow.reportservice.client;

import java.util.List;

public record PageRecord<T>(List<T> content,
                            int page,
                            int size,
                            long totalElements,
                            int totalPages,
                            boolean last) {
}
