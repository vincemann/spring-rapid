package com.github.vincemann.springrapid.acldemo.owner.dto;


import com.github.vincemann.springrapid.acldemo.owner.dto.abs.AbstractOwnerDto;
import lombok.*;
import org.springframework.lang.Nullable;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
public class UpdateOwnerDto extends AbstractOwnerDto {

    @NotNull
    @Positive
    private Long id;

    @NotEmpty
    @Size(min=2,max=20)
    @Override
    public String getFirstName() {
        return super.getFirstName();
    }

    @NotEmpty
    @Size(min=2,max=20)
    @Override
    public String getLastName() {
        return super.getLastName();
    }

    @NotEmpty
    @Size(min=10,max=255)
    @Override
    public String getAddress() {
        return super.getAddress();
    }

    @NotEmpty
    @Size(min=3,max=255)
    @Override
    public String getCity() {
        return super.getCity();
    }


    @Nullable
    @Size(min=10,max=10)
    @Override
    public String getTelephone() {
        return super.getTelephone();
    }

    @Builder
    public UpdateOwnerDto(@Size(min = 2, max = 20) String firstName, @Size(min = 2, max = 20) String lastName, @Size(min = 10, max = 255) String address, @Size(min = 3, max = 255) String city, @Size(min = 10, max = 10) String telephone, Set<String> hobbies, Long id) {
        super(firstName, lastName, address, city, telephone, hobbies);
        this.id = id;
    }




}
