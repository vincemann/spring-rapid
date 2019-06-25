package io.github.vincemann.demo.service.map;

import io.github.vincemann.demo.model.Pet;
import io.github.vincemann.demo.service.PetService;
import org.springframework.stereotype.Service;

import org.springframework.context.annotation.Profile;

@Profile({"default", "map"})
@Service
public class PetMapService extends LongIdMapService<Pet> implements PetService {
}
