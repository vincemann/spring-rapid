package io.github.vincemann.demo.service.map;

import io.github.vincemann.demo.service.VetService;
import io.github.vincemann.demo.model.Specialty;
import io.github.vincemann.demo.model.Vet;
import io.github.vincemann.generic.crud.lib.service.exception.BadEntityException;
import io.github.vincemann.demo.service.SpecialtyService;
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
