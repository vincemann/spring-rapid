package io.github.vincemann.demo.service.plugin.proxy;

import io.github.vincemann.demo.model.Pet;
import io.github.vincemann.demo.repositories.PetRepository;
import io.github.vincemann.demo.service.PetService;
import io.github.vincemann.generic.crud.lib.service.plugin.CrudService_PluginProxy;

public class PetService_PluginProxy extends CrudService_PluginProxy<Pet,Long, PetRepository, PetService>
    implements PetService
{
    public PetService_PluginProxy(PetService crudService, Plugin<? super Pet, ? super Long>... plugins) {
        super(crudService, plugins);
    }
}
