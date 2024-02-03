package com.github.vincemann.springrapid.syncdemo.dto;

import com.github.vincemann.springrapid.autobidir.id.annotation.parent.BiDirParentId;
import com.github.vincemann.springrapid.syncdemo.model.Pet;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.Size;

@Getter
@Setter
@ToString
@Builder
public class ToyDto {

    @Size(min = 2, max = 20)
    private String name;

    @BiDirParentId(Pet.class)
    private Long petId;
}
