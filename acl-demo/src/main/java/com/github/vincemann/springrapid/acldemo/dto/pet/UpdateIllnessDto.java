package com.github.vincemann.springrapid.acldemo.dto.pet;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

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
