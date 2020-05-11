package io.github.vincemann.springrapid.core.controller.rapid.parentAware;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.github.vincemann.springrapid.core.controller.dtoMapper.context.Direction;
import io.github.vincemann.springrapid.core.controller.rapid.DtoSerializingException;
import io.github.vincemann.springrapid.core.controller.rapid.RapidController;
import io.github.vincemann.springrapid.core.controller.rapid.idFetchingStrategy.IdFetchingStrategy;
import io.github.vincemann.springrapid.core.controller.rapid.idFetchingStrategy.exception.IdFetchingException;
import io.github.vincemann.springrapid.core.controller.rapid.validationStrategy.ValidationStrategy;
import io.github.vincemann.springrapid.core.model.IdentifiableEntity;
import io.github.vincemann.springrapid.core.service.CrudService;
import io.github.vincemann.springrapid.core.service.ParentAwareCrudService;
import io.github.vincemann.springrapid.core.service.exception.BadEntityException;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;

import javax.servlet.http.HttpServletRequest;
import java.io.Serializable;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * Controller for ChildEntity. Child Entity has one to n relationship with Parent.
 * This Controller offers endpoint for finding all children for given parent(-id).
 *
 * @param <E>
 * @param <Id>
 * @param <PId>
 * @param <S>
 */
@Slf4j
@Getter
public abstract class ParentAwareRapidController
        <
                E extends IdentifiableEntity<Id>,
                Id extends Serializable,
                PId extends Serializable,
                S extends CrudService<E, Id, ?> & ParentAwareCrudService<E, PId>
                >
        extends RapidController<E, Id, S> {

    public static final String FIND_ALL_OF_PARENT_METHOD_NAME = "getAllOfParent";
    private IdFetchingStrategy<PId> parentIdFetchingStrategy;
    private ValidationStrategy<PId> parentValidationStrategy;
    @Setter
    private String findAllOfParentUrl;


    @Autowired
    public void injectService(S service) {
        super.injectCrudService(service);
    }


    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        super.onApplicationEvent(event);
        this.findAllOfParentUrl = getBaseUrl() + FIND_ALL_OF_PARENT_METHOD_NAME;
        registerFindByParentIdRequestMapping();
    }

    private void registerFindByParentIdRequestMapping() {
        try {
            log.debug("Exposing findAllOfParent Endpoint for " + this.getClass().getSimpleName());
            getEndpointService().addMapping(getFindAllOfParentRequestMappingInfo(),
                    this.getClass().getMethod("findAllOfParent", HttpServletRequest.class), this);
        } catch (NoSuchMethodException e) {
            //should never happen
            throw new IllegalStateException(e);
        }
    }

    public RequestMappingInfo getFindAllOfParentRequestMappingInfo() {
        return RequestMappingInfo
                .paths(findAllOfParentUrl)
                .methods(RequestMethod.GET)
                .produces(getMediaType())
                .build();
    }


    public ResponseEntity<String> findAllOfParent(HttpServletRequest request) throws IdFetchingException, BadEntityException,  DtoSerializingException {
        try {
            log.debug("FindAllOfParent request arriving at controller: " + request);
            PId id = parentIdFetchingStrategy.fetchId(request);
            log.debug("parentId: " + id);
            parentValidationStrategy.validateId(id);
            log.debug("id successfully validated");
            beforeFindAllByParent(id, request);
            Set<E> children = getService().findAllOfParent(id);
            Collection<Object> dtos = new HashSet<>();
            for (E e : children) {
                Class<?> dtoClass = findDtoClass(ParentAwareDtoEndpoint.FIND_ALL_OF_PARENT, Direction.RESPONSE, null);
                dtos.add(getDtoMapper().mapToDto(e, dtoClass));
            }
            String json = getJsonMapper().writeValueAsString(dtos);
            return ok(json);
        } catch (JsonProcessingException e) {
            throw new DtoSerializingException(e);
        }
    }


    //callback
    protected void beforeFindAllByParent(PId parentId, HttpServletRequest httpServletRequest) {
    }

    @Autowired
    public void injectParentIdFetchingStrategy(IdFetchingStrategy<PId> parentIdFetchingStrategy) {
        this.parentIdFetchingStrategy = parentIdFetchingStrategy;
    }

    @Autowired
    public void injectParentValidationStrategy(ValidationStrategy<PId> parentValidationStrategy) {
        this.parentValidationStrategy = parentValidationStrategy;
    }
}
