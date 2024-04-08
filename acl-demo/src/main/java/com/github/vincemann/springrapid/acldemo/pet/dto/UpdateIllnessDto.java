package com.github.vincemann.springrapid.acldemo.pet.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

@Setter
@Getter
@NoArgsConstructor
public class UpdateIllnessDto {

    @NotNull
    @Positive
    private Long id;

    @NotNull
    private String illnessName;

    @Builder
    public UpdateIllnessDto(Long id, String illnessName) {
        this.id = id;
        this.illnessName = illnessName;
    }

    @Override
    public String toString() {
        return "VetUpdatesPetDto{" +
                "id=" + id +
                ", illnessName=" + illnessName +
                '}';
    }
}
