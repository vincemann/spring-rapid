package io.github.vincemann.generic.crud.lib.controller.dtoMapper.idResolver.uniDir;

import io.github.vincemann.generic.crud.lib.controller.dtoMapper.EntityMappingException;
import io.github.vincemann.generic.crud.lib.controller.dtoMapper.idResolver.EntityIdResolver;
import io.github.vincemann.generic.crud.lib.dto.uniDir.UniDirDtoChild;
import io.github.vincemann.generic.crud.lib.model.uniDir.UniDirChild;
import io.github.vincemann.generic.crud.lib.model.uniDir.UniDirParent;
import io.github.vincemann.generic.crud.lib.service.crudServiceFinder.CrudServiceFinder;
import io.github.vincemann.generic.crud.lib.service.exception.EntityNotFoundException;
import io.github.vincemann.generic.crud.lib.service.exception.NoIdException;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.Map;

@Component
public class UniDirChildResolver extends EntityIdResolver<UniDirChild, UniDirDtoChild> {

    public UniDirChildResolver(CrudServiceFinder crudServiceFinder) {
        super(crudServiceFinder, UniDirDtoChild.class);
    }

    public void resolveServiceEntityIds(UniDirChild mappedUniDirChild, UniDirDtoChild uniDirDtoChild) throws EntityMappingException {
        try {
            Map<Class, Serializable> allParentIdToClassMappings = uniDirDtoChild.findAllParentIds();
            for (Map.Entry<Class, Serializable> parentIdToClassMapping : allParentIdToClassMappings.entrySet()) {
                Object parent = findEntityFromService(parentIdToClassMapping);
                try {
                    //set parent of mapped child
                    mappedUniDirChild.findAndSetParent(parent);
                    //dont set backref, because Parent does not know about Child (uniDir)
                } catch (ClassCastException e) {
                    throw new IllegalArgumentException("Found Parent " + parent + " is not of Type UniDirParent");
                }
            }
        } catch (IllegalAccessException e){
            throw new RuntimeException(e);
        }
    }

    @Override
    public void resolveDtoIds(UniDirDtoChild mappedDto, UniDirChild serviceEntity ){
        try {
            for (UniDirParent uniDirParent : serviceEntity.findParents()) {
                mappedDto.addParentsId(uniDirParent);
            }
        }catch (IllegalAccessException e){
            throw new RuntimeException(e);
        }
    }
}
