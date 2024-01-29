package com.github.vincemann.springrapid.coredemo.service;

import com.github.vincemann.springrapid.core.service.CrudService;
import org.springframework.stereotype.Component;
import com.github.vincemann.springrapid.coredemo.model.ClinicCard;


@Component
public interface ClinicCardService extends CrudService<ClinicCard,Long> {
}
