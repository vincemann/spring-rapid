package com.github.vincemann.springrapid.acldemo.dto;

import com.github.vincemann.springrapid.acldemo.model.Pet;
import com.github.vincemann.springrapid.entityrelationship.dto.parent.annotation.BiDirParentId;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

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
