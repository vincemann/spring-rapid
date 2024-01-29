package com.github.vincemann.springrapid.auth.dto.user;

import com.github.vincemann.springrapid.core.dto.IdAwareDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
//can only see id
public class FindForeignUserDto extends IdAwareDto<String> {

}
