package com.github.vincemann.springrapid.acldemo.owner.dto.abs;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
public abstract class AbstractReadOwnerDto extends AbstractOwnerDto{
    private Long id;

    public AbstractReadOwnerDto(String firstName, String lastName, String address, String city, String telephone, Set<String> hobbies, Long id) {
        super(firstName, lastName, address, city, telephone, hobbies);
        this.id = id;
    }
}
