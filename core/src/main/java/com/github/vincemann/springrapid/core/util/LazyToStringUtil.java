package com.github.vincemann.springrapid.core.util;

import com.github.vincemann.springrapid.core.model.IdentifiableEntity;
import org.hibernate.LazyInitializationException;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceUnitUtil;
import java.io.Serializable;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

public class LazyToStringUtil {

    public static final String UNLOADED_IGNORED = "unloaded";

    private static EntityManager entityManager;

    public static boolean isLoaded(Object entity){
        if (entityManager == null)
            return true;
        PersistenceUnitUtil persistenceUtil =
                entityManager.getEntityManagerFactory().getPersistenceUnitUtil();
        return persistenceUtil.isLoaded(entity);
    }

    public static <T> String toStringIfLoaded(Set<T> entities, Function<? super T, ? extends String> mapper){
        try {
            if (entities == null)
                return "null";
            if (!isLoaded(entities))
                return UNLOADED_IGNORED;
            else
                return entities.stream()
                        .map(mapper)
                        .collect(Collectors.toSet())
                        .toString();
        }catch (LazyInitializationException e){
            return "<lazy-init exception>";
        }
    }

    public static <T extends IdentifiableEntity> String toIdIfLoaded(Set<T> entities){

        try {
            if (entities == null)
                return "null";
            if (!isLoaded(entities))
                return UNLOADED_IGNORED;
            else
                return entities.stream()
                        .map(IdentifiableEntity::getId)
                        .collect(Collectors.toSet())
                        .toString();
        }catch (LazyInitializationException e){
            return "<lazy-init exception>";
        }
    }

    public static <T extends IdentifiableEntity> String toIdIfLoaded(T entity){

        try {
            if (entity == null)
                return "null";
            if (!isLoaded(entity))
                return UNLOADED_IGNORED;
            else{
                Serializable id = entity.getId();
                if (id == null)
                    return "null";
                else
                    return id.toString();
            }

        }catch (LazyInitializationException e){
            return "<lazy-init exception>";
        }
    }

    public static <T extends IdentifiableEntity> String toStringIfLoaded(T entity, Function<? super T, ? extends String> mapper){

        try {
            if (entity == null)
                return "null";
            if (!isLoaded(entity))
                return UNLOADED_IGNORED;
            else
                return mapper.apply(entity);
        }catch (LazyInitializationException e){
            return "<lazy-init exception>";
        }
    }

    public static void setEntityManager(EntityManager entityManager) {
        LazyToStringUtil.entityManager = entityManager;
    }
}
