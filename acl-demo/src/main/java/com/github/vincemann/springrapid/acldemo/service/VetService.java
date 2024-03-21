package com.github.vincemann.springrapid.acldemo.service;

import com.github.vincemann.springrapid.acldemo.model.Vet;
import com.github.vincemann.springrapid.auth.service.UserService;

import java.util.Optional;

public interface VetService extends UserService<Vet,Long> {
    public Optional<Vet> findByLastName(String lastName);
}
