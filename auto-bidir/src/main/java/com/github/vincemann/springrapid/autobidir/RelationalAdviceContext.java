package com.github.vincemann.springrapid.autobidir;

import com.github.vincemann.springrapid.core.model.IdentifiableEntity;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

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
    private IdentifiableEntity detachedOldEntity;
    private OperationType operationType;
    private Set<String> whiteListedFields;
}
