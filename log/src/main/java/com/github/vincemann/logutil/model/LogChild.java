package com.github.vincemann.logutil.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.github.vincemann.springrapid.autobidir.model.parent.annotation.BiDirParentEntity;
import com.github.vincemann.springrapid.core.util.LazyLogger;
import lombok.*;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Setter
@Getter
@NoArgsConstructor
@Entity
@Table(name = "log_children")
public class LogChild extends LogIdentifiableEntity {

    @ManyToOne
    @JoinColumn(name = "log_entity_id")
    @JsonBackReference
    @BiDirParentEntity
    private LogEntity logEntity;
    private String name;


    public LogChild(String name) {
        this.name = name;
    }

    @Builder
    public LogChild(Long id, String name, LogEntity logEntity) {
        setId(id);
        this.logEntity = logEntity;
        this.name = name;
    }


    @Override
    public String toString() {
        LazyLogger logger = LazyLogger.builder()
                .ignoreLazyException(Boolean.TRUE)
                .ignoreEntities(Boolean.FALSE)
                .onlyLogLoaded(Boolean.FALSE)
                .build();

        return logger.toString(this);
    }
}
