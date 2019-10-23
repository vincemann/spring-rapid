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
public class UpdateTestEntityBundleContainer<ServiceE extends IdentifiableEntity, Dto extends IdentifiableEntity> extends TestEntityBundle<ServiceE> {

    @Setter
    private List<UpdateTestEntityBundle<Dto>> updateTestEntityBundles = new ArrayList<>();

    @Builder
    public UpdateTestEntityBundleContainer(ServiceE entity, List<UpdateTestEntityBundle<Dto>> updateTestEntityBundles) {
        super(entity);
        if(updateTestEntityBundles!=null) {
            this.updateTestEntityBundles = updateTestEntityBundles;
        }
    }

    public UpdateTestEntityBundleContainer(ServiceE entity, Dto... modifiedEntities){
        super(entity);
        for (Dto modEntity : modifiedEntities) {
            this.updateTestEntityBundles.add(new UpdateTestEntityBundle<>(modEntity));
        }
        verifyBundle();
    }


    @Override
    protected void verifyBundle() {
        super.verifyBundle();
        updateTestEntityBundles.forEach(bundle -> {
            Assertions.assertNotNull(bundle);
            Assertions.assertNotNull(bundle.getModifiedEntity());
            Assertions.assertFalse(BeanUtils.isDeepEqual(getEntity(),bundle.getModifiedEntity()),"ModifiedEntity must differ from Entity");
        });
    }
}
