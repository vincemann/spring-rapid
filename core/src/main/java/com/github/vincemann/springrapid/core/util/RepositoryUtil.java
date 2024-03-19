package com.github.vincemann.springrapid.core.util;

import com.github.vincemann.springrapid.core.service.exception.EntityNotFoundException;
import com.github.vincemann.springrapid.core.util.VerifyEntity;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public abstract class RepositoryUtil {

    private RepositoryUtil(){}

    public static <E,Id> E findPresentById(CrudRepository<E,Id> repo, Id id) throws EntityNotFoundException {
        Optional<E> byId = repo.findById(id);
        VerifyEntity.isPresent(byId,"no entity found with id : " + id
                 + ", managed by: " + repo.getClass().getSimpleName());
        return byId.get();
    }
}
