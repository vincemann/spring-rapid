package com.github.vincemann.springrapid.core.controller.dto.mapper;

public interface DtoPostProcessor<Dto,E/* extends IdentifiableEntity<?>*/> extends EntityDtoPostProcessor<Dto,E>, DtoEntityPostProcessor<Dto,E>{
}
