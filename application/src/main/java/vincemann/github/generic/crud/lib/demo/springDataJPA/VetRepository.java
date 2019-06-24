package vincemann.github.generic.crud.lib.demo.springDataJPA;

import vincemann.github.generic.crud.lib.demo.model.Vet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VetRepository extends JpaRepository<Vet,Long> {
}
