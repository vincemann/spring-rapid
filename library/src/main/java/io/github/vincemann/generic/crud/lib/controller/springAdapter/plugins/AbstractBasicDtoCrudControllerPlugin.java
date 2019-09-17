package io.github.vincemann.generic.crud.lib.controller.springAdapter.plugins;

import io.github.vincemann.generic.crud.lib.controller.BasicDtoCrudController;
import io.github.vincemann.generic.crud.lib.model.IdentifiableEntity;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Setter
@Getter
public abstract class AbstractBasicDtoCrudControllerPlugin<ServiceE extends IdentifiableEntity<Id>,Id extends Serializable> implements BasicDtoCrudControllerPlugin<ServiceE,Id> {

    private BasicDtoCrudController controller;

}
