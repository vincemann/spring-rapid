package io.github.vincemann.generic.crud.lib.test.controller.springAdapter.testBundles.update;

import io.github.vincemann.generic.crud.lib.model.IdentifiableEntity;
import io.github.vincemann.generic.crud.lib.test.controller.springAdapter.testBundles.TestEntityBundle;
import io.github.vincemann.generic.crud.lib.util.BeanUtils;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.junit.jupiter.api.Assertions;

import java.util.ArrayList;
import java.util.List;

@Getter
public class UpdateTestEntityBundle<ServiceE extends IdentifiableEntity, Dto extends IdentifiableEntity> extends TestEntityBundle<ServiceE> {

    @Setter
    private List<UpdateTestEntityBundleIteration<Dto>> updateTestEntityBundleIterations = new ArrayList<>();

    @Builder(builderMethodName = "Builder")
    public UpdateTestEntityBundle(ServiceE entity, List<UpdateTestEntityBundleIteration<Dto>> updateTestEntityBundleIterations) {
        super(entity);
        if(updateTestEntityBundleIterations !=null) {
            this.updateTestEntityBundleIterations = updateTestEntityBundleIterations;
        }
    }

    public UpdateTestEntityBundle(ServiceE entity, Dto... modifiedEntities){
        super(entity);
        for (Dto modEntity : modifiedEntities) {
            this.updateTestEntityBundleIterations.add(new UpdateTestEntityBundleIteration<>(modEntity));
        }
        verifyBundle();
    }


    @Override
    protected void verifyBundle() {
        super.verifyBundle();
        updateTestEntityBundleIterations.forEach(bundle -> {
            Assertions.assertNotNull(bundle);
            Assertions.assertNotNull(bundle.getModifiedEntity());
            Assertions.assertFalse(BeanUtils.isDeepEqual(getEntity(),bundle.getModifiedEntity()),"ModifiedEntity must differ from Entity");
        });
    }
}
