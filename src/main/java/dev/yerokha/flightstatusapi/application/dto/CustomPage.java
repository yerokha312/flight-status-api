package dev.yerokha.flightstatusapi.application.dto;

import jakarta.validation.constraints.NotNull;

import java.util.List;


public record CustomPage<T>(
        @NotNull List<T> content,
        @NotNull int pageNumber,
        @NotNull long pageSize,
        @NotNull long totalElements,
        @NotNull int totalPages,
        @NotNull boolean isEmpty
) {
}
