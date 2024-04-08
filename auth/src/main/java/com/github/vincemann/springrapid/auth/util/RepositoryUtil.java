package com.github.vincemann.springrapid.auth.util;

import org.springframework.data.repository.CrudRepository;

import java.io.Serializable;
import java.util.Optional;

public class RepositoryUtil {

    public static <T> T findPresentById(CrudRepository<T,Serializable>repository, Serializable id){
        Optional<T> byId = repository.findById(id);
        VerifyEntity.isPresent(byId,"cant find entity with id: " + id + " via repo: " + repository.getClass().getSimpleName());
        return byId.get();
    }
}
