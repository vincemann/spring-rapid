package com.github.vincemann.springrapid.syncdemo.dto.pet;

import com.github.vincemann.springrapid.autobidir.dto.child.annotation.BiDirChildIdCollection;
import com.github.vincemann.springrapid.autobidir.dto.child.annotation.UniDirChildId;
import com.github.vincemann.springrapid.autobidir.dto.parent.annotation.BiDirParentId;
import com.github.vincemann.springrapid.syncdemo.dto.abs.MyIdDto;
import com.github.vincemann.springrapid.syncdemo.model.Owner;
import com.github.vincemann.springrapid.syncdemo.model.PetType;
import com.github.vincemann.springrapid.syncdemo.model.Toy;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
public abstract class AbstractPetDto extends MyIdDto<Long>
{

    public AbstractPetDto(@Size(min = 2, max = 20) String name, Long petTypeId, Set<Long> toyIds, Long ownerId, LocalDate birthDate) {
        this.name = name;
        this.petTypeId = petTypeId;
        if (toyIds!=null)
            this.toyIds = toyIds;
        this.ownerId = ownerId;
        this.birthDate = birthDate;
    }

    @Size(min = 2, max = 20)
    private String name;

    @UniDirChildId(PetType.class)
    private Long petTypeId;

    @BiDirChildIdCollection(Toy.class)
    private Set<Long> toyIds = new HashSet<>();

    @BiDirParentId(Owner.class)
    private Long ownerId;

    private LocalDate birthDate;
}
