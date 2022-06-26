package com.github.vincemann.springrapid.coredemo.log;


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
@Table(name = "log_entity")
public class LogEntity extends IdentifiableEntityImpl<Long> {

    private String name;

    private Integer age;

    @Builder
    public LogEntity(String name, Integer age, Set<LogChild> logChildren1, Set<LogChild> logChildren2) {
        this.name = name;
        this.age = age;
        this.logChildren1 = logChildren1;
        this.logChildren2 = logChildren2;
    }

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "logEntity",fetch = FetchType.LAZY)
    @BiDirChildCollection(LogParent.class)
    @JsonManagedReference
    private Set<LogChild> logChildren1 = new HashSet<>();

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "logEntity",fetch = FetchType.LAZY)
    @BiDirChildCollection(LogParent.class)
    @JsonManagedReference
    private Set<LogChild> logChildren2 = new HashSet<>();

    @ManyToOne
    @JoinColumn(name = "log_parent_id")
    @JsonBackReference
    @BiDirParentEntity
    private LogParent logParent;
}
