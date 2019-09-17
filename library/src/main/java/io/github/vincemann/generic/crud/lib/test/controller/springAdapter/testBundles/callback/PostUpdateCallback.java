package io.github.vincemann.generic.crud.lib.test.controller.springAdapter.testBundles.callback;

import io.github.vincemann.generic.crud.lib.model.IdentifiableEntity;

/**
 * dto param in callbackmethod is the dto, that represents the latest state of the entity in the database, after the update operation
 * if the update operation was successful, then it represents the updated Entity.
 * if the update operation failed, then it represents the previously saved Entity.
 * @param <Dto>
 */
public interface PostUpdateCallback<Dto extends IdentifiableEntity> extends PostTestCallback<Dto>{
}
