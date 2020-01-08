package io.github.vincemann.generic.crud.lib.controller.springAdapter;

import io.github.vincemann.generic.crud.lib.controller.dtoMapper.MappingContext;
import io.github.vincemann.generic.crud.lib.controller.dtoMapper.exception.EntityMappingException;
import io.github.vincemann.generic.crud.lib.controller.springAdapter.idFetchingStrategy.exception.IdFetchingException;
import io.github.vincemann.generic.crud.lib.controller.springAdapter.idFetchingStrategy.IdFetchingStrategy;
import io.github.vincemann.generic.crud.lib.controller.springAdapter.mediaTypeStrategy.DtoReadingException;
import io.github.vincemann.generic.crud.lib.controller.springAdapter.mediaTypeStrategy.MediaTypeStrategy;
import io.github.vincemann.generic.crud.lib.controller.springAdapter.validationStrategy.ValidationStrategy;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.CrudRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import io.github.vincemann.generic.crud.lib.controller.BasicDtoCrudController;
import io.github.vincemann.generic.crud.lib.model.IdentifiableEntity;
import io.github.vincemann.generic.crud.lib.service.EndpointService;
import io.github.vincemann.generic.crud.lib.service.exception.BadEntityException;
import io.github.vincemann.generic.crud.lib.service.exception.EntityNotFoundException;
import io.github.vincemann.generic.crud.lib.service.exception.NoIdException;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolationException;
import java.io.IOException;
import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Adapter that connects Springs Requirements for a Controller (which can be seen as an Interface),
 * with the {@link BasicDtoCrudController} Interface, resulting in a fully functional Spring @{@link org.springframework.web.bind.annotation.RestController}
 *
 * fetches {@link Id} with given {@link IdFetchingStrategy} from HttpRequest
 * fetches Dto with given {@link MediaTypeStrategy} from HttpRequest
 * validates the Dto and {@link Id} with the given {@link ValidationStrategy}
 *
 * ExampleUrls with {@link io.github.vincemann.generic.crud.lib.controller.springAdapter.idFetchingStrategy.UrlParamIdFetchingStrategy}:
 * /entityName/httpMethod?entityIdName=id
 *
 * /account/get?accountId=34
 * /account/get?accountId=44bedc08-8e71-11e9-bc42-526af7764f64
 *
 * @param <E> Service Entity Type, of entity, which's curd operations are exposed, via endpoints,  by this Controller
 * @param <Id>       Id Type of {@link E}
 *
 */
@Slf4j
@Getter
public abstract class DtoCrudController_SpringAdapter
        <
                E extends IdentifiableEntity<Id>,
                Id extends Serializable,
                Repo extends CrudRepository<E,Id>
        >
        extends BasicDtoCrudController<E,Id, Repo>
                implements InitializingBean {


    private EndpointService endpointService;
    private String entityNameInUrl;
    private String baseUrl;
    @Setter
    private String findMethodName ="get";
    @Setter
    private String createMethodName="create";
    @Setter
    private String deleteMethodName="delete";
    @Setter
    private String updateMethodName="update";
    @Setter
    private String findAllMethodName = "getAll";

    @Setter
    private String findUrl;
    @Setter
    private String updateUrl;
    @Setter
    private String getAllUrl;
    @Setter
    private String deleteUrl;
    @Setter
    private String createUrl;

    private IdFetchingStrategy<Id> idIdFetchingStrategy;
    private MediaTypeStrategy<Id> mediaTypeStrategy;
    private ValidationStrategy<Id> validationStrategy;
    private EndpointsExposureContext endpointsExposureContext;

    public DtoCrudController_SpringAdapter(MappingContext<Id> mappingContext) {
        super(mappingContext);
    }


    @Override
    public void afterPropertiesSet() {
        initUrls();
        initRequestMapping();
    }

    @Autowired
    public void injectEndpointService(EndpointService endpointService) {
        this.endpointService = endpointService;
    }

    @Autowired
    public void injectMediaTypeStrategy(MediaTypeStrategy<Id> mediaTypeStrategy) {
        this.mediaTypeStrategy = mediaTypeStrategy;
    }
    @Autowired
    public void injectEndpointsExposureDetails(EndpointsExposureContext endpointsExposureContext) {
        this.endpointsExposureContext = endpointsExposureContext;
    }

    @Autowired
    public void injectIdIdFetchingStrategy(IdFetchingStrategy<Id> idIdFetchingStrategy) {
        this.idIdFetchingStrategy = idIdFetchingStrategy;
    }

    @Autowired
    public void injectValidationStrategy(ValidationStrategy<Id> validationStrategy) {
        this.validationStrategy = validationStrategy;
    }

    private void initUrls(){
        this.entityNameInUrl=getEntityClass().getSimpleName().toLowerCase();
        this.baseUrl="/"+entityNameInUrl+"/";
        this.findUrl =baseUrl+getFindMethodName();
        this.getAllUrl=baseUrl+getFindAllMethodName();
        this.updateUrl=baseUrl+getUpdateMethodName();
        this.deleteUrl=baseUrl+getDeleteMethodName();
        this.createUrl=baseUrl+getCreateMethodName();
    }

    private void initRequestMapping(){
        try {
            if(endpointsExposureContext.isCreateEndpointExposed()) {
                //CREATE
                log.debug("Exposing create Endpoint for "+this.getClass().getSimpleName());
                getEndpointService().addMapping(getCreateRequestMappingInfo(),
                        this.getClass().getMethod("create", HttpServletRequest.class), this);
            }

            if(endpointsExposureContext.isFindEndpointExposed()) {
                //GET
                log.debug("Exposing get Endpoint for "+this.getClass().getSimpleName());
                getEndpointService().addMapping(getFindRequestMappingInfo(),
                        this.getClass().getMethod("find", HttpServletRequest.class), this);
            }

            if(endpointsExposureContext.isUpdateEndpointExposed()) {
                //UPDATE
                log.debug("Exposing update Endpoint for "+this.getClass().getSimpleName());
                getEndpointService().addMapping(getUpdateRequestMappingInfo(),
                        this.getClass().getMethod("update", HttpServletRequest.class), this);
            }

            if(endpointsExposureContext.isDeleteEndpointExposed()) {
                //DELETE
                log.debug("Exposing delete Endpoint for "+this.getClass().getSimpleName());
                getEndpointService().addMapping(getDeleteRequestMappingInfo(),
                        this.getClass().getMethod("delete", HttpServletRequest.class), this);
            }

            if(endpointsExposureContext.isFindAllEndpointExposed()){
                //DELETE
                log.debug("Exposing delete Endpoint for "+this.getClass().getSimpleName());
                getEndpointService().addMapping(getFindAllRequestMappingInfo(),
                        this.getClass().getMethod("findAll", HttpServletRequest.class), this);
            }

        }catch (NoSuchMethodException e){
            //should never happen
            throw new IllegalStateException(e);
        }
    }

    public RequestMappingInfo getFindRequestMappingInfo(){
        return RequestMappingInfo
                .paths(findUrl)
                .methods(RequestMethod.GET)
                .produces(mediaTypeStrategy.getMediaType())
                .build();
    }

    public RequestMappingInfo getDeleteRequestMappingInfo(){
        return RequestMappingInfo
                .paths(deleteUrl)
                .methods(RequestMethod.DELETE)
                .build();
    }

    public RequestMappingInfo getCreateRequestMappingInfo(){
        return RequestMappingInfo
                .paths(createUrl)
                .methods(RequestMethod.POST)
                .consumes(mediaTypeStrategy.getMediaType())
                .produces(mediaTypeStrategy.getMediaType())
                .build();
    }

    public RequestMappingInfo getUpdateRequestMappingInfo(){
        return RequestMappingInfo
                .paths(updateUrl)
                .methods(RequestMethod.PUT)
                .consumes(mediaTypeStrategy.getMediaType())
                .produces(mediaTypeStrategy.getMediaType())
                .build();
    }

    public RequestMappingInfo getFindAllRequestMappingInfo(){
        return RequestMappingInfo
                .paths(getAllUrl)
                .methods(RequestMethod.GET)
                .produces(mediaTypeStrategy.getMediaType())
                .build();
    }

    public ResponseEntity<Collection<IdentifiableEntity<Id>>> findAll(HttpServletRequest request) throws EntityMappingException {
        log.debug("FindAll request arriving at controller: " + request);
        validationStrategy.validateFindAllRequest(request);
        log.debug("Find all request validated");
        beforeFindAll(request);
        return super.findAll();
    }


    public ResponseEntity<? extends IdentifiableEntity<Id>> find(HttpServletRequest request) throws IdFetchingException, EntityNotFoundException, NoIdException, EntityMappingException {
        log.debug("Find request arriving at controller: " + request);
        Id id = idIdFetchingStrategy.fetchId(request);
        validationStrategy.beforeFindValidate(id);
        log.debug("id fetched from request: " + id);
        validationStrategy.validateId(id,request);
        log.debug("id successfully validated");
        beforeFind(id,request);
        return super.find(id);
    }

    public ResponseEntity<? extends IdentifiableEntity<Id>> create(HttpServletRequest request) throws DtoReadingException, BadEntityException, EntityMappingException {
        log.debug("Create request arriving at controller: " + request);
        try {
            String body = request.getReader().lines().collect(Collectors.joining(System.lineSeparator()));
            log.debug("String Body fetched from request: " + body);
            IdentifiableEntity<Id> dto = mediaTypeStrategy.readDtoFromBody(body,getMappingContext().getCreateArgDtoClass());
            log.debug("Dto read from string body " + dto);
            validationStrategy.beforeCreateValidate(dto);
            validationStrategy.validateDto(dto,request);
            log.debug("Dto successfully validated");
            beforeCreate(dto,request);
            return super.create(dto);
        }catch (IOException e){
            throw new DtoReadingException(e);
        }
    }

    public ResponseEntity<? extends IdentifiableEntity<Id>> update(HttpServletRequest request) throws DtoReadingException, EntityNotFoundException, NoIdException, BadEntityException, EntityMappingException {
        log.debug("Update request arriving at controller: " + request);
        try {
            String body = request.getReader().lines().collect(Collectors.joining(System.lineSeparator()));
            log.debug("String Body fetched from request: " + body);
            IdentifiableEntity<Id> dto = mediaTypeStrategy.readDtoFromBody(body,getMappingContext().getUpdateArgDtoClass());
            log.debug("Dto read from string body " + dto);
            validationStrategy.beforeUpdateValidate(dto);
            validationStrategy.validateDto(dto,request);
            log.debug("Dto successfully validated");
            beforeUpdate(dto,request);
            return super.update(dto);
        }catch (IOException e){
            throw new DtoReadingException(e);
        }
    }


    public ResponseEntity<?> delete(HttpServletRequest request) throws IdFetchingException, NoIdException, EntityNotFoundException, ConstraintViolationException {
        log.debug("Delete request arriving at controller: " + request);
        Id id = idIdFetchingStrategy.fetchId(request);
        log.debug("id fetched from request: " + id);
        validationStrategy.beforeDeleteValidate(id);
        validationStrategy.validateId(id,request);
        log.debug("id successfully validated");
        beforeDelete(id,request);
        return super.delete(id);
    }

    protected void beforeCreate(IdentifiableEntity<Id> dto, HttpServletRequest httpServletRequest){
    }
    protected void beforeUpdate(IdentifiableEntity<Id> dto, HttpServletRequest httpServletRequest){
    }
    protected void beforeDelete(Id id, HttpServletRequest httpServletRequest){
    }
    protected void beforeFind(Id id, HttpServletRequest httpServletRequest){
    }
    protected void beforeFindAll(HttpServletRequest httpServletRequest){
    }

}
