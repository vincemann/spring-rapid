package io.github.vincemann.springrapid.core.controller.rapid;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.vincemann.springrapid.core.controller.JsonDtoCrudController;
import io.github.vincemann.springrapid.core.controller.dtoMapper.context.Direction;
import io.github.vincemann.springrapid.core.controller.dtoMapper.context.DtoMappingContext;
import io.github.vincemann.springrapid.core.controller.dtoMapper.context.CrudDtoEndpoint;
import io.github.vincemann.springrapid.core.controller.dtoMapper.DtoMappingException;
import io.github.vincemann.springrapid.core.controller.rapid.idFetchingStrategy.IdFetchingStrategy;
import io.github.vincemann.springrapid.core.controller.rapid.idFetchingStrategy.UrlParamIdFetchingStrategy;
import io.github.vincemann.springrapid.core.controller.rapid.idFetchingStrategy.exception.IdFetchingException;
import io.github.vincemann.springrapid.core.model.IdentifiableEntity;
import io.github.vincemann.springrapid.core.service.EndpointService;
import io.github.vincemann.springrapid.core.service.exception.BadEntityException;
import io.github.vincemann.springrapid.core.service.exception.EntityNotFoundException;
import io.github.vincemann.springrapid.core.service.exception.NoIdException;
import io.github.vincemann.springrapid.core.util.HttpServletRequestUtils;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolationException;
import java.io.IOException;
import java.io.Serializable;
import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Adapter that connects Springs Requirements for a Controller (which can be seen as an Interface),
 * with the {@link JsonDtoCrudController} Interface, resulting in a fully functional Spring @{@link org.springframework.web.bind.annotation.RestController}
 *
 * Fetches {@link Id} with given {@link IdFetchingStrategy} from HttpRequest.
 * Deserializes Json String from Requests to Dto and vice versa.
 *
 * Example-Request-URL's with {@link UrlParamIdFetchingStrategy}:
 * /entityName/httpMethod?entityIdName=id
 *
 * /account/get?accountId=34
 * /account/get?accountId=44bedc08-8e71-11e9-bc42-526af7764f64
 *
 * @param <E>        Entity Type, of entity, which's crud operations are exposed, via endpoints,  by this Controller
 * @param <Id>       Id Type of {@link E}
 *<
 */
@Slf4j
@Getter
public abstract class RapidController
        <
                E extends IdentifiableEntity<Id>,
                Id extends Serializable
        >
        extends JsonDtoCrudController<E,Id>
                implements ApplicationListener<ContextRefreshedEvent> {



    private EndpointService endpointService;
    @Setter
    private ObjectMapper jsonMapper;
    private String entityNameInUrl;
    private String baseUrl;

    public static final String FIND_METHOD_NAME ="get";
    public static final String CREATE_METHOD_NAME ="create";
    public static final String DELETE_METHOD_NAME ="delete";
    public static final String UPDATE_METHOD_NAME ="update";
    public static final String FIND_ALL_METHOD_NAME = "getAll";

    @Setter
    private String findUrl;
    @Setter
    private String updateUrl;
    @Setter
    private String findAllUrl;
    @Setter
    private String deleteUrl;
    @Setter
    private String createUrl;


    @Getter
    @Value("${controller.update.full.queryParam}")
    private String fullUpdateQueryParam;

    private IdFetchingStrategy<Id> idIdFetchingStrategy;
    private EndpointsExposureContext endpointsExposureContext;

    public RapidController(DtoMappingContext dtoMappingContext) {
        super(dtoMappingContext);
        initUrls();
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        initRequestMapping();
    }

    @Autowired
    public void injectEndpointService(EndpointService endpointService) {
        this.endpointService = endpointService;
    }

    @Autowired
    public void injectEndpointsExposureContext(EndpointsExposureContext endpointsExposureContext) {
        this.endpointsExposureContext = endpointsExposureContext;
    }

    @Autowired
    public void injectJsonMapper(ObjectMapper mapper) {
        this.jsonMapper = mapper;
    }



    @Autowired
    public void injectIdIdFetchingStrategy(IdFetchingStrategy<Id> idIdFetchingStrategy) {
        this.idIdFetchingStrategy = idIdFetchingStrategy;
    }

    private void initUrls(){
        this.entityNameInUrl=getEntityClass().getSimpleName().toLowerCase();
        this.baseUrl="/"+entityNameInUrl+"/";
        this.findUrl =baseUrl+FIND_METHOD_NAME;
        this.findAllUrl =baseUrl+FIND_ALL_METHOD_NAME;
        this.updateUrl=baseUrl+UPDATE_METHOD_NAME;
        this.deleteUrl=baseUrl+DELETE_METHOD_NAME;
        this.createUrl=baseUrl+CREATE_METHOD_NAME;
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
                log.debug("Exposing findAll Endpoint for "+this.getClass().getSimpleName());
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
                .produces(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .build();
    }

    public RequestMappingInfo getDeleteRequestMappingInfo(){
        return RequestMappingInfo
                .paths(deleteUrl)
                .methods(RequestMethod.DELETE)
                .produces(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .build();
    }

    public RequestMappingInfo getCreateRequestMappingInfo(){
        return RequestMappingInfo
                .paths(createUrl)
                .methods(RequestMethod.POST)
                .consumes(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .produces(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .build();
    }

    public RequestMappingInfo getUpdateRequestMappingInfo(){
        return RequestMappingInfo
                .paths(updateUrl)
                .methods(RequestMethod.PUT)
                .consumes(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .produces(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .build();
    }

    public RequestMappingInfo getFindAllRequestMappingInfo(){
        return RequestMappingInfo
                .paths(findAllUrl)
                .methods(RequestMethod.GET)
                .produces(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .build();
    }

    public ResponseEntity<String> findAll(HttpServletRequest request) throws DtoMappingException, DtoSerializingException {
        log.debug("FindAll request arriving at controller: " + request);
        beforeFindAll(request);
        Collection<IdentifiableEntity<Id>> dtos = super.findAll();
        String json = null;
        try {
            json = jsonMapper.writeValueAsString(dtos);
        } catch (JsonProcessingException e) {
            throw new DtoSerializingException(e);
        }
        return ok(json);
    }


    public ResponseEntity<String> find(HttpServletRequest request) throws IdFetchingException, EntityNotFoundException, NoIdException, DtoMappingException, DtoSerializingException {
        log.debug("Find request arriving at controller: " + request);
        Id id = idIdFetchingStrategy.fetchId(request);
        log.debug("id fetched from request: " + id);

        beforeFind(id,request);
        IdentifiableEntity<Id> dto = super.find(id);
        try {
            return ok(jsonMapper.writeValueAsString(dto));
        }catch (JsonProcessingException e){
            throw new DtoSerializingException(e);
        }
    }

    public ResponseEntity<String> create(HttpServletRequest request) throws BadEntityException, DtoMappingException, DtoSerializingException {
        log.debug("Create request arriving at controller: " + request);
        try {
            String json = request.getReader().lines().collect(Collectors.joining(System.lineSeparator()));
            Class<? extends IdentifiableEntity> dtoClass = findDtoClass(CrudDtoEndpoint.CREATE, Direction.REQUEST);
            IdentifiableEntity<Id> dto = getJsonMapper().readValue(json, dtoClass);
            beforeCreate(dto,request);
            IdentifiableEntity<Id> resultDto = super.create(dto);
            return new ResponseEntity<>(
                    jsonMapper.writeValueAsString(resultDto),
                    HttpStatus.OK);
        }catch (IOException e){
            throw new DtoSerializingException(e);
        }
    }

    public ResponseEntity<String> update(HttpServletRequest request) throws EntityNotFoundException, NoIdException, BadEntityException, DtoMappingException, DtoSerializingException {
        log.debug("Update request arriving at controller: " + request);
        try {
            String json = request.getReader().lines().collect(Collectors.joining(System.lineSeparator()));
            boolean fullUpdate = isFullUpdate(request);
            log.debug("full update mode: " + fullUpdate);
            Class<? extends IdentifiableEntity> dtoClass = null;
            if(fullUpdate) {
                dtoClass = findDtoClass(CrudDtoEndpoint.FULL_UPDATE, Direction.REQUEST);
            }else {
                dtoClass = findDtoClass(CrudDtoEndpoint.PARTIAL_UPDATE, Direction.REQUEST);
            }
            IdentifiableEntity<Id> dto  = getJsonMapper().readValue(json, dtoClass);
            beforeUpdate(dto,request,fullUpdate);
            IdentifiableEntity<Id> resultDto = super.update(dto, fullUpdate);
            return new ResponseEntity<>(
                    jsonMapper.writeValueAsString(resultDto),
                    HttpStatus.OK);
        }catch (IOException e){
            throw new DtoSerializingException(e);
        }
    }




    public ResponseEntity<?> delete(HttpServletRequest request) throws IdFetchingException, NoIdException, EntityNotFoundException, ConstraintViolationException {
        log.debug("Delete request arriving at controller: " + request);
        Id id = idIdFetchingStrategy.fetchId(request);
        log.debug("id fetched from request: " + id);
        beforeDelete(id,request);
        super.delete(id);
        return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON_UTF8).build();
    }

    protected boolean isFullUpdate(HttpServletRequest request) throws BadEntityException {
        Map<String, String[]> queryParameters = HttpServletRequestUtils.getQueryParameters(request);
        String[] fullUpdateParams = queryParameters.get(fullUpdateQueryParam);
        if(fullUpdateParams==null){
            return false;
        }
        if(fullUpdateParams.length>1){
            throw new BadEntityException("Multiple full update query params specified, there must be only one max. key: " + fullUpdateQueryParam);
        }
        else if(fullUpdateParams.length==0){
            return false;
        }else {
            return Boolean.parseBoolean(fullUpdateParams[0]);
        }
    }

    //callbacks
    public void beforeCreate(IdentifiableEntity<Id> dto, HttpServletRequest httpServletRequest){
    }
    public void beforeUpdate(IdentifiableEntity<Id> dto, HttpServletRequest httpServletRequest, boolean full){
    }
    public void beforeDelete(Id id, HttpServletRequest httpServletRequest){
    }
    public void beforeFind(Id id, HttpServletRequest httpServletRequest){
    }
    public void beforeFindAll(HttpServletRequest httpServletRequest){
    }

}
