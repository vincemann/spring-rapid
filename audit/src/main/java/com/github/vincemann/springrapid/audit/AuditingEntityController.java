package com.github.vincemann.springrapid.audit;

import com.github.vincemann.springrapid.core.controller.AbstractEntityController;
import com.github.vincemann.springrapid.core.controller.fetchid.IdFetchingException;
import com.github.vincemann.springrapid.core.controller.fetchid.IdFetchingStrategy;
import com.github.vincemann.springrapid.core.model.AuditingEntity;
import com.github.vincemann.springrapid.core.service.CrudService;
import com.github.vincemann.springrapid.core.service.exception.BadEntityException;
import com.github.vincemann.springrapid.core.service.exception.EntityNotFoundException;
import com.github.vincemann.springrapid.core.util.VerifyEntity;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

@Slf4j
@Getter
public class AuditingEntityController<
        E extends AuditingEntity<ID>,
        ID extends Serializable,
        S extends AuditingService<ID>>
        extends AbstractEntityController<E,ID>
{

    private IdFetchingStrategy<ID> idFetchingStrategy;
    private S service;
    @Setter
    private String checkUpdateRequiredUrl;


    @SuppressWarnings("unchecked")
    public AuditingEntityController() {
        super();
    }


    public ResponseEntity<String> checkUpdateRequired(HttpServletRequest request, HttpServletResponse response) throws BadEntityException, EntityNotFoundException {
        ID id = fetchId(request);
        String lastUpdate = request.getParameter("timestamp");
        VerifyEntity.isPresent(lastUpdate, "need 'lastupdate' parameter");
        Date lastModifiedDate = serviceFindLastModifiedDate(id);
        if (lastModifiedDate == null)
            throw new EntityNotFoundException("Could not find last modified timestamp for entity: " + getEntityClass() + " : " + id);

        // jpa uses this format
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

        try {
            Date lastUpdateDate = sdf.parse(lastUpdate);
            boolean updateRequired = lastUpdateDate.before(lastModifiedDate);
            return ResponseEntity.ok(updateRequired ? "1" : "0");
        } catch (ParseException e) {
            throw new BadEntityException("bad timestamp format, use yyyy-MM-dd HH:mm:ss.SSS");
        }
    }

    @Override
    protected void registerEndpoints() throws NoSuchMethodException {
        registerEndpoint(createCheckUpdateRequriedRequestMappingInfo(),"checkUpdateRequired");
    }


    protected Date serviceFindLastModifiedDate(ID id){
        return service.findLastModifiedDate(id);
    }

    private RequestMappingInfo createCheckUpdateRequriedRequestMappingInfo() {
        return RequestMappingInfo
                .paths(checkUpdateRequiredUrl)
                .methods(RequestMethod.GET)
                .produces(MediaType.TEXT_PLAIN_VALUE)
                .build();
    }

    protected void initUrls() {
        super.initUrls();
        this.checkUpdateRequiredUrl = entityBaseUrl + "check-update-required";
    }


    protected ID fetchId(HttpServletRequest request) throws IdFetchingException {
        return this.getIdFetchingStrategy().fetchId(request);
    }

    @Autowired
    public void injectIdFetchingStrategy(IdFetchingStrategy<ID> idFetchingStrategy) {
        this.idFetchingStrategy = idFetchingStrategy;
    }

    @Autowired
    @Lazy
    public void injectService(S service) {
        this.service = service;
    }
}
