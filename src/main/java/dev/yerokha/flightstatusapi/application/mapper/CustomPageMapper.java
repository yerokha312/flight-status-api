package dev.yerokha.flightstatusapi.application.mapper;

import dev.yerokha.flightstatusapi.application.dto.CustomPage;
import org.springframework.data.domain.Page;

public class CustomPageMapper {
    public static <T> CustomPage<T> getCustomPage(Page<T> page) {
        return new CustomPage<T>(
                page.getContent(),
                page.getNumber(),
                page.getSize(),
                page.getNumber(),
                page.getTotalPages(),
                page.isEmpty()
        );
    }
}