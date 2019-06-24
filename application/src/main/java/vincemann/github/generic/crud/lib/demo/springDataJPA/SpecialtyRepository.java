package vincemann.github.generic.crud.lib.demo.springDataJPA;

import vincemann.github.generic.crud.lib.demo.model.Specialty;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SpecialtyRepository extends JpaRepository<Specialty,Long> {
}
