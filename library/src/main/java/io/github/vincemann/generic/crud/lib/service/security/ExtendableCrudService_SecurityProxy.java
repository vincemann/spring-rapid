package io.github.vincemann.generic.crud.lib.service.security;

import io.github.vincemann.generic.crud.lib.model.IdentifiableEntity;
import io.github.vincemann.generic.crud.lib.service.ExtendableCrudService;
import io.github.vincemann.generic.crud.lib.service.exception.BadEntityException;
import io.github.vincemann.generic.crud.lib.service.exception.EntityNotFoundException;
import io.github.vincemann.generic.crud.lib.service.exception.NoIdException;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.repository.CrudRepository;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.Optional;
import java.util.Set;


@Getter
@Qualifier(ExtendableCrudService_SecurityProxy.SECURITY_PROXY)
public abstract class ExtendableCrudService_SecurityProxy
        <
            E extends IdentifiableEntity<Id>,
            Id extends Serializable,
            R extends CrudRepository<E,Id>,
            S extends ExtendableCrudService<E,Id,R>
        >
            extends ExtendableCrudService<E,Id,R>{

    private S service;

    //@Value("${crud.security.readPermission:READ}")
    @Setter
    private String readPermission = "READ";
    //@Value("${crud.security.writePermission:WRITE}")
    @Setter
    private String writePermission = "WRITE";
    /*@Value("${crud.security.createPermission:CREATE}")
    private String createPermission;*/
    //@Value("${crud.security.deletePermission:DELETE}")
    @Setter
    private String deletePermission = "DELETE";



    public static final String SECURITY_PROXY = "securityProxy";

    public ExtendableCrudService_SecurityProxy(S service, ExtendableCrudService.Plugin<? super E,? super Id>... plugins) {
        super(plugins);
        this.service = service;
    }

    public ExtendableCrudService_SecurityProxy(S service){
        super();
        this.service=service;
    }


    @Override
    public Optional<E> findByIdImpl(Id id) throws NoIdException {
        return service.findByIdImpl(id);
    }

    @Override
    public E updateImpl(E entity) throws EntityNotFoundException, NoIdException, BadEntityException {
        return service.updateImpl(entity);
    }

    @Override
    public E saveImpl(E entity) throws BadEntityException {
        return service.saveImpl(entity);
    }

    @Override
    public Set<E> findAllImpl() {
        return service.findAllImpl();
    }

    @Override
    public void deleteImpl(E entity) throws EntityNotFoundException, NoIdException {
        service.deleteImpl(entity);
    }

    @Override
    public void deleteByIdImpl(Id id) throws EntityNotFoundException, NoIdException {
        service.deleteByIdImpl(id);
    }

    @Transactional
    @Override
    public Optional<E> findById(Id id) throws NoIdException {
        checkPermission(id,readPermission);
        return service.findById(id);
    }

    @Transactional
    @Override
    public E update(E entity) throws EntityNotFoundException, NoIdException, BadEntityException {
        checkPermission(entity,writePermission);
        return service.update(entity);
    }

    @Transactional
    @Override
    public E save(E entity) throws BadEntityException {
        return service.save(entity);
    }

    @Transactional
    @Override
    public Set<E> findAll() {
        return service.findAll();
    }

    @Transactional
    @Override
    public void delete(E entity) throws EntityNotFoundException, NoIdException {
        checkPermission(entity,deletePermission);
        service.delete(entity);
    }

    @Transactional
    @Override
    public void deleteById(Id id) throws EntityNotFoundException, NoIdException {
        checkPermission(id,deletePermission);
        service.deleteById(id);
    }

    @Override
    public Class<E> getEntityClass() {
        return service.getEntityClass();
    }

    @Override
    public R getRepository() {
        return service.getRepository();
    }

    protected void checkPermission(Id id, String permission){
        boolean permitted = SecurityChecker.preAuthorize("hasPermission(" + id + ",'" + service.getEntityClass().getName() + "','" + permission + "')");
        reactToAuthorizeResult(permitted);
    }

    protected void checkPermission(E entity,String permission){
        boolean permitted = SecurityChecker.preAuthorize("hasPermission(" + entity + ",'" + permission + "')");
        reactToAuthorizeResult(permitted);
    }

    protected void checkRole(String role){
        boolean permitted = SecurityChecker.preAuthorize("hasRole('" + role + "')");
        reactToAuthorizeResult(permitted);
    }

    private void reactToAuthorizeResult(boolean permitted){
        if(!permitted){
            throw new AccessDeniedException("Permission not Granted");
        }
    }

}
