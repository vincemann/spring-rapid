package io.github.vincemann.generic.crud.lib.test.controller.springAdapter;

import java.io.Serializable;

public interface NonExistingIdProvider<Id extends Serializable> {
    public Id provideId();
}
