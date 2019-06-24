package vincemann.github.generic.crud.lib.demo.springDataJPA;

import vincemann.github.generic.crud.lib.demo.model.Visit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VisitRepository extends JpaRepository<Visit,Long> {
}
