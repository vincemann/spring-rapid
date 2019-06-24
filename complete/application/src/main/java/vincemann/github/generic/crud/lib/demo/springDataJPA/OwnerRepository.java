package vincemann.github.generic.crud.lib.demo.springDataJPA;

import vincemann.github.generic.crud.lib.demo.model.Owner;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OwnerRepository extends JpaRepository<Owner,Long> {

    Optional<Owner> findByLastName(String lastName);
}
