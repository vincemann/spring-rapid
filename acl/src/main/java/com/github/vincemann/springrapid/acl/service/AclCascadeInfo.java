package com.github.vincemann.springrapid.acl.service;

import com.github.vincemann.springrapid.auth.IdAware;
import org.springframework.security.acls.model.AccessControlEntry;
import org.springframework.util.Assert;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.function.Predicate;


public class AclCascadeInfo<S extends IdAware,T extends IdAware> {

    private Class<S> source;
    private Predicate<S> sourceFilter;
    private TargetSupplier<S,T> target;
    private Predicate<T> targetFilter;
    private Predicate<AccessControlEntry> aceFilter;


    public AclCascadeInfo(Class<S> source, Predicate<S> sourceFilter, Predicate<T> targetFilter, Predicate<AccessControlEntry> aceFilter, TargetSupplier<S,T> target) {
        this.source = source;
        this.sourceFilter = sourceFilter;
        this.target = target;
        this.targetFilter = targetFilter;
        this.aceFilter = aceFilter;
    }

    public Collection<T> getTargetCollection(S parent){
        Assert.notNull(target,"no target supplier set for cascade info: " + this);
        return target.get(parent);

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

    public Predicate<S> getSourceFilter() {
        return sourceFilter;
    }

    public TargetSupplier<S,T> getTarget() {
        return target;
    }

    public Predicate<T> getTargetFilter() {
        return targetFilter;
    }

    public Predicate<AccessControlEntry> getAceFilter() {
        return aceFilter;
    }

    @Override
    public String toString() {
        return "AclCascadeInfo{" +
                "source=" + source +
                '}';
    }

    public static <S extends IdAware,T extends IdAware> Builder<S,T> builder(){
        return new Builder<>();
    }

    public static final class Builder<S extends IdAware,T extends IdAware> {
        private Class<S> source;
        private Predicate<S> sourceFilter;
        private TargetSupplier<S, T> targetSupplier;
        private Predicate<T> targetFilter;
        private Predicate<AccessControlEntry> aceFilter;

        private Builder() {
        }


        public Builder<S,T> source(Class<S> source) {
            this.source = source;
            return this;
        }

        public Builder<S,T> sourceFilter(Predicate<S> sourceFilter) {
            this.sourceFilter = sourceFilter;
            return this;
        }

        public Builder<S,T> target(TargetSupplier<S, T> targetSupplier) {
            this.targetSupplier = targetSupplier;
            return this;
        }

        public Builder<S,T> targetFilter(Predicate<T> targetFilter) {
            this.targetFilter = targetFilter;
            return this;
        }

        public Builder<S,T> aceFilter(Predicate<AccessControlEntry> aceFilter) {
            this.aceFilter = aceFilter;
            return this;
        }

        public AclCascadeInfo<S,T> build() {
            return new AclCascadeInfo<>(source,sourceFilter, targetFilter, aceFilter, targetSupplier);
        }
    }
}
