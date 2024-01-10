package com.github.vincemann.springrapid.core.repo;

import com.github.vincemann.springrapid.core.model.IdentifiableEntity;
import com.sun.xml.bind.v2.model.core.ID;
import org.springframework.data.jpa.repository.JpaRepository;

import java.io.Serializable;
import java.util.Set;

public interface RapidJpaRepository<E extends IdentifiableEntity<Id>,Id extends Serializable>
        extends JpaRepository<E,Id>, FindSomeRepository<E,Id> {
}
