package io.github.vincemann.demo.service.plugin.proxy;

import io.github.vincemann.demo.model.Owner;
import io.github.vincemann.demo.repositories.OwnerRepository;
import io.github.vincemann.demo.service.OwnerService;
import io.github.vincemann.generic.crud.lib.service.plugin.CrudService_PluginProxy;

import java.util.Optional;

public class OwnerService_PluginProxy
        extends CrudService_PluginProxy<Owner,Long, OwnerRepository, OwnerService>
            implements OwnerService
{

    public OwnerService_PluginProxy(OwnerService crudService, Plugin<? super Owner, ? super Long>... plugins) {
        super(crudService, plugins);
    }

    @Override
    public Optional<Owner> findByLastName(String lastName) {
        return getCrudService().findByLastName(lastName);
    }
}
