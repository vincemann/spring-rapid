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
public class UpdatePetsIllnessesDto {

    @NotNull
    @Positive
    private Long id;

    @NotNull
    private Set<Long> illnessIds;

    @Builder
    public UpdatePetsIllnessesDto(Long id, Set<Long> illnessIds) {
        this.id = id;
        this.illnessIds = illnessIds;
    }

    @Override
    public String toString() {
        return "VetUpdatesPetDto{" +
                "id=" + id +
                ", illnessIds=" + illnessIds +
                '}';
    }
}
