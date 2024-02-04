package com.github.vincemann.springrapid.sync.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
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
    private String entityClass;

    @Id
    @Column(name = "entity_id")
    private String entityId;

    @OneToMany(mappedBy = "auditLog",fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<EntityDtoMapping> dtoMappings;

    public AuditLog(AuditId id) {
        this.entityClass = id.getEntityClass();
        this.entityId = id.getEntityId();
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
