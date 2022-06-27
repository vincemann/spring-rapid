package com.github.vincemann.logutil.model;


import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.github.vincemann.springrapid.autobidir.model.child.annotation.BiDirChildCollection;
import com.github.vincemann.springrapid.autobidir.model.child.annotation.BiDirChildEntity;
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

    @Builder
    public LogEntity(String name, Set<LogChild> lazyChildren1, Set<LogChild> lazyChildren2, Set<LogChild> eagerChildren, LogParent lazyParent) {
        this.name = name;
        if (lazyChildren1!=null)
            this.lazyChildren1 = lazyChildren1;
        if (lazyChildren2!=null)
            this.lazyChildren2 = lazyChildren2;
        if (eagerChildren != null)
            this.eagerChildren = eagerChildren;
        this.lazyParent = lazyParent;
    }

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "logEntity",fetch = FetchType.LAZY)
    @BiDirChildCollection(LogChild.class)
    @JsonManagedReference
    private Set<LogChild> lazyChildren1 = new HashSet<>();

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "logEntity",fetch = FetchType.LAZY)
    @BiDirChildCollection(LogChild.class)
    @JsonManagedReference
    private Set<LogChild> lazyChildren2 = new HashSet<>();

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "logEntity",fetch = FetchType.EAGER)
    @BiDirChildCollection(LogChild.class)
    @JsonManagedReference
    private Set<LogChild> eagerChildren = new HashSet<>();

    @OneToOne(fetch = FetchType.EAGER)
    @BiDirChildEntity
    @JoinColumn(name = "eager_child_id",referencedColumnName = "id")
    private LazySingleLogChild eagerChild;

    @OneToOne(fetch = FetchType.LAZY)
    @BiDirChildEntity
    @JoinColumn(name = "lazy_child_id",referencedColumnName = "id")
    private LazySingleLogChild lazyChild;


    @ManyToOne
    @JoinColumn(name = "log_parent_id")
    @BiDirParentEntity
    private LogParent lazyParent;
}
