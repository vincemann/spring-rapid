package vincemann.github.generic.crud.lib.demo.service.map;

import vincemann.github.generic.crud.lib.demo.model.Owner;
import vincemann.github.generic.crud.lib.demo.model.Pet;
import vincemann.github.generic.crud.lib.demo.service.OwnerService;
import vincemann.github.generic.crud.lib.demo.service.PetService;
import vincemann.github.generic.crud.lib.demo.service.PetTypeService;
import vincemann.github.generic.crud.lib.service.exception.BadEntityException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

import org.springframework.context.annotation.Profile;

@Profile({"default", "map"})
@Service
@AllArgsConstructor
public class OwnerMapService extends LongIdMapService<Owner> implements OwnerService {

    private PetTypeService petTypeService;
    private PetService petService;


    @Override
    public Optional<Owner> findByLastName(String lastName){
       return super.getMap().values().stream().filter(owner -> owner.getLastName().equals(lastName)).findFirst();
    }

    @Override
    public Owner update(Owner entity) {
        return null;
    }

    @Override
    public Owner save(Owner object) throws  BadEntityException {
        if(object != null){
            if (object.getPets() != null) {
                for (Pet pet : object.getPets()) {
                    if (pet.getPetType() != null) {
                        if (pet.getPetType().getId() == null) {
                            pet.setPetType(petTypeService.save(pet.getPetType()));
                        }
                    } else {
                        throw new RuntimeException("Pet Type is required");
                    }

                    if (pet.getId() == null) {
                        Pet savedPet = petService.save(pet);
                        pet.setId(savedPet.getId());
                    }
                }
            }

            return super.save(object);

        } else {
            return null;
        }
    }
}
