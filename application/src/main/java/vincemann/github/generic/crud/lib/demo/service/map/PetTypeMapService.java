package vincemann.github.generic.crud.lib.demo.service.map;

import vincemann.github.generic.crud.lib.demo.model.PetType;
import vincemann.github.generic.crud.lib.demo.service.PetTypeService;
import org.springframework.stereotype.Service;
import org.springframework.context.annotation.Profile;

@Profile({"default", "map"})
@Service
public class PetTypeMapService extends LongIdMapService<PetType> implements PetTypeService {
}
