package com.github.vincemann.springrapid.syncdemo.repo;



import com.github.vincemann.springrapid.syncdemo.model.ClinicCard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClinicCardRepository extends JpaRepository<ClinicCard,Long> {
}
