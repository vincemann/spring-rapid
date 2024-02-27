package com.github.vincemann.springrapid.syncdemo.dto;

import com.github.vincemann.springrapid.autobidir.id.annotation.parent.BiDirParentId;
import com.github.vincemann.springrapid.coredemo.dto.abs.IdAwareDto;
import com.github.vincemann.springrapid.coredemo.model.Pet;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

@Getter
@Setter
@NoArgsConstructor
public class ToyDto extends IdAwareDto {

    @NotEmpty
    @Size(min = 2, max = 20)
    private String name;
    @BiDirParentId(Pet.class)
    private Long petId;

    @Builder
    public ToyDto(Long id, String name, Long petId) {
        super(id);
        this.name = name;
        this.petId = petId;
    }

    @Override
    public String toString() {
        return "ToyDto{" +
                "name='" + name + '\'' +
                ", petId=" + petId +
                ", id=" + getId() +
                '}';
    }
}
