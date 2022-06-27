package com.github.vincemann.logutil.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.github.vincemann.springrapid.autobidir.model.parent.annotation.BiDirParentEntity;
import com.github.vincemann.springrapid.core.model.IdentifiableEntityImpl;
import lombok.*;

import javax.persistence.Entity;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Setter
@Getter
@NoArgsConstructor
@Entity
@Table(name = "eager_single_children")
@ToString
public class EagerSingleLogChild extends LogIdentifiableEntity {

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
