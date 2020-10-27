package com.github.vincemann.springrapid.coredemo.dtos;

import com.github.vincemann.springrapid.core.model.IdentifiableEntityImpl;
import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Getter
@Setter
@NoArgsConstructor
@Builder
@AllArgsConstructor
@ToString(callSuper = true)
public class PetTypeDto extends IdentifiableEntityImpl<Long> {
    @NotBlank
    @Size(min = 2, max = 20)
    private String name;
}
