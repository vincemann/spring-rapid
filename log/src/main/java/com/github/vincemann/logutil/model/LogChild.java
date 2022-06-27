package com.github.vincemann.logutil.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.github.vincemann.springrapid.autobidir.model.parent.annotation.BiDirParentEntity;
import com.github.vincemann.springrapid.core.model.IdentifiableEntityImpl;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
    public LogChild(LogEntity logEntity, String name) {
        this.logEntity = logEntity;
        this.name = name;
    }

    //    @Override
//    public String toString() {
//        return "LazyLoadedItem{ " +
//                getName()+ " , " +
//                getId().toString() +
//                " }";
//    }

}
