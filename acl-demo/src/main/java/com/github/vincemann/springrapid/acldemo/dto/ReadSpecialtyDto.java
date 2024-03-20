package com.github.vincemann.springrapid.acldemo.dto;


import com.github.vincemann.springrapid.acldemo.dto.abs.IdAwareDto;
import com.github.vincemann.springrapid.acldemo.model.Specialty;
import com.github.vincemann.springrapid.acldemo.model.Vet;
import com.github.vincemann.springrapid.autobidir.resolveid.annotation.parent.BiDirParentIdCollection;

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
public class ReadSpecialtyDto extends IdAwareDto {

    @Size(min = 2, max = 255)
    private String description;
    private Set<Long> vetIds = new HashSet<>();
    @Builder
    public ReadSpecialtyDto(@Size(min = 2, max = 255) String description, Set<Long> vetIds) {
        this.description = description;
        if (vetIds!=null)
            this.vetIds = vetIds;
    }

    public ReadSpecialtyDto(Specialty specialty){
        this.description=specialty.getDescription();
    }


}
