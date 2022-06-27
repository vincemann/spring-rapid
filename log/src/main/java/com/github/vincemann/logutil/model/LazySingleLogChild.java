package com.github.vincemann.logutil.model;

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
public class LazySingleLogChild extends LogIdentifiableEntity {

    @OneToOne(mappedBy = "lazyChild")
    @BiDirParentEntity
    private LogEntity logEntity;
    private String name;

    public LazySingleLogChild(String name) {
        this.name = name;
    }

    @Builder
    public LazySingleLogChild(LogEntity logEntity, String name) {
        this.logEntity = logEntity;
        this.name = name;
    }
}
