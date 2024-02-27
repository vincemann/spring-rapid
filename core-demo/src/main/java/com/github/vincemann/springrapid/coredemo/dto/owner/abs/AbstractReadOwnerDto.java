package com.github.vincemann.springrapid.coredemo.dto.owner.abs;

import com.github.vincemann.springrapid.autobidir.id.annotation.child.BiDirChildIdCollection;
import com.github.vincemann.springrapid.coredemo.model.Pet;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
public class AbstractReadOwnerDto extends AbstractOwnerDto{

    @BiDirChildIdCollection(Pet.class)
    private Set<Long> petIds = new HashSet<>();
    private Long id;

    public AbstractReadOwnerDto(String address, String city, String telephone, Set<String> hobbies, Set<Long> petIds, Long id) {
        super(address, city, telephone, hobbies);
        this.petIds = petIds;
        this.id = id;
    }
}
