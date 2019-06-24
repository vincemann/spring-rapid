package vincemann.github.generic.crud.lib.demo.service.map;

import vincemann.github.generic.crud.lib.demo.model.Specialty;
import vincemann.github.generic.crud.lib.demo.model.Vet;
import vincemann.github.generic.crud.lib.service.exception.BadEntityException;
import vincemann.github.generic.crud.lib.demo.service.SpecialtyService;
import vincemann.github.generic.crud.lib.demo.service.VetService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.context.annotation.Profile;

@Profile({"default", "map"})
@Service
@AllArgsConstructor
public class VetMapService extends LongIdMapService<Vet> implements VetService {

    private final SpecialtyService specialtyService;

    @Override
    public Vet save(Vet object) throws  BadEntityException {

        if (object.getSpecialties().size() > 0){
            for (Specialty specialty : object.getSpecialties()) {
                if (specialty.getId() == null) {
                    Specialty savedSpecialty = specialtyService.save(specialty);
                    specialty.setId(savedSpecialty.getId());
                }
            }
        }
        return super.save(object);
    }
}
