package com.github.vincemann.springrapid.acldemo.pet.dto;

import com.github.vincemann.springrapid.acldemo.pet.dto.abs.AbstractPetDto;
import com.github.vincemann.springrapid.acldemo.pet.Pet;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
public class CreatePetDto extends AbstractPetDto {


    @NotNull
    @Positive
    private Long ownerId;

    @NotBlank
    @Size(min = 2, max = 20)
    @Override
    public String getName() {
        return super.getName();
    }

    @NotNull
    @Positive
    @Override
    public Long getPetTypeId() {
        return super.getPetTypeId();
    }


    @Builder
    public CreatePetDto(String name, Long petTypeId, LocalDate birthDate, Long ownerId) {
        super(name, petTypeId, birthDate);
        this.ownerId = ownerId;
    }

    public CreatePetDto(Pet pet){
        this(
                pet.getName(),
                pet.getPetType().getId(),
                pet.getBirthDate(),
                pet.getOwner() == null ? null : pet.getOwner().getId()
        );
    }

    @Override
    public String toString() {
        return "CreatePetDto{" +
                "name='" + getName() + '\'' +
                ", petTypeId=" + getPetTypeId() +
                ", ownerId=" + getOwnerId() +
                ", birthDate=" + getBirthDate() +
                '}';
    }
}
