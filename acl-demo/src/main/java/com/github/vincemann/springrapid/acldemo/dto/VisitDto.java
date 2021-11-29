package com.github.vincemann.springrapid.acldemo.dto;

import com.github.vincemann.springrapid.acldemo.model.Owner;
import com.github.vincemann.springrapid.acldemo.model.Pet;
import com.github.vincemann.springrapid.acldemo.model.Vet;
import com.github.vincemann.springrapid.acldemo.model.Visit;
import com.github.vincemann.springrapid.core.model.IdentifiableEntityImpl;
import com.github.vincemann.springrapid.autobidir.dto.child.annotation.UniDirChildId;
import com.github.vincemann.springrapid.autobidir.dto.child.annotation.UniDirChildIdCollection;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor

public class VisitDto extends IdentifiableEntityImpl<Long>  {

    @UniDirChildIdCollection(Pet.class)
    private Set<Long> petIds = new HashSet<>();

    @UniDirChildId(Owner.class)
    private Long ownerId;

    @UniDirChildId(Vet.class)
    private Long vetId;

    @NotNull
    private LocalDate date;

    private String reason;

    public VisitDto(Visit visit) {
        this.petIds = visit.getPets().stream().map(Pet::getId).collect(Collectors.toSet());
        this.ownerId = visit.getOwner() ==  null ? null : visit.getOwner().getId();
        this.vetId = visit.getVet() ==  null ? null : visit.getVet().getId();
        this.reason=visit.getReason();
        this.date=visit.getDate();
    }
}
