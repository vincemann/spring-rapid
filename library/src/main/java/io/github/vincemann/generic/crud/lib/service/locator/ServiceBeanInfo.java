package io.github.vincemann.generic.crud.lib.service.locator;

import io.github.vincemann.generic.crud.lib.model.IdentifiableEntity;
import io.github.vincemann.generic.crud.lib.service.CrudService;
import io.github.vincemann.generic.crud.lib.service.ServiceBeanType;
import lombok.*;
import org.springframework.stereotype.Service;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@EqualsAndHashCode
@ToString
public class ServiceBeanInfo {
    private Class<? extends IdentifiableEntity> entityClass;
    private List<Class<? extends Annotation>> beanTypeAnnotations = new ArrayList<>();
    private String name;

    public ServiceBeanInfo(Class<? extends IdentifiableEntity> entityClass) {
        this.entityClass = entityClass;
    }

    @Builder
    public ServiceBeanInfo(Class<? extends IdentifiableEntity> entityClass, List<Class<? extends Annotation>> beanTypeAnnotations, String name) {
        this.entityClass = entityClass;
        if(beanTypeAnnotations!=null) {
            this.beanTypeAnnotations = beanTypeAnnotations;
        }else {
            this.beanTypeAnnotations = new ArrayList<>();
        }
        this.name = name;
    }
}
