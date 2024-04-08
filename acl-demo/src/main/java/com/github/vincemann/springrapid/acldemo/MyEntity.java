package com.github.vincemann.springrapid.acldemo;

import com.github.vincemann.springrapid.acl.IdAware;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.MappedSuperclass;
import java.util.Objects;

@MappedSuperclass
public abstract class MyEntity implements IdAware<Long> {

    @GeneratedValue(strategy = GenerationType.AUTO)
    @jakarta.persistence.Id
    private Long id;

    public MyEntity() {
    }

    @Override
    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id=id;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MyEntity other = (MyEntity) o;
        // added null check here, otherwise entities with null ids are considered equal
        // and cut down to one entity in a set for example
        return id != null &&
                id.equals(other.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
