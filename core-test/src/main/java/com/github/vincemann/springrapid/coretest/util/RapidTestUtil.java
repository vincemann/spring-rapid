package com.github.vincemann.springrapid.coretest.util;

import com.github.vincemann.springrapid.core.model.IdentifiableEntity;
import org.springframework.data.repository.CrudRepository;

import java.io.Serializable;
import java.util.Optional;

public class RapidTestUtil {

    public static <E extends IdentifiableEntity> E mustBePresentIn(CrudRepository repo, Serializable id){
        Optional byId = repo.findById(id);
        if (byId.isEmpty()){
            throw new IllegalArgumentException("No Entity found with id: " + id);
        }
        return (E) byId.get();
    }
}
