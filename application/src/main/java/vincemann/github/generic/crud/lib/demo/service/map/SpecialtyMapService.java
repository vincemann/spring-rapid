package vincemann.github.generic.crud.lib.demo.service.map;

import vincemann.github.generic.crud.lib.demo.model.Specialty;
import vincemann.github.generic.crud.lib.demo.service.SpecialtyService;
import org.springframework.stereotype.Service;
import org.springframework.context.annotation.Profile;

@Profile({"default", "map"})
@Service
public class SpecialtyMapService extends LongIdMapService<Specialty> implements SpecialtyService {
}
