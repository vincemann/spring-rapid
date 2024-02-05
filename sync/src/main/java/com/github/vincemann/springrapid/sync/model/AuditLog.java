package com.github.vincemann.springrapid.sync.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Optional;
import java.util.Set;

@Entity
@IdClass(AuditId.class)
@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
public class AuditLog {

    @Id
    @Column(name = "entity_class")
    @NotNull
    private String entityClass;

    @Id
    @Column(name = "entity_id")
    @NotNull
    private String entityId;

    @OneToMany(mappedBy = "auditLog",fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<EntityDtoMapping> dtoMappings;

    public AuditLog(AuditId id) {
        this.entityClass = id.getEntityClass();
        this.entityId = id.getEntityId();
    }

    public EntityDtoMapping findMapping(Class<?> dtoClass){
        Optional<EntityDtoMapping> entityDtoMapping = dtoMappings.stream()
                .filter(mapping -> mapping.getDtoClass().equals(dtoClass.getName()))
                .findFirst();
        if (entityDtoMapping.isEmpty()){
            throw new IllegalArgumentException("no mapping found for dto class: " + dtoClass);
        }
        return entityDtoMapping.get();
    }

    @Override
    public String toString() {
        return "AuditLog{" +
                "entityClass='" + entityClass + '\'' +
                ", entityId='" + entityId + '\'' +
                ", dtoMappings=" + dtoMappings +
                '}';
    }

    public String toShortString(){
        return "AuditLog{" +
                "entityClass='" + entityClass + '\'' +
                ", entityId='" + entityId + '\'' +
                '}';
    }
}
