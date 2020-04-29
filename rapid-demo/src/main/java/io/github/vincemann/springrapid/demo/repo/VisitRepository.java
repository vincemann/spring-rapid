package io.github.vincemann.springrapid.demo.repo;

import io.github.vincemann.springrapid.demo.model.Visit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VisitRepository extends JpaRepository<Visit,Long> {
}
