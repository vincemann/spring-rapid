package io.github.vincemann.demo.service.map;

import io.github.vincemann.demo.model.Specialty;
import io.github.vincemann.demo.service.SpecialtyService;
import org.springframework.stereotype.Service;
import org.springframework.context.annotation.Profile;

@Profile({"default", "map"})
@Service
public class SpecialtyMapService extends LongIdMapService<Specialty> implements SpecialtyService {
}
