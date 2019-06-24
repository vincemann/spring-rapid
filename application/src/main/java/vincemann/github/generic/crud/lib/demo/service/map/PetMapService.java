package vincemann.github.generic.crud.lib.demo.service.map;

import vincemann.github.generic.crud.lib.demo.model.Pet;
import vincemann.github.generic.crud.lib.demo.service.PetService;
import org.springframework.stereotype.Service;

import org.springframework.context.annotation.Profile;

@Profile({"default", "map"})
@Service
public class PetMapService extends LongIdMapService<Pet> implements PetService {
}
