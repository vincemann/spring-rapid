package com.github.vincemann.springrapid.sync.model;

import java.io.Serializable;
import java.util.Objects;

public class AuditId implements Serializable {
    private String entityClass;
    private String entityId;

    public AuditId() {
    }

    public AuditId(String entityClass, String entityId) {
        this.entityClass = entityClass;
        this.entityId = entityId;
    }

    public Class<?> getConvertedEntityClass(){
        try {
            return Class.forName(entityClass);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    // Getters and Setters
    public String getEntityClass() {
        return entityClass;
    }

    public void setEntityClass(String entityClass) {
        this.entityClass = entityClass;
    }

    public String getEntityId() {
        return entityId;
    }

    public void setEntityId(String entityId) {
        this.entityId = entityId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AuditId)) return false;
        AuditId auditId = (AuditId) o;
        return Objects.equals(entityClass, auditId.entityClass) &&
                Objects.equals(entityId, auditId.entityId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(entityClass, entityId);
    }
}
