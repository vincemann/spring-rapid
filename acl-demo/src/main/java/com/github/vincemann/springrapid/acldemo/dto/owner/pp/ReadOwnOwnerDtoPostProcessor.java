package com.github.vincemann.springrapid.acldemo.dto.owner.pp;

import com.github.vincemann.springrapid.acldemo.dto.owner.ReadOwnOwnerDto;
import com.github.vincemann.springrapid.acldemo.model.Owner;
import com.github.vincemann.springrapid.core.controller.dto.DtoPostProcessor;
import com.github.vincemann.springrapid.core.service.exception.BadEntityException;
import org.springframework.stereotype.Component;

@Component
public class ReadOwnOwnerDtoPostProcessor implements DtoPostProcessor<ReadOwnOwnerDto, Owner> {

    @Override
    public boolean supports(Class<?> entityClazz, Class<?> dtoClass) {
        return dtoClass.equals(ReadOwnOwnerDto.class);
    }

    @Override
    public void postProcessDto(ReadOwnOwnerDto readOwnOwnerDto, Owner entity, String... fieldsToMap) throws BadEntityException {
        readOwnOwnerDto.setSecret(Owner.SECRET);
    }
}
