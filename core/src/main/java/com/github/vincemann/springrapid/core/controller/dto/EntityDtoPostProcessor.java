package com.github.vincemann.springrapid.core.controller.dto;

public interface EntityDtoPostProcessor<Dto,E/* extends IdentifiableEntity<?>*/>
        extends DtoPostProcessor<Dto,E>, EntityPostProcessor<Dto,E> {
}
