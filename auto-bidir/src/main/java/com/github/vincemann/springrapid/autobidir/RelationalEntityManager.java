package com.github.vincemann.springrapid.autobidir;

import com.github.vincemann.springrapid.core.model.IdentifiableEntity;

public interface RelationalEntityManager {

    public <E extends IdentifiableEntity> E save(E entity);
    public void remove(IdentifiableEntity entity);
    public <E extends IdentifiableEntity> E update(E entity, Boolean full);
}
