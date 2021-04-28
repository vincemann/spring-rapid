package com.github.vincemann.springrapid.acldemo.dto.abs;


import com.github.vincemann.springrapid.core.model.IdentifiableEntityImpl;
import lombok.*;

import javax.validation.constraints.Size;


@Getter
@Setter
@NoArgsConstructor
@ToString(callSuper = true)
@AllArgsConstructor
public class PersonDto extends IdentifiableEntityImpl<Long> {
    @Size(min = 2, max = 20)
    private String firstName;

    @Size(min = 2, max = 20)
    private String lastName;
}
