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

    public enum OperationType {
        FULL,
        PARTIAL,
        SOFT,
        CREATE
    }

//    public IdentifiableEntity oldEntity;
    private IdentifiableEntity detachedUpdateEntity;
    private IdentifiableEntity detachedSourceEntity;
    private OperationType operationType;
}
