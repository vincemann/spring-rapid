package com.github.vincemann.springrapid.acldemo.pet.dto;

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
// cant update pets owner in contrast to vet
public class OwnerUpdatesPetDto {

    @NotNull
    @Positive
    private Long id;

    @NotBlank
    @Size(min = 2, max = 20)
    private String name;

    private LocalDate birthDate;

    @Builder
    public OwnerUpdatesPetDto(Long id, String name, LocalDate birthDate) {
        this.id = id;
        this.name = name;
        this.birthDate = birthDate;
    }

    @Override
    public String toString() {
        return "OwnerUpdatesPetDto{" +
                "id=" + id +
                ", name='" + getName() + '\'' +
                ", birthDate=" + getBirthDate() +
                '}';
    }
}
