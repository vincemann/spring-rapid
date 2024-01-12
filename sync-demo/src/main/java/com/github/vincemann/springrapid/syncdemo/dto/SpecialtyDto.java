package com.github.vincemann.springrapid.syncdemo.dto;


import com.github.vincemann.springrapid.autobidir.dto.parent.annotation.BiDirParentIdCollection;
import com.github.vincemann.springrapid.syncdemo.dto.abs.MyIdDto;
import com.github.vincemann.springrapid.syncdemo.model.Specialty;
import com.github.vincemann.springrapid.syncdemo.model.Vet;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.Size;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
public class SpecialtyDto extends MyIdDto<Long> {

    @Builder
    public SpecialtyDto(@Size(min = 2, max = 255) String description, Set<Long> vetIds) {
        this.description = description;
        if (vetIds!=null)
            this.vetIds = vetIds;
    }

    public SpecialtyDto(Specialty specialty){
        this.description=specialty.getDescription();
    }

    @Size(min = 2, max = 255)
    private String description;

    @BiDirParentIdCollection(Vet.class)
    private Set<Long> vetIds = new HashSet<>();
}
