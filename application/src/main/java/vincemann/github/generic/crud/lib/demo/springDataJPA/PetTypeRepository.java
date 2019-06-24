package vincemann.github.generic.crud.lib.demo.springDataJPA;

import vincemann.github.generic.crud.lib.demo.model.PetType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PetTypeRepository extends JpaRepository<PetType,Long> {
}
