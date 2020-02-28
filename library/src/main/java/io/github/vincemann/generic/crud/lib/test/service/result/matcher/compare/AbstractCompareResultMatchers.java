package io.github.vincemann.generic.crud.lib.test.service.result.matcher.compare;

import io.github.vincemann.generic.crud.lib.model.IdentifiableEntity;
import lombok.Getter;
import lombok.Setter;

@Setter
public class AbstractCompareResultMatchers<T extends AbstractCompareResultMatchers> {
    @Getter
    private IdentifiableEntity inputEntity;
    private boolean checkDbEntity = false;

    public boolean checkDbEntity() {
        return checkDbEntity;
    }

    public AbstractCompareResultMatchers(IdentifiableEntity inputEntity) {
        this.inputEntity = inputEntity;
    }

    public T withDbEntity(){
        this.checkDbEntity =true;
        return ((T) this);
    }
}
