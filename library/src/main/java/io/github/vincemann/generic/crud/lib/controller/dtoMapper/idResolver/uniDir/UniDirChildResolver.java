package io.github.vincemann.generic.crud.lib.controller.dtoMapper.idResolver.uniDir;

import io.github.vincemann.generic.crud.lib.controller.dtoMapper.EntityMappingException;
import io.github.vincemann.generic.crud.lib.controller.dtoMapper.idResolver.EntityIdResolver;
import io.github.vincemann.generic.crud.lib.dto.uniDir.UniDirChildDto;
import io.github.vincemann.generic.crud.lib.model.IdentifiableEntity;
import io.github.vincemann.generic.crud.lib.model.uniDir.UniDirChild;
import io.github.vincemann.generic.crud.lib.service.finder.CrudServiceFinder;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.Map;

@Component
public class UniDirChildResolver extends EntityIdResolver<UniDirChild, UniDirChildDto> {

    public UniDirChildResolver(CrudServiceFinder crudServiceFinder) {
        super(crudServiceFinder, UniDirChildDto.class);
    }

    public void resolveServiceEntityIds(UniDirChild mappedUniDirChild, UniDirChildDto uniDirChildDto) throws EntityMappingException {
        try {
            Map<Class, Serializable> allParentIdToClassMappings = uniDirChildDto.findAllParentIds();
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
    public void resolveDtoIds(UniDirChildDto mappedDto, UniDirChild serviceEntity ){
        try {
            for (Object uniDirParent : serviceEntity.findParents()) {
                mappedDto.addParentsId((IdentifiableEntity) uniDirParent);
            }
        }catch (IllegalAccessException e){
            throw new RuntimeException(e);
        }
    }
}
