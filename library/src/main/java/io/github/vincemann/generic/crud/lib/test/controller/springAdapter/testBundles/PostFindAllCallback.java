package io.github.vincemann.generic.crud.lib.test.controller.springAdapter.testBundles;

import io.github.vincemann.generic.crud.lib.model.IdentifiableEntity;

import java.util.Collection;

public interface PostFindAllCallback<Dto extends Collection<? extends IdentifiableEntity>> extends PostTestCallback<Dto> {

}
