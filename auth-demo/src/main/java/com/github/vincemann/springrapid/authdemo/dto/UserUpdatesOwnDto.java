package com.github.vincemann.springrapid.authdemo.dto;

import com.github.vincemann.springrapid.core.dto.IdAwareDto;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class UserUpdatesOwnDto extends IdAwareDto<Long> {
    private String name;
}
