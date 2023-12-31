package com.github.vincemann.springrapid.core.model;


//import com.github.vincemann.smartlogger.SmartLogger;
import com.github.vincemann.springrapid.core.util.ProxyUtils;
import lombok.NoArgsConstructor;
import org.springframework.util.ReflectionUtils;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.MappedSuperclass;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.Objects;

@MappedSuperclass
@NoArgsConstructor
public class IdentifiableEntityImpl<Id extends Serializable>
        implements IdentifiableEntity<Id> {


    @GeneratedValue(strategy = GenerationType.AUTO)
    @javax.persistence.Id
    private Id id;

    @Override
    public Id getId() {
        return this.id;
    }

    @Override
    public void setId(Id id) {
        this.id=id;
    }

    /**
     * uses default constructor to create instance of this and nullifies all collections
     * -> make sure to always use this method to create instances used for {@link com.github.vincemann.springrapid.core.service.CrudService#partialUpdate(IdentifiableEntity, String...)}
     */
    public static <T> T createUpdate(Class<T> clazz) {
//        Class<?> clazz = this.getClass();
        // Create an instance of the class using Spring's ReflectionUtils

        try {
            T instance = clazz.getDeclaredConstructor().newInstance();
            // Use Spring's ReflectionUtils to iterate through fields
            ReflectionUtils.doWithFields(clazz, field -> {
                field.setAccessible(true); // Make the field accessible

                // Check if the field is of type Collection
                if (Collection.class.isAssignableFrom(field.getType())) {
                    // Set the field to null
                    field.set(instance, null);
                }
            });
            return instance;
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        IdentifiableEntityImpl<?> other = (IdentifiableEntityImpl<?>) o;
        // added null check here, otherwise entities with null ids are considered equal
        // and cut down to one entity in a set for example
        return id != null &&
                id.equals(other.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
