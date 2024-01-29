package com.github.vincemann.springrapid.syncdemo.service;

import com.github.vincemann.springrapid.core.service.CrudService;
import org.springframework.stereotype.Component;
import com.github.vincemann.springrapid.syncdemo.model.Vet;

import java.util.Optional;

@Component
public interface VetService extends CrudService<Vet,Long> {

    public Optional<Vet> findByLastName(String lastName);
}
