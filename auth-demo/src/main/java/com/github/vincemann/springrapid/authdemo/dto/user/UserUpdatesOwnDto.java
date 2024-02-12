package com.github.vincemann.springrapid.authdemo.dto.user;

import com.github.vincemann.springrapid.authdemo.service.val.ValidUsername;
import com.github.vincemann.springrapid.core.dto.IdAwareDto;
import lombok.*;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class UserUpdatesOwnDto extends IdAwareDto<Long> {

    @ValidUsername
    @NotBlank
    private String name;
}
