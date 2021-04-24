package com.github.vincemann.springrapid.coredemo.dtos;

import com.github.vincemann.springrapid.core.model.IdentifiableEntityImpl;
import com.github.vincemann.springrapid.coredemo.model.Owner;
import com.github.vincemann.springrapid.coredemo.model.Pet;
import com.github.vincemann.springrapid.coredemo.model.Vet;
import com.github.vincemann.springrapid.entityrelationship.dto.child.annotation.UniDirChildId;
import com.github.vincemann.springrapid.entityrelationship.dto.child.annotation.UniDirChildIdCollection;
import com.github.vincemann.springrapid.entityrelationship.dto.parent.UniDirParentDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@ToString(callSuper = true)
public class VisitDto extends IdentifiableEntityImpl<Long> implements UniDirParentDto {

    @UniDirChildIdCollection(Pet.class)
    private Set<Long> petIds;

    @UniDirChildId(Owner.class)
    private Long ownerId;

    @UniDirChildId(Vet.class)
    private Long vetId;

    @NotNull
    private LocalDate date;

    private String reason;
}
