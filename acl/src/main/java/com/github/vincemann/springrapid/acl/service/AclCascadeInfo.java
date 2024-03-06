package com.github.vincemann.springrapid.acl.service;

import com.github.vincemann.springrapid.core.model.IdentifiableEntity;
import lombok.Builder;
import lombok.Getter;
import org.springframework.util.Assert;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;

@Getter
public class AclCascadeInfo {

    private Class<?> source;
    private EntityFilter<?> sourceFilter;
    private TargetSupplier targetSupplier;
    private EntityFilter<?> targetFilter;
    private AceFilter aceFilter;

    @Builder
    public AclCascadeInfo(Class<?> source, EntityFilter<?> sourceFilter, EntityFilter<?> targetFilter, AceFilter aceFilter, TargetSupplier targetSupplier) {
        this.source = source;
        this.sourceFilter = sourceFilter;
        this.targetSupplier = targetSupplier;
        this.targetFilter = targetFilter;
        this.aceFilter = aceFilter;
    }

    public Collection<IdentifiableEntity<?>> getTargetCollection(IdentifiableEntity<?> parent){
        Assert.notNull(targetSupplier,"no target supplier set for cascade info: " + this);
        return targetSupplier.get(parent);

    }

    /**
     * check if entity type matches directly or Collections generic parameter matches
     * @param entityClass
     * @return
     */

    protected boolean matches(Class<?> entityClass){
        if (source.equals(entityClass)) {
            return true;
        } else if (Collection.class.isAssignableFrom(source)) {
            Type genericType = source.getGenericSuperclass();
            if (genericType instanceof ParameterizedType) {
                ParameterizedType parameterizedType = (ParameterizedType) genericType;
                Type[] typeArguments = parameterizedType.getActualTypeArguments();
                if (typeArguments.length == 1 && typeArguments[0] instanceof Class) {
                    Class<?> collectionElementType = (Class<?>) typeArguments[0];
                    return collectionElementType.equals(entityClass);
                }
            }
        }
        return false;
    }

    @Override
    public String toString() {
        return "AclCascadeInfo{" +
                "source=" + source +
                '}';
    }
}
