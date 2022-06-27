package com.github.vincemann.logutil.model;


import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.github.vincemann.springrapid.autobidir.model.child.annotation.BiDirChildCollection;
import com.github.vincemann.springrapid.autobidir.model.parent.annotation.BiDirParentEntity;
import com.github.vincemann.springrapid.core.model.IdentifiableEntityImpl;
import lombok.Builder;
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
public class LogParent extends LogIdentifiableEntity {


    @OneToMany(cascade = CascadeType.ALL, mappedBy = "lazyParent",fetch = FetchType.LAZY)
    @BiDirChildCollection(LogParent.class)
    @JsonManagedReference
    private Set<LogEntity> logEntities = new HashSet<>();

    private String name;

    public LogParent(String name) {
        this.name = name;
    }

    @Builder
    public LogParent(Set<LogEntity> logEntities, String name) {
        if (logEntities!= null)
            this.logEntities = logEntities;
        this.name = name;
    }

//    @Override
//    public String toString() {
//        return "LazyExceptionItem{" +
//                "" + getName() +
//                "}";
//    }
}
