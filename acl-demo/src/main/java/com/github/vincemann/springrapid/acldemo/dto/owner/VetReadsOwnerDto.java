package com.github.vincemann.springrapid.acldemo.dto.owner;


import lombok.*;

import javax.validation.constraints.Size;
import java.util.Set;

@NoArgsConstructor
@Getter
@Setter

public class VetReadsOwnerDto extends AbstractOwnerDto {


    @Builder
    public VetReadsOwnerDto(@Size(min = 2, max = 20) String firstName, @Size(min = 2, max = 20) String lastName, @Size(min = 10, max = 255) String address, @Size(min = 3, max = 255) String city, @Size(min = 10, max = 10) String telephone, Set<String> hobbies, Set<Long> petIds) {
        super(firstName, lastName, address, city, telephone, hobbies, petIds);
    }
}
