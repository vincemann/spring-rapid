package io.github.vincemann.generic.crud.lib.test.service.testBundles;

import io.github.vincemann.generic.crud.lib.model.IdentifiableEntity;
import lombok.Builder;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class ServiceUpdateTestEntityBundle<ServiceE extends IdentifiableEntity> {
    private ServiceE entity;
    private List<ServiceUpdateTestEntityBundleIteration<ServiceE>> updateTestEntityBundleIterations = new ArrayList<>();

    public ServiceUpdateTestEntityBundle(ServiceE entity) {
        this.entity = entity;
    }

    @Builder
    public ServiceUpdateTestEntityBundle(ServiceE entity, List<ServiceUpdateTestEntityBundleIteration<ServiceE>> updateTestEntityBundleIterations) {
        this.entity = entity;
        if(updateTestEntityBundleIterations!=null){
            this.updateTestEntityBundleIterations = updateTestEntityBundleIterations;
        }
    }

    public ServiceUpdateTestEntityBundle(ServiceE entity, ServiceE... mods) {
        this.entity = entity;
        for (ServiceE mod : mods) {
            updateTestEntityBundleIterations.add(new ServiceUpdateTestEntityBundleIteration<>(mod));
        }
    }


}
