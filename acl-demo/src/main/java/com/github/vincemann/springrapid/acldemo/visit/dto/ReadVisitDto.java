package com.github.vincemann.springrapid.acldemo.visit.dto;

import com.github.vincemann.springrapid.acldemo.visit.dto.abs.AbstractVisitDto;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.Set;

@Setter
@Getter
@NoArgsConstructor
public class ReadVisitDto extends AbstractVisitDto {

    private Long id;

    @Builder
    public ReadVisitDto(Set<Long> petIds, Long ownerId, Long vetId, LocalDate date, String reason, Long id) {
        super(petIds, ownerId, vetId, date, reason);
        this.id = id;
    }
}
