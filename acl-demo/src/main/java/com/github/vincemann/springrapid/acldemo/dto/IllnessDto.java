package com.github.vincemann.springrapid.acldemo.dto;

import com.github.vincemann.springrapid.acldemo.model.Pet;

import com.github.vincemann.springrapid.autobidir.id.annotation.child.BiDirChildIdCollection;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Size;
import java.util.Set;

@Getter
@Setter

@Builder
public class IllnessDto  {

    @Size(min = 2, max = 20)
    private String name;

    @BiDirChildIdCollection(Pet.class)
    private Set<Long> petIds;
}
