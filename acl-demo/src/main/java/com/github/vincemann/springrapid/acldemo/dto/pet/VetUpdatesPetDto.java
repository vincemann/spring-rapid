package com.github.vincemann.springrapid.acldemo.dto.pet;

import com.github.vincemann.springrapid.acldemo.dto.pet.abs.AbstractPetDto;
import com.github.vincemann.springrapid.acldemo.model.Illness;
import com.github.vincemann.springrapid.acldemo.model.Owner;
import com.github.vincemann.springrapid.autobidir.resolveid.annotation.child.BiDirChildIdCollection;
import com.github.vincemann.springrapid.autobidir.resolveid.annotation.parent.BiDirParentId;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Setter
@Getter
@NoArgsConstructor
public class VetUpdatesPetDto extends AbstractPetDto {

    @NotNull
    @Positive
    private Long id;

    @BiDirChildIdCollection(Illness.class)
    private Set<Long> illnessIds = new HashSet<>();

    @NotNull
    @Positive
    @BiDirParentId(Owner.class)
    private Long ownerId;

    @NotBlank
    @Size(min = 2, max = 20)
    @Override
    public  String getName() {
        return super.getName();
    }

    @Positive
    @NotNull
    @Override
    public Long getPetTypeId() {
        return super.getPetTypeId();
    }


    @Builder
    public VetUpdatesPetDto(String name, Long petTypeId, LocalDate birthDate, Long id, Set<Long> illnessIds, Long ownerId) {
        super(name, petTypeId, birthDate);
        this.id = id;
        if (illnessIds != null)
            this.illnessIds = illnessIds;
        this.ownerId = ownerId;
    }
}
