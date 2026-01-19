package org.example.inventory_service.Dto;

import java.util.List;

public record PageResponse<T>(List<T> data,
                              int page,
                              int size,
                              long totalElements,
                              int totalPages,
                              boolean last) {

}
