package com.github.vincemann.springrapid.coredemo.dtos;

import com.github.vincemann.springrapid.core.model.IdentifiableEntityImpl;
import com.github.vincemann.springrapid.entityrelationship.dto.child.UniDirChildDto;
import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Getter
@Setter
@NoArgsConstructor
@Builder
@AllArgsConstructor
@ToString(callSuper = true)
public class PetTypeDto extends IdentifiableEntityImpl<Long> implements UniDirChildDto {
    @Size(min = 2, max = 20)
    private String name;
}
