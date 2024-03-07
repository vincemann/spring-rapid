package com.github.vincemann.springrapid.acl.service;

import com.github.vincemann.springrapid.core.model.IdentifiableEntity;

import org.springframework.util.Assert;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;


public class AclCascadeInfo {

    private Class<?> source;
    private EntityFilter<?> sourceFilter;
    private TargetSupplier targetSupplier;
    private EntityFilter<?> targetFilter;
    private AceFilter aceFilter;


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

    public Class<?> getSource() {
        return source;
    }

    public EntityFilter<?> getSourceFilter() {
        return sourceFilter;
    }

    public TargetSupplier getTargetSupplier() {
        return targetSupplier;
    }

    public EntityFilter<?> getTargetFilter() {
        return targetFilter;
    }

    public AceFilter getAceFilter() {
        return aceFilter;
    }



    @Override
    public String toString() {
        return "AclCascadeInfo{" +
                "source=" + source +
                '}';
    }

    public static final class Builder {
        private Class<?> source;
        private EntityFilter<?> sourceFilter;
        private TargetSupplier targetSupplier;
        private EntityFilter<?> targetFilter;
        private AceFilter aceFilter;

        private Builder() {
        }

        public static Builder builder() {
            return new Builder();
        }

        public Builder source(Class<?> source) {
            this.source = source;
            return this;
        }

        public Builder sourceFilter(EntityFilter<?> sourceFilter) {
            this.sourceFilter = sourceFilter;
            return this;
        }

        public Builder targetSupplier(TargetSupplier<?> targetSupplier) {
            this.targetSupplier = targetSupplier;
            return this;
        }

        public Builder targetFilter(EntityFilter<?> targetFilter) {
            this.targetFilter = targetFilter;
            return this;
        }

        public Builder aceFilter(AceFilter aceFilter) {
            this.aceFilter = aceFilter;
            return this;
        }

        public AclCascadeInfo build() {
            return new AclCascadeInfo(source, sourceFilter, targetFilter, aceFilter, targetSupplier);
        }
    }
}
