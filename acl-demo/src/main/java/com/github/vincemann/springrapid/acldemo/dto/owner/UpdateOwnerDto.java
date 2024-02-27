package com.github.vincemann.springrapid.acldemo.dto.owner;


import com.github.vincemann.springrapid.acldemo.dto.owner.abs.AbstractOwnerDto;
import lombok.*;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
public class UpdateOwnerDto extends AbstractOwnerDto {

    @NotNull
    @Positive
    private Long id;

    @Builder
    public UpdateOwnerDto(@Size(min = 2, max = 20) String firstName, @Size(min = 2, max = 20) String lastName, @Size(min = 10, max = 255) String address, @Size(min = 3, max = 255) String city, @Size(min = 10, max = 10) String telephone, Set<String> hobbies, Set<Long> petIds, Long id) {
        super(firstName, lastName, address, city, telephone, hobbies, petIds);
        this.id = id;
    }


}
