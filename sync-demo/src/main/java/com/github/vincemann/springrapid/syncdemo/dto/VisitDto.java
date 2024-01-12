package com.github.vincemann.springrapid.syncdemo.dto;

import com.github.vincemann.springrapid.autobidir.dto.child.annotation.UniDirChildId;
import com.github.vincemann.springrapid.autobidir.dto.child.annotation.UniDirChildIdCollection;
import com.github.vincemann.springrapid.syncdemo.dto.abs.MyIdDto;
import com.github.vincemann.springrapid.syncdemo.model.Owner;
import com.github.vincemann.springrapid.syncdemo.model.Pet;
import com.github.vincemann.springrapid.syncdemo.model.Vet;
import com.github.vincemann.springrapid.syncdemo.model.Visit;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
public class VisitDto extends MyIdDto<Long> {

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
