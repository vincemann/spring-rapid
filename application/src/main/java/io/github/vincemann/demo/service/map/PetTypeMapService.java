package io.github.vincemann.demo.service.map;

import io.github.vincemann.demo.model.PetType;
import io.github.vincemann.demo.service.PetTypeService;
import org.springframework.stereotype.Service;
import org.springframework.context.annotation.Profile;

@Profile({"default", "map"})
@Service
public class PetTypeMapService extends LongIdMapService<PetType> implements PetTypeService {
}
