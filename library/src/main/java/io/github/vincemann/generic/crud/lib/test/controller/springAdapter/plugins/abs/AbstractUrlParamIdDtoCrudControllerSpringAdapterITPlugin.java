package io.github.vincemann.generic.crud.lib.test.controller.springAdapter.plugins.abs;

import io.github.vincemann.generic.crud.lib.model.IdentifiableEntity;
import io.github.vincemann.generic.crud.lib.test.controller.springAdapter.UrlParamIdDtoCrudControllerSpringAdapterIT;
import org.springframework.http.ResponseEntity;

import java.io.Serializable;
import java.util.Set;

public class AbstractUrlParamIdDtoCrudControllerSpringAdapterITPlugin<Dto extends IdentifiableEntity<Id>, Id extends Serializable> {

    private UrlParamIdDtoCrudControllerSpringAdapterIT integrationTest;

    //updateTests callbacks
    public void onAfterUpdateEntityShouldFail(Dto oldEntity, Dto newEntity, ResponseEntity<String> responseEntity) throws Exception{}
    public void onBeforeUpdateEntityShouldFail(Dto oldEntity, Dto newEntity) throws Exception{}
    public void onAfterUpdateEntityShouldSucceed(Dto oldEntity, Dto newEntity, Dto responseDto) throws Exception{}
    public void onBeforeUpdateEntityShouldSucceed(Dto oldEntity, Dto newEntity) throws Exception{}

    public void onAfterCreateEntityShouldSucceed(Dto dtoToCreate, Dto responseDto) throws Exception{}
    public void onBeforeCreateEntityShouldSucceed(Dto dtoToCreate) throws Exception{}

    //public void onAfterCreateEntityShouldFail(Dto dtoToCreate) throws Exception{}
    //public void onBeforeCreateEntityShouldFail(Dto dtoToCreate,ResponseEntity<String> responseEntity) throws Exception{}

    public void onAfterDeleteEntityShouldSucceed(Id id, ResponseEntity<String> responseEntity) throws Exception{}
    public void onBeforeDeleteEntityShouldSucceed(Id id) throws Exception{}


    public void onAfterDeleteEntityShouldFail(Id id,ResponseEntity<String> responseEntity) throws Exception{}
    public void onBeforeDeleteEntityShouldFail(Id id) throws Exception{}


    public void onAfterFindEntityShouldSucceed(Id id, Dto responseDto) throws Exception{}
    public void onBeforeFindEntityShouldSucceed(Id id) throws Exception{}


    public void onBeforeFindAllEntitiesShouldSucceed() throws Exception{}
    public void onAfterFindAllEntitiesShouldSucceed(Set<? extends Dto> dtos) throws Exception{}


    public void setIntegrationTest(UrlParamIdDtoCrudControllerSpringAdapterIT integrationTest)  {
        this.integrationTest = integrationTest;
    }

    public UrlParamIdDtoCrudControllerSpringAdapterIT getIntegrationTest(){
        return integrationTest;
    }
}
