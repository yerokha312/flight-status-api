package dev.yerokha.flightstatusapi.application.mapper;

import dev.yerokha.flightstatusapi.application.dto.CustomPage;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

@Component
public class CustomPageMapper {

    public <T> CustomPage<T> putIntoCustomPage(Page<T> page) {
        return new CustomPage<>(
                page.getContent(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.isEmpty()
        );
    }
}