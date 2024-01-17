package com.github.vincemann.springrapid.acl.service;

import com.github.hervian.reflection.Types;
import com.github.vincemann.springrapid.core.model.IdentifiableEntity;
import lombok.Builder;
import lombok.Getter;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;

@Getter
public class AclCascadeInfo {

    private Class<?> source;
    private EntityFilter<?> sourceFilter;
    private Method target;
    private TargetSupplier targetSupplier;
    private EntityFilter<?> targetFilter;
    private AceFilter aceFilter;

    @Builder
    public AclCascadeInfo(Class<?> source, EntityFilter<?> sourceFilter, Types.Supplier<?> target, EntityFilter<?> targetFilter, AceFilter aceFilter,
                          TargetSupplier targetSupplier) {
        this.source = source;
        this.sourceFilter = sourceFilter;
        if (target!=null){
            try {
                this.target = Types.createMethod(target);
            }catch (Exception e){
                throw new IllegalArgumentException("Could not extract method from method ref");
            }
        }
        this.targetSupplier = targetSupplier;
        this.targetFilter = targetFilter;
        this.aceFilter = aceFilter;
        if (targetSupplier != null && target != null)
            throw new IllegalArgumentException("can only set target method ref or target supplier");
    }

    public Collection<IdentifiableEntity<?>> getTargetCollection(IdentifiableEntity<?> parent){
        if (target!= null){
            try {
                return (Collection<IdentifiableEntity<?>>) getTarget().invoke(parent);
            } catch (IllegalAccessException | InvocationTargetException e) {
                throw new RuntimeException(e);
            }
        }else if (targetSupplier != null){
            return targetSupplier.get(parent);
        }else{
            throw new IllegalArgumentException("no target or target supplier found");
        }
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
}
