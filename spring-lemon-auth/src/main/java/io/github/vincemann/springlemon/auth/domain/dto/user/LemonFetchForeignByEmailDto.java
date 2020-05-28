package io.github.vincemann.springlemon.auth.domain.dto.user;

import io.github.vincemann.springrapid.core.model.IdentifiableEntityImpl;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString(callSuper = true)
@NoArgsConstructor
//can only see id
public class LemonFetchForeignByEmailDto extends IdentifiableEntityImpl<String> {

}
