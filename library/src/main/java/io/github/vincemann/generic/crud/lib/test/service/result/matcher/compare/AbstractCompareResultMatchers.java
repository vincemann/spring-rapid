package io.github.vincemann.generic.crud.lib.test.service.result.matcher.compare;

import io.github.vincemann.generic.crud.lib.model.IdentifiableEntity;
import io.github.vincemann.generic.crud.lib.test.service.result.ServiceResult;
import lombok.Getter;
import lombok.Setter;

import java.util.Optional;

@Setter
public class AbstractCompareResultMatchers<T extends AbstractCompareResultMatchers> {
    @Getter
    private IdentifiableEntity toCompare;
    private boolean checkDbEntity = false;

    public boolean checkDbEntity() {
        return checkDbEntity;
    }

    public AbstractCompareResultMatchers(IdentifiableEntity toCompare) {
        this.toCompare = toCompare;
    }

    protected void resolveToCompare(ServiceResult serviceResult){
        try {
            if(toCompare==null){
                Object serviceResultEntity = serviceResult.getResult();
                if(serviceResultEntity instanceof Optional){
                    setToCompare(((IdentifiableEntity) ((Optional) serviceResultEntity).get()));
                }else {
                    setToCompare(((IdentifiableEntity) serviceResultEntity));
                }
            }
        }catch (ClassCastException e){
            throw new IllegalArgumentException("Service Result was chosen as compare object, but was not of type Entity or Optional.of(Entity)");
        }

    }

    public T withDbEntity(){
        this.checkDbEntity =true;
        return ((T) this);
    }
}
