package io.github.vincemann.springrapid.core.controller.rapid;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.vincemann.springrapid.core.advice.log.LogComponentInteractionAdvice;
import io.github.vincemann.springrapid.core.controller.DtoCrudController;
import io.github.vincemann.springrapid.core.controller.JsonDtoCrudController;
import io.github.vincemann.springrapid.core.controller.dtoMapper.Delegating;
import io.github.vincemann.springrapid.core.controller.dtoMapper.DtoMapper;
import io.github.vincemann.springrapid.core.controller.dtoMapper.context.Direction;
import io.github.vincemann.springrapid.core.controller.dtoMapper.context.DtoMappingContext;
import io.github.vincemann.springrapid.core.controller.dtoMapper.context.CrudDtoEndpoint;
import io.github.vincemann.springrapid.core.controller.dtoMapper.DtoMappingException;
import io.github.vincemann.springrapid.core.controller.dtoMapper.context.DtoMappingInfo;
import io.github.vincemann.springrapid.core.controller.rapid.idFetchingStrategy.IdFetchingStrategy;
import io.github.vincemann.springrapid.core.controller.rapid.idFetchingStrategy.UrlParamIdFetchingStrategy;
import io.github.vincemann.springrapid.core.controller.rapid.idFetchingStrategy.exception.IdFetchingException;
import io.github.vincemann.springrapid.core.controller.rapid.validationStrategy.ValidationStrategy;
import io.github.vincemann.springrapid.core.model.IdentifiableEntity;
import io.github.vincemann.springrapid.core.service.CrudService;
import io.github.vincemann.springrapid.core.service.EndpointService;
import io.github.vincemann.springrapid.core.service.exception.BadEntityException;
import io.github.vincemann.springrapid.core.service.exception.EntityNotFoundException;
import io.github.vincemann.springrapid.core.util.AuthorityUtil;
import io.github.vincemann.springrapid.core.util.EntityUtils;
import io.github.vincemann.springrapid.core.util.HttpServletRequestUtils;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.ConstraintViolationException;
import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.util.*;
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
                Id extends Serializable,
                S extends CrudService<E,Id,?>
        >
        implements DtoCrudController<Id>, ApplicationListener<ContextRefreshedEvent> {



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
    @Value("${controller.update.full.queryParam:full}")
    private String fullUpdateQueryParam;

    private IdFetchingStrategy<Id> idIdFetchingStrategy;
    private EndpointsExposureContext endpointsExposureContext;
    private S crudService;
    private DtoMapper dtoMapper;
    private DtoMappingContext dtoMappingContext;
    private ValidationStrategy<Id> validationStrategy;
    private boolean serviceInteractionLogging = true;
    @Getter
    @Setter
    private String mediaType = MediaType.APPLICATION_JSON_UTF8_VALUE;

    @SuppressWarnings("unchecked")
    private Class<E> entityClass = (Class<E>) ((ParameterizedType) this.getClass().getGenericSuperclass()).getActualTypeArguments()[0];

    @Autowired
    public void injectCrudService(S crudService) {
        this.crudService = crudService;
    }

    public void setDtoMappingContext(DtoMappingContext dtoMappingContext) {
        this.dtoMappingContext = dtoMappingContext;
    }

    @Autowired
    public void injectValidationStrategy(ValidationStrategy<Id> validationStrategy) {
        this.validationStrategy = validationStrategy;
    }


    @Delegating
    @Autowired
    public void injectDtoMapper(DtoMapper dtoMapper) {
        this.dtoMapper = dtoMapper;
    }

    public RapidController(DtoMappingContext dtoMappingContext) {
        super(dtoMappingContext);
        initUrls();
    }

    public RapidController(){
        super(null);
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

    protected void initRequestMapping(){
        try {
            if(endpointsExposureContext.isCreateEndpointExposed()) {
                //CREATE
                log.debug("Exposing create Endpoint for "+this.getClass().getSimpleName());
                getEndpointService().addMapping(getCreateRequestMappingInfo(),
                        this.getClass().getMethod("create", HttpServletRequest.class,HttpServletResponse.class), this);
            }

            if(endpointsExposureContext.isFindEndpointExposed()) {
                //GET
                log.debug("Exposing get Endpoint for "+this.getClass().getSimpleName());
                getEndpointService().addMapping(getFindRequestMappingInfo(),
                        this.getClass().getMethod("find", HttpServletRequest.class,HttpServletResponse.class), this);
            }

            if(endpointsExposureContext.isUpdateEndpointExposed()) {
                //UPDATE
                log.debug("Exposing update Endpoint for "+this.getClass().getSimpleName());
                getEndpointService().addMapping(getUpdateRequestMappingInfo(),
                        this.getClass().getMethod("update", HttpServletRequest.class,HttpServletResponse.class), this);
            }

            if(endpointsExposureContext.isDeleteEndpointExposed()) {
                //DELETE
                log.debug("Exposing delete Endpoint for "+this.getClass().getSimpleName());
                getEndpointService().addMapping(getDeleteRequestMappingInfo(),
                        this.getClass().getMethod("delete", HttpServletRequest.class, HttpServletResponse.class), this);
            }

            if(endpointsExposureContext.isFindAllEndpointExposed()){
                //DELETE
                log.debug("Exposing findAll Endpoint for "+this.getClass().getSimpleName());
                getEndpointService().addMapping(getFindAllRequestMappingInfo(),
                        this.getClass().getMethod("findAll", HttpServletRequest.class,HttpServletResponse.class), this);
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
                .produces(getMediaType())
                .build();
    }

    public RequestMappingInfo getDeleteRequestMappingInfo(){
        return RequestMappingInfo
                .paths(deleteUrl)
                .methods(RequestMethod.DELETE)
                .produces(getMediaType())
                .build();
    }

    public RequestMappingInfo getCreateRequestMappingInfo(){
        return RequestMappingInfo
                .paths(createUrl)
                .methods(RequestMethod.POST)
                .consumes(getMediaType())
                .produces(getMediaType())
                .build();
    }

    public RequestMappingInfo getUpdateRequestMappingInfo(){
        return RequestMappingInfo
                .paths(updateUrl)
                .methods(RequestMethod.PUT)
                .consumes(getMediaType())
                .produces(getMediaType())
                .build();
    }

    public RequestMappingInfo getFindAllRequestMappingInfo(){
        return RequestMappingInfo
                .paths(findAllUrl)
                .methods(RequestMethod.GET)
                .produces(getMediaType())
                .build();
    }

    public ResponseEntity<String> findAll(HttpServletRequest request,HttpServletResponse response) throws DtoMappingException, DtoSerializingException {
        log.debug("FindAll request arriving at controller: " + request);
        beforeFindAll(request,response);
        logStateBeforeServiceCall("findAll");
        Set<E> foundEntities = serviceFindAll();
        logServiceResult("findAll", foundEntities);
        Collection<Object> dtos = new HashSet<>();
        for (E e : foundEntities) {
            dtos.add(dtoMapper.mapToDto(e,
                    findDtoClass(CrudDtoEndpoint.FIND_ALL, Direction.RESPONSE)));
        }
        afterFindAll(dtos,foundEntities,request,response);
        String json = null;
        try {
            json = jsonMapper.writeValueAsString(dtos);
        } catch (JsonProcessingException e) {
            throw new DtoSerializingException(e);
        }
        return ok(json);
    }


    public ResponseEntity<String> find(HttpServletRequest request,HttpServletResponse response) throws IdFetchingException, EntityNotFoundException, BadEntityException, DtoMappingException, DtoSerializingException {
        log.debug("Find request arriving at controller: " + request);
        Id id = idIdFetchingStrategy.fetchId(request);
        log.debug("id fetched from request: " + id);

        beforeFind(id,request,response);
        validationStrategy.validateId(id);
        log.debug("id successfully validated");
        logStateBeforeServiceCall("findById", id);
        Optional<E> optionalEntity = serviceFind(id);
        logServiceResult("findById", optionalEntity);
        EntityUtils.checkPresent(optionalEntity, id, getEntityClass());
        Object dto = dtoMapper.mapToDto(optionalEntity.get(),
                findDtoClass(CrudDtoEndpoint.FIND, Direction.RESPONSE));
        afterFind(id,dto,optionalEntity,request,response);
        try {
            return ok(jsonMapper.writeValueAsString(dto));
        }catch (JsonProcessingException e){
            throw new DtoSerializingException(e);
        }
    }

    public ResponseEntity<String> create(HttpServletRequest request,HttpServletResponse response) throws BadEntityException, DtoMappingException, DtoSerializingException {
        log.debug("Create request arriving at controller: " + request);
        try {
            String json = request.getReader().lines().collect(Collectors.joining(System.lineSeparator()));
            Class<? > dtoClass = findDtoClass(CrudDtoEndpoint.CREATE, Direction.REQUEST);
            Object dto = getJsonMapper().readValue(json, dtoClass);
            beforeCreate(dto,request,response);
            validationStrategy.validateDto(dto);
            log.debug("Dto successfully validated");
            //i expect that dto has the right dto type -> callers responsibility
            E entity = mapToEntity(dto);
            logStateBeforeServiceCall("save", entity);
            E savedEntity = serviceCreate(entity);
            logServiceResult("save", savedEntity);
            Object resultDto = dtoMapper.mapToDto(savedEntity,
                    findDtoClass(CrudDtoEndpoint.CREATE, Direction.RESPONSE));
            afterCreate(resultDto,entity,request,response);
            return ok(jsonMapper.writeValueAsString(resultDto));
        }catch (IOException e){
            throw new DtoSerializingException(e);
        }
    }

    public ResponseEntity<String> update(HttpServletRequest request,HttpServletResponse response) throws EntityNotFoundException, BadEntityException, BadEntityException, DtoMappingException, DtoSerializingException {
        log.debug("Update request arriving at controller: " + request);
        try {
            String json = request.getReader().lines().collect(Collectors.joining(System.lineSeparator()));
            boolean full = isFullUpdate(request,response);
            log.debug("full update mode: " + full);
            Class<?> dtoClass;
            if(full) {
                dtoClass = findDtoClass(CrudDtoEndpoint.FULL_UPDATE, Direction.REQUEST);
            }else {
                dtoClass = findDtoClass(CrudDtoEndpoint.PARTIAL_UPDATE, Direction.REQUEST);
            }
            Object dto  = getJsonMapper().readValue(json, dtoClass);
            beforeUpdate(dto,request,full,response);

            validationStrategy.validateDto(dto);
            log.debug("Dto successfully validated");
            //i expect that dto has the right dto type -> callers responsibility
            E entity = mapToEntity(dto);
            logStateBeforeServiceCall("update", entity, full);
            E updatedEntity = serviceUpdate(entity,full);

            logServiceResult("update", updatedEntity);
            //no idea why casting is necessary here?
            Class<?> resultDtoClass;
            if (full) {
                resultDtoClass = findDtoClass(CrudDtoEndpoint.FULL_UPDATE, Direction.RESPONSE);
            } else {
                resultDtoClass = findDtoClass(CrudDtoEndpoint.PARTIAL_UPDATE, Direction.RESPONSE);
            }
            Object resultDto =  dtoMapper.mapToDto(updatedEntity, resultDtoClass);
            afterUpdate(resultDto,entity,request,full,response);
            return ok(jsonMapper.writeValueAsString(resultDto));
        }catch (IOException e){
            throw new DtoSerializingException(e);
        }
    }




    public ResponseEntity<?> delete(HttpServletRequest request,HttpServletResponse response) throws IdFetchingException, BadEntityException, EntityNotFoundException, ConstraintViolationException {
        log.debug("Delete request arriving at controller: " + request);
        Id id = idIdFetchingStrategy.fetchId(request);
        log.debug("id fetched from request: " + id);
        beforeDelete(id,request,response);
        validationStrategy.validateId(id);
        log.debug("id successfully validated");
        logStateBeforeServiceCall("delete", id);
        serviceDelete(id);
        afterDelete(id,request,response);
        return ok();
    }

    protected boolean isFullUpdate(HttpServletRequest request,HttpServletResponse response) throws BadEntityException {
        Map<String, String[]> queryParameters = HttpServletRequestUtils.getQueryParameters(request);
        String[] fullUpdateParams = queryParameters.get(fullUpdateQueryParam);
        if(fullUpdateParams==null){
            return false;
        }
        EntityUtils.checkProperEntity(!(fullUpdateParams.length>1),"Multiple full update query params specified, there must be only one max. key: " + fullUpdateQueryParam);
        if(fullUpdateParams.length==0){
            return false;
        }else {
            return Boolean.parseBoolean(fullUpdateParams[0]);
        }
    }

    public Class<?> findDtoClass(String endpoint, Direction direction) {
        DtoMappingInfo endpointInfo = createEndpointInfo(endpoint, direction);
        return getDtoMappingContext().find(endpointInfo);
    }

    protected DtoMappingInfo createEndpointInfo(String endpoint, Direction direction) {
        return DtoMappingInfo.builder()
                .authorities(AuthorityUtil.getAuthorities())
                .direction(direction)
                .endpoint(endpoint)
                .build();
    }

    protected void logStateBeforeServiceCall(String methodName, Object... args) {
        if (serviceInteractionLogging) {
            LogComponentInteractionAdvice.logArgs(methodName, args);
            log.info("SecurityContexts Authentication right before service call: " + SecurityContextHolder.getContext().getAuthentication());
        }
    }

    protected void logServiceResult(String methodName, Object result) {
        if (serviceInteractionLogging)
            LogComponentInteractionAdvice.logResult(methodName, result);
    }

    private E mapToEntity(Object dto) throws DtoMappingException {
        return dtoMapper.mapToEntity(dto, entityClass);
    }


    protected ResponseEntity<String> ok(String jsonDto) {
        return ResponseEntity.ok()
                .contentType(MediaType.valueOf(getMediaType()))
                .body(jsonDto);
    }

    protected ResponseEntity<?> ok() {
        return ResponseEntity.ok()
                .contentType(MediaType.valueOf(getMediaType()))
                .build();
    }

    // overrideable
    protected E serviceUpdate(E update, boolean full) throws BadEntityException, EntityNotFoundException {
        return crudService.update(update, full);
    }

    protected E serviceCreate(E entity) throws BadEntityException {
        return crudService.save(entity);
    }

    protected void serviceDelete(Id id) throws BadEntityException, EntityNotFoundException {
        crudService.deleteById(id);
    }

    protected Set<E> serviceFindAll() {
        return crudService.findAll();
    }

    protected Optional<E> serviceFind(Id id) throws BadEntityException {
        return crudService.findById(id);
    }


    //callbacks
    public void beforeCreate(Object dto, HttpServletRequest httpServletRequest,HttpServletResponse response){
    }
    public void beforeUpdate(Object dto, HttpServletRequest httpServletRequest, boolean full,HttpServletResponse response){
    }
    public void beforeDelete(Id id, HttpServletRequest httpServletRequest,HttpServletResponse response){
    }
    public void beforeFind(Id id, HttpServletRequest httpServletRequest,HttpServletResponse response){
    }
    public void beforeFindAll(HttpServletRequest httpServletRequest,HttpServletResponse response){
    }

    //callbacks
    public void afterCreate(Object dto,E created, HttpServletRequest httpServletRequest,HttpServletResponse response){
    }
    public void afterUpdate(Object dto,E updated, HttpServletRequest httpServletRequest, boolean full,HttpServletResponse response){
    }
    public void afterDelete(Id id, HttpServletRequest httpServletRequest,HttpServletResponse response){
    }
    public void afterFind(Id id, Object dto, Optional<E> found, HttpServletRequest httpServletRequest, HttpServletResponse response){
    }
    public void afterFindAll(Collection<Object> dtos,Set<E> found,  HttpServletRequest httpServletRequest,HttpServletResponse response){
    }




}
