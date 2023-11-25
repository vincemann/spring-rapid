package com.github.vincemann.springrapid.authdemo.dto;

import com.github.vincemann.springrapid.authdemo.dto.abs.MyIdDto;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class UserUpdatesOwnDto extends MyIdDto<Long> {
    private String name;
}
