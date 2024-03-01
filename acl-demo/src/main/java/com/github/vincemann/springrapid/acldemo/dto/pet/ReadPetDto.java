package com.github.vincemann.springrapid.acldemo.dto.pet;

import com.github.vincemann.springrapid.acldemo.dto.pet.abs.AbstractPetDto;
import com.github.vincemann.springrapid.acldemo.model.Illness;
import com.github.vincemann.springrapid.acldemo.model.Owner;
import com.github.vincemann.springrapid.acldemo.model.Pet;
import com.github.vincemann.springrapid.autobidir.resolveid.annotation.parent.BiDirParentId;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;


@Getter
@Setter
@NoArgsConstructor
public class ReadPetDto extends AbstractPetDto {

    @NotNull
    @Positive
    private Long id;

    private Set<Long> illnessIds = new HashSet<>();

    @NotNull
    @Positive
    @BiDirParentId(Owner.class)
    private Long ownerId;

    public ReadPetDto(String name, Long petTypeId, LocalDate birthDate, Long id, Set<Long> illnessIds, Long ownerId) {
        super(name, petTypeId, birthDate);
        this.id = id;
        if (illnessIds != null)
            this.illnessIds = illnessIds;
        this.ownerId = ownerId;
    }

    public ReadPetDto(Pet pet){
        this(
                pet.getName(),
                pet.getPetType().getId(),
                pet.getBirthDate(),
                pet.getId(),
                pet.getIllnesss().stream().map(Illness::getId).collect(Collectors.toSet()),
                pet.getOwner().getId()
        );
    }

    @Override
    public String toString() {
        return "ReadPetDto{" +
                "id=" + id +
                ", illnessIds=" + illnessIds +
                ", ownerId=" + ownerId +
                ", name='" + getName() + '\'' +
                ", petTypeId=" + getPetTypeId() +
                ", birthDate=" + getBirthDate() +
                '}';
    }
}
