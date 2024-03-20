package com.github.vincemann.springrapid.acldemo.dto.pet;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.util.Set;

@Setter
@Getter
@NoArgsConstructor
public class VetUpdatesPetDto {

    @NotNull
    @Positive
    private Long id;

    @NotNull
    private Set<Long> illnessIds;

    @Builder
    public VetUpdatesPetDto(Long id, Set<Long> illnessIds) {
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
