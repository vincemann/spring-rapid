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

    public enum UpdateKind{
        FULL,
        PARTIAL,
        SOFT
    }

    public IdentifiableEntity oldEntity;
    private IdentifiableEntity updateEntity;
    private UpdateKind updateKind;
}
