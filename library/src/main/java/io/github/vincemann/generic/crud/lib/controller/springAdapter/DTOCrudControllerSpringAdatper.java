package io.github.vincemann.generic.crud.lib.controller.springAdapter;

import io.github.vincemann.generic.crud.lib.controller.springAdapter.idFetchingStrategy.IdFetchingException;
import io.github.vincemann.generic.crud.lib.controller.springAdapter.idFetchingStrategy.IdFetchingStrategy;
import io.github.vincemann.generic.crud.lib.controller.springAdapter.mediaTypeStrategy.DTOReadingException;
import io.github.vincemann.generic.crud.lib.controller.springAdapter.mediaTypeStrategy.MediaTypeStrategy;
import io.github.vincemann.generic.crud.lib.controller.springAdapter.validationStrategy.ValidationStrategy;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import io.github.vincemann.generic.crud.lib.controller.BasicDTOCrudController;
import io.github.vincemann.generic.crud.lib.controller.exception.EntityMappingException;
import io.github.vincemann.generic.crud.lib.model.IdentifiableEntity;
import io.github.vincemann.generic.crud.lib.service.CrudService;
import io.github.vincemann.generic.crud.lib.service.EndpointService;
import io.github.vincemann.generic.crud.lib.service.exception.BadEntityException;
import io.github.vincemann.generic.crud.lib.service.exception.EntityNotFoundException;
import io.github.vincemann.generic.crud.lib.service.exception.NoIdException;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolationException;
import java.io.IOException;
import java.io.Serializable;
import java.util.stream.Collectors;

/**
 * Adapter that connects Springs Requirements for a Controller (which can be seen as an Interface),
 * with the {@link BasicDTOCrudController} Interface
 * fetches {@link Id} from UrlParameter
 *
 * /entityName/httpMethod?entityIdName=id
 *
 * examples:
 *
 * /account/get?accountId=34
 * /account/get?accountId=44bedc08-8e71-11e9-bc42-526af7764f64
 *
 *
 */
public abstract class DTOCrudControllerSpringAdatper<ServiceE extends IdentifiableEntity<Id>,DTO extends IdentifiableEntity<Id>,Id extends Serializable, Service extends CrudService<ServiceE,Id>> extends BasicDTOCrudController<ServiceE,Id, Service,DTO> {


    private EndpointService endpointService;
    private String entityNameInUrl;
    private String baseUrl;
    private Class<DTO> dtoClass;
    private String findMethodName ="get";
    private String createMethodName="create";
    private String deleteMethodName="delete";
    private String updateMethodName="update";
    private IdFetchingStrategy<Id> idIdFetchingStrategy;
    private MediaTypeStrategy mediaTypeStrategy;
    private ValidationStrategy<DTO,Id> validationStrategy;

    //todo methoden einbauen die einfach nur die id returnen, siehe BasicDTOCrudController


    public DTOCrudControllerSpringAdatper(Service crudService, EndpointService endpointService, String entityNameInUrl, Class<DTO> dtoClass, IdFetchingStrategy<Id> idIdFetchingStrategy, MediaTypeStrategy mediaTypeStrategy, ValidationStrategy<DTO, Id> validationStrategy) {
        super(crudService);
        this.endpointService = endpointService;
        this.entityNameInUrl = entityNameInUrl;
        this.dtoClass = dtoClass;
        this.baseUrl="/"+entityNameInUrl+"/";
        this.idIdFetchingStrategy=idIdFetchingStrategy;
        this.mediaTypeStrategy=mediaTypeStrategy;
        this.validationStrategy = validationStrategy;
        initRequestMapping();
    }

    public DTOCrudControllerSpringAdatper(Service crudService, EndpointService endpointService, Class<ServiceE> entityClass, Class<DTO> dtoClass, IdFetchingStrategy<Id> idIdFetchingStrategy, MediaTypeStrategy mediaTypeStrategy, ValidationStrategy<DTO, Id> validationStrategy) {
        super(crudService);
        this.endpointService = endpointService;
        this.entityNameInUrl = entityClass.getSimpleName();
        this.dtoClass = dtoClass;
        this.validationStrategy = validationStrategy;
        this.baseUrl="/"+entityNameInUrl+"/";
        this.idIdFetchingStrategy=idIdFetchingStrategy;
        this.mediaTypeStrategy=mediaTypeStrategy;
        initRequestMapping();
    }


    private void initRequestMapping(){
        try {
            //CREATE
            endpointService.addMapping(getCreateRequestMappingInfo(),
                    this.getClass().getMethod("create", HttpServletRequest.class), this);

            //GET
            endpointService.addMapping(getGetRequestMappingInfo(),
                    this.getClass().getMethod("find", HttpServletRequest.class),this);

            //UPDATE
            endpointService.addMapping(getUpdateRequestMappingInfo(),
                    this.getClass().getMethod("update", HttpServletRequest.class),this);

            //DELETE
            endpointService.addMapping(getDeleteRequestMappingInfo(),
                    this.getClass().getMethod("delete", HttpServletRequest.class),this);

        }catch (NoSuchMethodException e){
            //should never happen
            throw new IllegalStateException(e);
        }
    }

    protected RequestMappingInfo getGetRequestMappingInfo(){
        String getUrl = baseUrl+ getFindMethodName();
        return RequestMappingInfo
                .paths(getUrl)
                .methods(RequestMethod.GET)
                .produces(mediaTypeStrategy.getMediaType())
                .build();
    }

    protected RequestMappingInfo getDeleteRequestMappingInfo(){
        String deleteUrl = baseUrl+getDeleteMethodName();
        return RequestMappingInfo
                .paths(deleteUrl)
                .methods(RequestMethod.DELETE)
                .build();
    }

    protected RequestMappingInfo getCreateRequestMappingInfo(){
        String createUrl = baseUrl+getCreateMethodName();
        return RequestMappingInfo
                .paths(createUrl)
                .methods(RequestMethod.POST)
                .consumes(mediaTypeStrategy.getMediaType())
                .produces(mediaTypeStrategy.getMediaType())
                .build();
    }

    protected RequestMappingInfo getUpdateRequestMappingInfo(){
        String updateUrl = baseUrl+getUpdateMethodName();
        return RequestMappingInfo
                .paths(updateUrl)
                .methods(RequestMethod.PUT)
                .consumes(mediaTypeStrategy.getMediaType())
                .produces(mediaTypeStrategy.getMediaType())
                .build();
    }


    public ResponseEntity<DTO> find(HttpServletRequest request) throws IdFetchingException, EntityNotFoundException, NoIdException, EntityMappingException {
        Id id = idIdFetchingStrategy.fetchId(request);
        validationStrategy.validateId(id,request);
        beforeFind(id,request);
        return super.find(id);
    }

    public ResponseEntity<DTO> create(HttpServletRequest request) throws DTOReadingException, EntityMappingException, BadEntityException {
        try {
            String body = request.getReader().lines().collect(Collectors.joining(System.lineSeparator()));
            DTO dto = mediaTypeStrategy.readDTOFromBody(body,dtoClass);
            validationStrategy.validateDTO(dto,request);
            beforeCreate(dto,request);
            return super.create(dto);
        }catch (IOException e){
            throw new DTOReadingException(e);
        }
    }

    public ResponseEntity<DTO> update(HttpServletRequest request) throws DTOReadingException, EntityMappingException, EntityNotFoundException, NoIdException, BadEntityException {
        try {
            String body = request.getReader().lines().collect(Collectors.joining(System.lineSeparator()));
            DTO dto = mediaTypeStrategy.readDTOFromBody(body,dtoClass);
            validationStrategy.validateDTO(dto,request);
            beforeUpdate(dto,request);
            return super.update(dto);
        }catch (IOException e){
            throw new DTOReadingException(e);
        }


    }

    public ResponseEntity delete(HttpServletRequest request) throws IdFetchingException, NoIdException, EntityNotFoundException, ConstraintViolationException {
        Id id = idIdFetchingStrategy.fetchId(request);
        validationStrategy.validateId(id,request);
        beforeDelete(id,request);
        return super.delete(id);
    }

    protected void beforeCreate(DTO dto, HttpServletRequest httpServletRequest){}
    protected void beforeUpdate(DTO dto, HttpServletRequest httpServletRequest){}
    protected void beforeDelete(Id id, HttpServletRequest httpServletRequest){}
    protected void beforeFind(Id id, HttpServletRequest httpServletRequest){}



    /*private Long getEntityIdFromPathVar(String url) throws MissingIdException {
        Path urlPath = Paths.find(url);
        String entityId=null;
        int index=0;
        for (Path path: urlPath){
            if(path.toString().equals(entityNameInUrl)){
                if(urlPath.getNameCount()>=index+1) {
                    entityId = urlPath.getName(index + 1).toString();
                }else {
                    throw new MissingIdException("Could not retrieve PathVar " + entityIdParamKey + " from url");
                }
                break;
            }
        }

        if(entityId==null){
            throw new MissingIdException("Could not retrieve PathVar " + entityIdParamKey + " from url");
        }
        try {
            return Long.parseLong(entityId);
        }catch (NumberFormatException e){
            throw new MissingIdException("PathVar " + entityIdParamKey + " is not of Type Long",e);
        }

    }*/


    public String getEntityNameInUrl() {
        return entityNameInUrl;
    }

    public Class<DTO> getDtoClass() {
        return dtoClass;
    }

    public String getFindMethodName() {
        return findMethodName;
    }

    public String getCreateMethodName() {
        return createMethodName;
    }

    public String getDeleteMethodName() {
        return deleteMethodName;
    }

    public String getUpdateMethodName() {
        return updateMethodName;
    }

    public void setFindMethodName(String findMethodName) {
        this.findMethodName = findMethodName;
    }

    public void setCreateMethodName(String createMethodName) {
        this.createMethodName = createMethodName;
    }

    public void setDeleteMethodName(String deleteMethodName) {
        this.deleteMethodName = deleteMethodName;
    }

    public void setUpdateMethodName(String updateMethodName) {
        this.updateMethodName = updateMethodName;
    }

    public IdFetchingStrategy<Id> getIdIdFetchingStrategy() {
        return idIdFetchingStrategy;
    }

    public MediaTypeStrategy getMediaTypeStrategy() {
        return mediaTypeStrategy;
    }

    public ValidationStrategy<DTO, Id> getValidationStrategy() {
        return validationStrategy;
    }
}
