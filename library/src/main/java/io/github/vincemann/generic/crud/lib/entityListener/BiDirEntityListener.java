package io.github.vincemann.generic.crud.lib.entityListener;

import io.github.vincemann.generic.crud.lib.service.crudServiceFinder.CrudServiceFinder;
import lombok.Getter;

@Getter
public abstract class BiDirEntityListener {

    private CrudServiceFinder crudServiceFinder;

    public BiDirEntityListener(CrudServiceFinder crudServiceFinder) {
        this.crudServiceFinder = crudServiceFinder;
    }

}
