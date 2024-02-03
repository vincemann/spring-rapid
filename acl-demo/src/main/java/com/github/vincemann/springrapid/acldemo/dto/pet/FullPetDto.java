package com.github.vincemann.springrapid.acldemo.dto.pet;

import com.github.vincemann.springrapid.acldemo.model.Illness;
import com.github.vincemann.springrapid.acldemo.model.Owner;

import com.github.vincemann.springrapid.autobidir.id.annotation.child.BiDirChildIdCollection;

import com.github.vincemann.springrapid.autobidir.id.annotation.parent.BiDirParentId;
import lombok.*;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;


@NoArgsConstructor

@Getter
@Setter
public class FullPetDto extends AbstractPetDto  {

    @BiDirChildIdCollection(Illness.class)
    private Set<Long> illnessIds = new HashSet<>();

    @BiDirParentId(Owner.class)
    private Long ownerId;

    private String name;

    @Builder
    public FullPetDto(String name, Long petTypeId, LocalDate birthDate, Set<Long> illnessIds, Long ownerId) {
        super(petTypeId, birthDate);
        this.name = name;
        this.illnessIds = illnessIds;
        this.ownerId = ownerId;
    }

    @Override
    public String toString() {
        return "FullPetDto{" +
                "illnessIds=" + illnessIds +
                ", ownerId=" + ownerId +
                ", name='" + name + '\'' +
                ", petTypeId=" + getPetTypeId() +
                ", birthDate=" + getBirthDate() +
                ", id=" + getId() +
                '}';
    }
}
