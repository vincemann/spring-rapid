package com.github.vincemann.springrapid.coredemo.dtos;


import com.github.vincemann.springrapid.coredemo.model.Specialty;
import com.github.vincemann.springrapid.coredemo.model.Vet;
import com.github.vincemann.springrapid.entityrelationship.dto.child.BiDirChildDto;
import com.github.vincemann.springrapid.entityrelationship.dto.child.annotation.BiDirChildIdCollection;
import com.github.vincemann.springrapid.entityrelationship.dto.parent.annotation.BiDirParentIdCollection;
import lombok.*;
import com.github.vincemann.springrapid.core.model.IdentifiableEntityImpl;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@ToString(callSuper = true)
@AllArgsConstructor
public class SpecialtyDto extends IdentifiableEntityImpl<Long> implements BiDirChildDto {
    @NotBlank
    @Size(min = 2, max = 255)
    private String description;

    @Size(max = 20)
    @BiDirParentIdCollection(Vet.class)
    private Set<Long> specialtyIds = new HashSet<>();
}
