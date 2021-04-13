package com.github.vincemann.springrapid.coredemo.dtos.owner;


import com.github.vincemann.springrapid.coredemo.model.Owner;
import com.github.vincemann.springrapid.coredemo.model.Pet;
import com.github.vincemann.springrapid.entityrelationship.dto.child.annotation.BiDirChildIdCollection;
import com.github.vincemann.springrapid.entityrelationship.dto.parent.BiDirParentDto;
import com.github.vincemann.springrapid.entityrelationship.model.child.annotation.BiDirChildCollection;
import lombok.*;
import org.springframework.lang.Nullable;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Null;
import javax.validation.constraints.Size;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@ToString(callSuper = true)
public class CreateOwnerDto extends AbstractOwnerDto implements BiDirParentDto {

    @NotBlank
    @Size(min = 2, max = 20)
    private String firstName;

    @NotBlank
    @Size(min = 2, max = 20)
    private String lastName;

    @BiDirChildIdCollection(Pet.class)
    private Set<Long> petIds = new HashSet<>();

    @Null
    @Override
    public Long getId() {
        return super.getId();
    }



    @Builder
    public CreateOwnerDto(@Size(min = 10, max = 255) @NotBlank String address, @NotBlank String city, @Size(min = 10, max = 10) String telephone, @NotBlank @Size(min = 2, max = 20) String firstName, @NotBlank @Size(min = 2, max = 20) String lastName) {
        super(address, city, telephone);
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public CreateOwnerDto(Owner owner){
        super(owner.getAddress(), owner.getCity(), owner.getTelephone());
        this.firstName = owner.getFirstName();
        this.lastName = owner.getLastName();
    }

    @NotBlank
    @Override
    public @Size(min = 10, max = 255) String getAddress() {
        return super.getAddress();
    }

    @NotBlank
    @Override
    public @Size(min=3,max=255) String getCity() {
        return super.getCity();
    }

    @Nullable
    @Override
    public @Size(min = 10, max = 10) String getTelephone() {
        return super.getTelephone();
    }
}
