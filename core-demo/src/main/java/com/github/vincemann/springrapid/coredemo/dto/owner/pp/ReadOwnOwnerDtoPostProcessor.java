package com.github.vincemann.springrapid.coredemo.dto.owner.pp;

import com.github.vincemann.springrapid.core.controller.dto.DtoPostProcessor;
import com.github.vincemann.springrapid.core.service.exception.BadEntityException;
import com.github.vincemann.springrapid.coredemo.dto.owner.ReadOwnOwnerDto;
import com.github.vincemann.springrapid.coredemo.model.Owner;
import org.springframework.stereotype.Component;

@Component
public class ReadOwnOwnerDtoPostProcessor implements DtoPostProcessor<ReadOwnOwnerDto, Owner> {

    @Override
    public boolean supports(Class<?> entityClazz, Class<?> dtoClass) {
        return dtoClass.equals(ReadOwnOwnerDto.class);
    }

    @Override
    public void postProcessDto(ReadOwnOwnerDto readOwnOwnerDto, Owner entity, String... fieldsToMap) throws BadEntityException {
        readOwnOwnerDto.setDirtySecret(Owner.SECRET);
    }
}
