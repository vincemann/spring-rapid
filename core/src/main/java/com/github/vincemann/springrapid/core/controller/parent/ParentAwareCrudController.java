package com.github.vincemann.springrapid.core.controller.parent;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.vincemann.springrapid.core.controller.dto.mapper.context.Direction;
import com.github.vincemann.springrapid.core.controller.GenericCrudController;
import com.github.vincemann.springrapid.core.controller.fetchid.IdFetchingStrategy;
import com.github.vincemann.springrapid.core.controller.fetchid.IdFetchingException;
import com.github.vincemann.springrapid.core.controller.DtoValidationStrategy;
import com.github.vincemann.springrapid.core.model.IdentifiableEntity;
import com.github.vincemann.springrapid.core.service.CrudService;
import com.github.vincemann.springrapid.core.service.ParentAwareService;
import com.github.vincemann.springrapid.core.service.exception.BadEntityException;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.Serializable;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * Controller for ChildEntity. Parent Entity has one to n relationship with ChildEntity.
 * This Controller offers endpoint for finding all children for given parent(-id).
 *
 * @param <E>   Child Entity Type
 * @param <Id>  Child Entity Id Type
 * @param <PId> Parent Entity Id Type
 * @param <S>   ChildService Type
 */
@Slf4j
@Getter
public abstract class ParentAwareCrudController
        <
                E extends IdentifiableEntity<Id>,
                Id extends Serializable,
                PId extends Serializable,
                S extends CrudService<E, Id> & ParentAwareService<E, PId>
                >
        extends GenericCrudController<E, Id, S,ParentAwareEndpointInfo,ParentAwareDtoMappingContextBuilder> {

    private IdFetchingStrategy<PId> parentIdFetchingStrategy;
    private DtoValidationStrategy parentDtoValidationStrategy;
    @Setter
    private String findAllOfParentUrl;


    // init

    @Override
    protected ParentAwareDtoMappingContextBuilder createDtoMappingContextBuilder() {
        return new ParentAwareDtoMappingContextBuilder(this);
    }

    @Override
    protected void initUrls() {
        super.initUrls();
        this.findAllOfParentUrl = entityBaseUrl + coreProperties.controller.endpoints.findAllOfParent;
    }


    //endpoints

    @Override
    protected void registerEndpoints() throws NoSuchMethodException {
        super.registerEndpoints();
        registerEndpoint(createFindAllOfParentRequestMappingInfo(),"findAllOfParent");
    }

    protected RequestMappingInfo createFindAllOfParentRequestMappingInfo() {
        return RequestMappingInfo
                .paths(findAllOfParentUrl)
                .methods(RequestMethod.GET)
                .produces(coreProperties.getController().getMediaType())
                .build();
    }


    // controller methods

    public ResponseEntity<String> findAllOfParent(HttpServletRequest request, HttpServletResponse response) throws IdFetchingException, BadEntityException, JsonProcessingException {
            log.debug("FindAllOfParent request arriving at controller: " + request);
            PId id = parentIdFetchingStrategy.fetchId(request);
            log.debug("parentId: " + id);
            beforeFindAllByParent(id, request);
            Set<E> children = getService().findAllOfParent(id);
            Collection<Object> dtos = new HashSet<>();
            for (E e : children) {
                Class<?> dtoClass = createDtoClass(getFindAllOfParentUrl(), Direction.RESPONSE, null);
                dtos.add(getDtoMapper().mapToDto(e, dtoClass));
            }
            String json = getJsonMapper().writeDto(dtos);
            return ok(json);

    }


    //callback
    protected void beforeFindAllByParent(PId parentId, HttpServletRequest httpServletRequest) {
    }


    //dependencies

    @Autowired
    public void injectParentIdFetchingStrategy(IdFetchingStrategy<PId> parentIdFetchingStrategy) {
        this.parentIdFetchingStrategy = parentIdFetchingStrategy;
    }

    @Autowired
    public void injectParentValidationStrategy(DtoValidationStrategy parentDtoValidationStrategy) {
        this.parentDtoValidationStrategy = parentDtoValidationStrategy;
    }

    @Autowired
    public void injectService(S service) {
        super.injectCrudService(service);
    }

    @Autowired
    @Qualifier("parentAwareEndpointInfo")
    @Override
    public void injectEndpointInfo(ParentAwareEndpointInfo endpointInfo) {
        super.injectEndpointInfo(endpointInfo);
    }
}
