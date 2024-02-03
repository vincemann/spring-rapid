package com.github.vincemann.springrapid.coredemo.dto;

import com.github.vincemann.springrapid.coredemo.model.Pet;
import com.github.vincemann.springrapid.autobidir.id.annotation.parent.BiDirParentId;
import lombok.*;

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
