package com.github.vincemann.springrapid.auth.dto.user;

import com.github.vincemann.springrapid.core.model.IdentifiableEntityImpl;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter

@NoArgsConstructor
//can only see id
public class RapidFindForeignUserDto extends IdentifiableEntityImpl<String> {

}
