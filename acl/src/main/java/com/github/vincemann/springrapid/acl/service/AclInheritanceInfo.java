package com.github.vincemann.springrapid.acl.service;

import com.github.hervian.reflection.Types;
import lombok.Builder;
import lombok.Getter;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Set;

@Getter
public class AclInheritanceInfo {
    private Class<?> source;
    private EntityFilter sourceFilter;
    private Method target;
    private EntityFilter targetFilter;
    private AceFilter aceFilter;

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

    @Builder
    public AclInheritanceInfo(Class<?> source, EntityFilter sourceFilter, Types.Supplier<?> target, EntityFilter targetFilter, AceFilter aceFilter) {
        this.source = source;
        this.sourceFilter = sourceFilter;
        this.target = Types.createMethod(target);
        this.targetFilter = targetFilter;
        this.aceFilter = aceFilter;
    }


}
