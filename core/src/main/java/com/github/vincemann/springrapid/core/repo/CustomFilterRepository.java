package com.github.vincemann.springrapid.core.repo;

import com.github.vincemann.springrapid.core.model.IdentifiableEntity;
import com.github.vincemann.springrapid.core.service.JPQLEntityFilter;

import java.util.List;

public interface CustomFilterRepository<E extends IdentifiableEntity<?>> {

    List<E> findAll(List<JPQLEntityFilter<E>> filters);
}
