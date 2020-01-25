package io.github.vincemann.demo.service.plugin.proxy;

import io.github.vincemann.demo.model.Pet;
import io.github.vincemann.demo.repositories.PetRepository;
import io.github.vincemann.demo.service.PetService;
import io.github.vincemann.generic.crud.lib.service.plugin.CrudServicePluginProxy;

public class PetServicePluginProxy extends CrudServicePluginProxy<Pet,Long, PetRepository, PetService>
    implements PetService
{
    public PetServicePluginProxy(PetService crudService, Plugin<? super Pet, ? super Long>... plugins) {
        super(crudService, plugins);
    }
}