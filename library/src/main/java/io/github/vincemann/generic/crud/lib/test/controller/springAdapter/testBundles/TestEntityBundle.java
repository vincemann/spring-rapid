package io.github.vincemann.generic.crud.lib.test.controller.springAdapter.testBundles;

import io.github.vincemann.generic.crud.lib.model.IdentifiableEntity;
import io.github.vincemann.generic.crud.lib.test.controller.springAdapter.testBundles.update.UpdateTestEntityBundleIteration;
import io.github.vincemann.generic.crud.lib.test.controller.springAdapter.testRequestEntity.TestRequestEntityModification;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.junit.jupiter.api.Assertions;
import org.springframework.lang.Nullable;

@Getter
public abstract class TestEntityBundle<E extends IdentifiableEntity> {
    @Setter
    private E entity;

    /**
     * Set this, if you dont want the default {@link io.github.vincemann.generic.crud.lib.test.controller.springAdapter.testRequestEntity.TestRequestEntity} that will be generated by the {@link io.github.vincemann.generic.crud.lib.test.controller.springAdapter.testRequestEntity.factory.TestRequestEntityFactory}
     * but instead, you want to edit it, you can with {@link TestRequestEntityModification} for this specific tested Entity for this specfic test.
     *
     * For Update Tests see {@link UpdateTestEntityBundleIteration}.
     */
    @Nullable
    private TestRequestEntityModification testRequestEntityModification;
    
    public TestEntityBundle(E entity,@Nullable TestRequestEntityModification testRequestEntityModification) {
        this.entity=entity;
        this.testRequestEntityModification = testRequestEntityModification;
    }

    public TestEntityBundle(E entity) {
        this.entity = entity;
    }

    protected void verifyBundle(){
        Assertions.assertNotNull(entity);
    }
}
