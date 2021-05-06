package com.github.vincemann.springrapid.acldemo.service.extensions;

//@ServiceComponent
//@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
//public class OwnerFullAccessAboutSavedPetAclExtension extends AbstractAclExtension<PetService>
//        implements GenericCrudServiceExtension<PetService, Pet,Long> {
//
//    @Override
//    public Pet save(Pet entity) throws BadEntityException {
//        Pet savedPet = getNext().save(entity);
//        savePermissionForUserOverEntity(savedPet.getOwner().getUser().getEmail(),
//                savedPet,BasePermission.ADMINISTRATION);
//        return savedPet;
//    }
//}
