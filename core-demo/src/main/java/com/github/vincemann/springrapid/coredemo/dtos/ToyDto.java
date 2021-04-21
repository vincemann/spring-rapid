package com.github.vincemann.springrapid.coredemo.dtos;

import com.github.vincemann.springrapid.coredemo.model.Pet;
import com.github.vincemann.springrapid.entityrelationship.dto.parent.annotation.BiDirParentId;
import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Getter
@Setter
@ToString(callSuper = true)
@Builder
public class ToyDto {

    @Size(min = 2, max = 20)
    private String name;

    @BiDirParentId(Pet.class)
    private Long petId;
}
