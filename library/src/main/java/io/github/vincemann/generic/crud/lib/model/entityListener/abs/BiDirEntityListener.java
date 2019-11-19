package io.github.vincemann.generic.crud.lib.model.entityListener.abs;

import io.github.vincemann.generic.crud.lib.service.finder.CrudServiceFinder;
import lombok.Getter;

@Getter
public abstract class BiDirEntityListener {

    private CrudServiceFinder crudServiceFinder;

    public BiDirEntityListener(CrudServiceFinder crudServiceFinder) {
        this.crudServiceFinder = crudServiceFinder;
    }

}
