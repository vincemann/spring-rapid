package com.github.vincemann.springrapid.coredemo.log;


import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.github.vincemann.springrapid.autobidir.model.child.annotation.BiDirChildCollection;
import com.github.vincemann.springrapid.autobidir.model.parent.annotation.BiDirParentEntity;
import com.github.vincemann.springrapid.core.model.IdentifiableEntityImpl;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Setter
@Getter
@NoArgsConstructor
@Entity
@Table(name = "log_parents")
public class LogParent extends IdentifiableEntityImpl<Long> {


    @OneToMany(cascade = CascadeType.ALL, mappedBy = "logEntity",fetch = FetchType.LAZY)
    @BiDirChildCollection(LogParent.class)
    @JsonManagedReference
    private Set<LogEntity> logEntities = new HashSet<>();

    private String name;

    public LogParent(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "LazyExceptionItem{" +
                "" + getName() +
                "}";
    }
}
