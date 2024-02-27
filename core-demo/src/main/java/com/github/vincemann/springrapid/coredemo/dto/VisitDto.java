package com.github.vincemann.springrapid.coredemo.dto;

import com.github.vincemann.springrapid.autobidir.id.annotation.child.UniDirChildId;
import com.github.vincemann.springrapid.autobidir.id.annotation.child.UniDirChildIdCollection;
import com.github.vincemann.springrapid.coredemo.dto.abs.IdAwareDto;
import com.github.vincemann.springrapid.coredemo.model.Owner;
import com.github.vincemann.springrapid.coredemo.model.Pet;
import com.github.vincemann.springrapid.coredemo.model.Vet;
import com.github.vincemann.springrapid.coredemo.model.Visit;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
public class VisitDto extends IdAwareDto {

    @UniDirChildIdCollection(Pet.class)
    private Set<Long> petIds = new HashSet<>();

    @Positive
    @UniDirChildId(Owner.class)
    private Long ownerId;

    @Positive
    @UniDirChildId(Vet.class)
    private Long vetId;

    @NotNull
    private LocalDate date;

    @NotEmpty
    private String reason;

    @Builder
    public VisitDto(Long id, Set<Long> petIds, Long ownerId, Long vetId, LocalDate date, String reason) {
        super(id);
        this.petIds = petIds;
        this.ownerId = ownerId;
        this.vetId = vetId;
        this.date = date;
        this.reason = reason;
    }

    public VisitDto(Visit visit) {
        this(
                visit.getId(),
                visit.getPets().stream().map(Pet::getId).collect(Collectors.toSet()),
                visit.getOwner() ==  null ? null : visit.getOwner().getId(),
                visit.getVet() ==  null ? null : visit.getVet().getId(),
                visit.getDate(),
                visit.getReason()
        );
    }

    @Override
    public String toString() {
        return "VisitDto{" +
                "petIds=" + petIds +
                ", ownerId=" + ownerId +
                ", vetId=" + vetId +
                ", date=" + date +
                ", reason='" + reason + '\'' +
                ", id=" + getId() +
                '}';
    }
}
