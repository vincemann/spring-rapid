package com.github.vincemann.springrapid.auth.dto.user;

import com.github.vincemann.springrapid.core.dto.AbstractIdDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
//can only see id
public class RapidFindForeignUserDto extends AbstractIdDto<String> {

}
