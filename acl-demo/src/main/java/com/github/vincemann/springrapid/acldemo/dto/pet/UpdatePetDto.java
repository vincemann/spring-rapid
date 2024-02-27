package com.github.vincemann.springrapid.acldemo.dto.pet;

import com.github.vincemann.springrapid.acldemo.dto.pet.abs.AbstractPetDto;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.Set;


@Getter
@Setter
@NoArgsConstructor
public class UpdatePetDto extends AbstractPetDto {

    private Long id;

    @NotBlank
    @Size(min = 2, max = 20)
    @Override
    public  String getName() {
        return super.getName();
    }

    @Positive
    @Override
    public Long getPetTypeId() {
        return super.getPetTypeId();
    }

    @Positive
    @Override
    public Long getOwnerId() {
        return super.getOwnerId();
    }

    @Builder
    public UpdatePetDto(String name, Long petTypeId, Long ownerId, LocalDate birthDate, Long id) {
        super(name, petTypeId, ownerId, birthDate);
        this.id = id;
    }

    @Override
    public String toString() {
        return "UpdatePetDto{" +
                "id=" + id +
                ", name='" + getName() + '\'' +
                ", petTypeId=" + getPetTypeId() +
                ", ownerId=" + getOwnerId() +
                ", birthDate=" + getBirthDate() +
                '}';
    }
}
