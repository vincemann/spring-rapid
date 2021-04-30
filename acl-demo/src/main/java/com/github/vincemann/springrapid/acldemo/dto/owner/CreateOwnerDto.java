package com.github.vincemann.springrapid.acldemo.dto.owner;


import com.github.vincemann.springrapid.acldemo.dto.user.CreateUserDto;
import com.github.vincemann.springrapid.acldemo.model.Owner;
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
public class CreateOwnerDto extends AbstractOwnerDto implements CreateUserDto {


    @NotBlank
    private String uuid;

    @Null
    @Override
    public Long getId() {
        return super.getId();
    }


    @Builder
    public CreateOwnerDto(@Size(min = 2, max = 20) String firstName, @Size(min = 2, max = 20) String lastName, @Size(min = 10, max = 255) String address, @Size(min = 3, max = 255) String city, @Size(min = 10, max = 10) String telephone, Set<String> hobbies, Set<Long> petIds, @NotBlank String uuid) {
        super(firstName, lastName, address, city, telephone, hobbies, petIds);
        this.uuid = uuid;
    }

    public CreateOwnerDto(Owner owner,String uuid) {
        super(owner.getFirstName(),owner.getLastName(),owner.getAddress(),owner.getCity(),owner.getTelephone(),owner.getHobbies(),new HashSet<>());
        this.uuid = uuid;
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
