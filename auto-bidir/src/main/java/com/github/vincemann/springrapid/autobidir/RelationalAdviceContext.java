package com.github.vincemann.springrapid.autobidir;

import com.github.vincemann.springrapid.core.model.IdentifiableEntity;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
//@AllArgsConstructor
@Builder
public class RelationalAdviceContext {
    public IdentifiableEntity detachedOldEntity;
    private IdentifiableEntity detachedUpdateEntity;
    private Boolean fullUpdate;

}
