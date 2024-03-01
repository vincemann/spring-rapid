package com.github.vincemann.springrapid.acldemo.dto.visit;

import com.github.vincemann.springrapid.acldemo.dto.visit.abs.AbstractVisitDto;
import com.github.vincemann.springrapid.acldemo.model.Pet;
import com.github.vincemann.springrapid.acldemo.model.Visit;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.time.LocalDate;
import java.util.Set;
import java.util.stream.Collectors;

@Setter
@Getter
@NoArgsConstructor
public class CreateVisitDto extends AbstractVisitDto {


    @NotNull
    @Positive
    @Override
    public Long getVetId() {
        return super.getVetId();
    }

    @NotEmpty
    @Override
    public String getReason() {
        return super.getReason();
    }

    @NotNull
    @Positive
    @Override
    public Long getOwnerId() {
        return super.getOwnerId();
    }

    @NotEmpty
    @Override
    public Set<Long> getPetIds() {
        return super.getPetIds();
    }

    @NotNull
    @Override
    public LocalDate getDate() {
        return super.getDate();
    }

    @Builder
    public CreateVisitDto(Set<Long> petIds, Long ownerId, Long vetId, LocalDate date, String reason) {
        super(petIds, ownerId, vetId, date, reason);
    }

    public CreateVisitDto(Visit visit) {
        this(
                visit.getPets().stream().map(Pet::getId).collect(Collectors.toSet()),
                visit.getOwner().getId(),
                visit.getVet().getId(),
                visit.getDate(),
                visit.getReason()
        );
    }


}
