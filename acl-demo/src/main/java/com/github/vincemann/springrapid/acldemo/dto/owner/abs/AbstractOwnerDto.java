package com.github.vincemann.springrapid.acldemo.dto.owner.abs;

import com.github.vincemann.springrapid.acldemo.model.Pet;
import com.github.vincemann.springrapid.autobidir.id.annotation.child.BiDirChildIdCollection;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.lang.Nullable;

import javax.validation.constraints.Size;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
public abstract class AbstractOwnerDto{

    private String firstName;

    private String lastName;

    @Size(min=10,max=255)
    private String address;

    @Size(min=3,max=255)
    private String city;

    @Size(min=10,max=10)
    private String telephone;

    private Set<String> hobbies = new HashSet<>();

    @Nullable
    @BiDirChildIdCollection(Pet.class)
    private Set<Long> petIds = new HashSet<>();

    public AbstractOwnerDto(String firstName, String lastName, String address, String city, String telephone, Set<String> hobbies, @Nullable Set<Long> petIds) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.address = address;
        this.city = city;
        this.telephone = telephone;
        if (hobbies != null)
            this.hobbies = hobbies;
        if (petIds != null)
            this.petIds = petIds;
    }
}
