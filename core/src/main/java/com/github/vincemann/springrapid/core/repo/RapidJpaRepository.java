package com.github.vincemann.springrapid.core.repo;

import com.github.vincemann.springrapid.core.model.IdentifiableEntity;
import com.sun.xml.bind.v2.model.core.ID;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.io.Serializable;
import java.util.List;

public interface RapidJpaRepository<T extends IdentifiableEntity<ID>,ID extends Serializable>
        extends JpaRepository<T, ID> {

}
