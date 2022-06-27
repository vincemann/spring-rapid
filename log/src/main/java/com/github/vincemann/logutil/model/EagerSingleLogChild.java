package com.github.vincemann.logutil.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.github.vincemann.springrapid.autobidir.model.parent.annotation.BiDirParentEntity;
import com.github.vincemann.springrapid.core.model.IdentifiableEntityImpl;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Setter
@Getter
@NoArgsConstructor
@Entity
@Table(name = "eager_single_children")
public class EagerSingleLogChild extends IdentifiableEntityImpl<Long> {

    @OneToOne(mappedBy = "eagerChild")
    @BiDirParentEntity
    private LogEntity logEntity;
    private String name;

    public EagerSingleLogChild(String name) {
        this.name = name;
    }

    @Builder
    public EagerSingleLogChild(LogEntity logEntity, String name) {
        this.logEntity = logEntity;
        this.name = name;
    }
}
