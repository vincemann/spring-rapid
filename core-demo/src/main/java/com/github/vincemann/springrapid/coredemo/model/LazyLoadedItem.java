package com.github.vincemann.springrapid.coredemo.model;

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
@Table(name = "lazy_loaded_items")
public class LazyLoadedItem extends IdentifiableEntityImpl<Long> {

    @ManyToOne
    @JoinColumn(name = "lazy_item_id")
    @JsonBackReference
    @BiDirParentEntity
    private Owner owner;
    private String name;

    public LazyLoadedItem(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "LazyLoadedItem{ " +
                getName()+ " , " +
                getId().toString() +
                " }";
    }

}
