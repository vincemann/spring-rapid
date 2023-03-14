package com.github.vincemann.springrapid.authdemo.dto;

import com.github.vincemann.springrapid.authdemo.dto.abs.MyIdDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserUpdatesOwnDto extends MyIdDto<Long> {
    private String name;
}
