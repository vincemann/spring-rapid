package com.github.vincemann.springrapid.coredemo.log;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.github.vincemann.springrapid.autobidir.model.parent.annotation.BiDirParentEntity;
import com.github.vincemann.springrapid.core.model.IdentifiableEntityImpl;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Setter
@Getter
@NoArgsConstructor
@Entity
@Table(name = "single_log_children")
public class SingleLogChild extends IdentifiableEntityImpl<Long> {

    @OneToOne
    @JoinColumn(name = "log_entity_id")
    @JsonBackReference
    @BiDirParentEntity
    private LogEntity logEntity;
    private String name;

    public SingleLogChild(String name) {
        this.name = name;
    }

    @Builder
    public SingleLogChild(LogEntity logEntity, String name) {
        this.logEntity = logEntity;
        this.name = name;
    }
}
