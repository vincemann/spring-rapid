package com.github.vincemann.springrapid.syncdemo.dto;


import com.github.vincemann.springrapid.autobidir.id.annotation.parent.BiDirParentIdCollection;
import com.github.vincemann.springrapid.syncdemo.dto.abs.IdAwareDto;
import com.github.vincemann.springrapid.syncdemo.model.Specialty;
import com.github.vincemann.springrapid.syncdemo.model.Vet;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
public class SpecialtyDto extends IdAwareDto {

    @NotEmpty
    @Size(min = 2, max = 20)
    private String description;

    @BiDirParentIdCollection(Vet.class)
    private Set<Long> vetIds = new HashSet<>();

    @Builder
    public SpecialtyDto(String description, Set<Long> vetIds, Long id) {
        super(id);
        this.description = description;
        if (vetIds!=null)
            this.vetIds = vetIds;
    }

    public SpecialtyDto(Specialty specialty){
        this(specialty.getDescription(),specialty.getVets().stream().map(Vet::getId).collect(Collectors.toSet()),specialty.getId());
    }

    @Override
    public String toString() {
        return "SpecialtyDto{" +
                "description='" + description + '\'' +
                ", vetIds=" + vetIds +
                ", id=" + getId() +
                '}';
    }
}
