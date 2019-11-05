package io.github.vincemann.demo.config;

import io.github.vincemann.demo.model.Owner;
import io.github.vincemann.demo.model.Pet;
import io.github.vincemann.demo.service.*;
import io.github.vincemann.demo.service.plugins.AclPlugin;
import io.github.vincemann.demo.service.plugins.PersonNameSavingPlugin;
import io.github.vincemann.demo.service.springDataJPA.decorator.adapter.*;
import io.github.vincemann.generic.crud.lib.service.decorator.DecorationQualifier;
import io.github.vincemann.generic.crud.lib.service.decorator.implementations.TransactionalCrudServiceDecorator;
import io.github.vincemann.generic.crud.lib.service.plugin.BiDirChildPlugin;
import io.github.vincemann.generic.crud.lib.service.plugin.BiDirParentPlugin;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class ServiceConfig {

    @Primary
    @Bean
    public OwnerService ownerService(
            @Qualifier(DecorationQualifier.UNDECORATED) OwnerService ownerService,
            AclPlugin aclPlugin,
            BiDirParentPlugin<Owner, Long> biDirParentPlugin,
            PersonNameSavingPlugin personNameSavingPlugin
    ) {
        return OwnerServiceDecoratorAdapter.builder()
                .ownerService(ownerService)
                .crudServiceDecorator(
                        new TransactionalPluginServiceDecorator<Owner, Long>(
                                ownerService,
                                //service Plugins
                                biDirParentPlugin,
                                aclPlugin,
                                personNameSavingPlugin
                        )
                ).build();
    }

    @Primary
    @Bean
    public PetService petService(
            @Qualifier(DecorationQualifier.UNDECORATED) PetService petService,
            BiDirChildPlugin<Pet, Long> biDirChildPlugin,
            AclPlugin aclPlugin
    ) {
        return PetServiceDecoratorAdapter.builder()
                .undecoratedService(petService)
                .crudServiceDecorator(
                        new TransactionalPluginServiceDecorator<Pet, Long>(
                                petService,
                                biDirChildPlugin,
                                aclPlugin
                        )
                )
                .build();
    }

    @Primary
    @Bean
    public VetService vetService(
            @Qualifier(DecorationQualifier.UNDECORATED) VetService vetService,
            AclPlugin aclPlugin,
            PersonNameSavingPlugin personNameSavingPlugin
    ){
        return VetServiceDecoratorAdapter.builder()
                .undecoratedService(vetService)
                .crudServiceDecorator(new TransactionalPluginServiceDecorator<>(
                        vetService,
                        aclPlugin,
                        personNameSavingPlugin
                )).build();
    }

    @Primary
    @Bean
    public SpecialtyService specialtyService(
            @Qualifier(DecorationQualifier.UNDECORATED) SpecialtyService specialtyService
    ){
        return SpecialtyDecoratorAdapter.builder()
                .undecoratedService(specialtyService)
                .crudServiceDecorator(new TransactionalCrudServiceDecorator<>(specialtyService))
                .build();
    }

    @Primary
    @Bean
    public VisitService visitService(
            @Qualifier(DecorationQualifier.UNDECORATED) VisitService visitService
    ){
        return VisitServiceDecoratorAdapter.builder()
                .undecoratedService(visitService)
                .crudServiceDecorator(new TransactionalCrudServiceDecorator<>(visitService))
                .build();
    }







}
