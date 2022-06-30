package com.github.vincemann.logutil.model;

import com.github.vincemann.springrapid.autobidir.model.parent.annotation.BiDirParentEntity;
import lombok.*;

import javax.persistence.Entity;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Setter
@Getter
@NoArgsConstructor
@Entity
@Table(name = "single_log_children")
@ToString
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
