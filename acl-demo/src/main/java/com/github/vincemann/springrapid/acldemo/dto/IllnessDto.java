package com.github.vincemann.springrapid.acldemo.dto;

import com.github.vincemann.springrapid.acldemo.model.Pet;
import com.github.vincemann.springrapid.entityrelationship.dto.child.BiDirChildDto;
import com.github.vincemann.springrapid.entityrelationship.dto.child.annotation.BiDirChildIdCollection;
import com.github.vincemann.springrapid.entityrelationship.dto.parent.annotation.BiDirParentId;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.Size;
import java.util.Set;

@Getter
@Setter
@ToString(callSuper = true)
@Builder
public class IllnessDto implements BiDirChildDto {

    @Size(min = 2, max = 20)
    private String name;

    @BiDirChildIdCollection(Pet.class)
    private Set<Long> petIds;
}
