package com.github.vincemann.springrapid.syncdemo.dto.pet.abs;

import com.github.vincemann.springrapid.autobidir.id.annotation.child.BiDirChildIdCollection;
import com.github.vincemann.springrapid.autobidir.id.annotation.child.UniDirChildId;
import com.github.vincemann.springrapid.autobidir.id.annotation.parent.BiDirParentId;
import com.github.vincemann.springrapid.coredemo.model.Owner;
import com.github.vincemann.springrapid.coredemo.model.PetType;
import com.github.vincemann.springrapid.coredemo.model.Toy;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
public abstract class AbstractPetDto
{

    private String name;

    @UniDirChildId(PetType.class)
    private Long petTypeId;

    @BiDirChildIdCollection(Toy.class)
    private Set<Long> toyIds = new HashSet<>();

    @BiDirParentId(Owner.class)
    private Long ownerId;

    private LocalDate birthDate;

    public AbstractPetDto(String name, Long petTypeId, Set<Long> toyIds, Long ownerId, LocalDate birthDate) {
        this.name = name;
        this.petTypeId = petTypeId;
        if (toyIds!=null)
            this.toyIds = toyIds;
        this.ownerId = ownerId;
        this.birthDate = birthDate;
    }


}
