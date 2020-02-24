package io.github.vincemann.generic.crud.lib.test.service.result.resultMatcher.compare;

import io.github.vincemann.generic.crud.lib.model.IdentifiableEntity;
import lombok.Getter;
import lombok.Setter;

@Setter
public class AbstractCompareResultMatchers<T extends AbstractCompareResultMatchers> {
    @Getter
    private IdentifiableEntity entity;
    private boolean checkReturnedEntity = false;
    private boolean checkDbEntity = false;

    public boolean checkReturnedEntity() {
        return checkReturnedEntity;
    }

    public boolean checkDbEntity() {
        return checkDbEntity;
    }

    public AbstractCompareResultMatchers(IdentifiableEntity entity) {
        this.entity = entity;
    }

    public T withReturnedEntity(){
        this.checkReturnedEntity =true;
        return ((T) this);
    }

    public T withDbEntity(){
        this.checkDbEntity =true;
        return ((T) this);
    }

    public T withReturnedAndDbEntity(){
        this.checkReturnedEntity =true;
        this.checkDbEntity =true;
        return ((T) this);
    }


}
