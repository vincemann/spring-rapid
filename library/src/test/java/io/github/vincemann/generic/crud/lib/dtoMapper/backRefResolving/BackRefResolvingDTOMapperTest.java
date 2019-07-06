package io.github.vincemann.generic.crud.lib.dtoMapper.backRefResolving;

import io.github.vincemann.generic.crud.lib.bidir.BiDirDTOChild;
import io.github.vincemann.generic.crud.lib.model.IdentifiableEntityImpl;
import io.github.vincemann.generic.crud.lib.model.biDir.*;
import io.github.vincemann.generic.crud.lib.service.CrudService;
import io.github.vincemann.generic.crud.lib.service.springDataJpa.JPACrudService;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BackRefResolvingDTOMapperTest {

    /*private class EntityParent extends IdentifiableEntityImpl<Long> implements BiDirParent {
        @BiDirChildEntity(EntityChild.class)
        private EntityChild entityChild;
    }

    private class EntityParentDTO extends IdentifiableEntityImpl<Long>{
        private EntityChildDTO entityChild;
    }

    private class EntityChild extends IdentifiableEntityImpl<Long> implements BiDirChild {
        @BiDirParentEntity
        private EntityParent entityParent;
    }

    private class EntityChildDTO extends IdentifiableEntityImpl<Long> implements BiDirDTOChild<Long> {
        @BiDirParentId(EntityParent.class)
        private Long entityParentId;
    }

    @Test
    void mapParentDTOToParent() {
        CrudService<EntityParent,Long> parentService = null;
        BackRefResolvingConverter<EntityChildDTO,EntityChild,EntityParent,Long,Long> backRefResolvingConverter = new BackRefResolvingConverter<>(null,null);
        BackRefResolvingDTOMapper<EntityParentDTO,EntityParent,Long> mapper = new BackRefResolvingDTOMapper<>(EntityParent.class,);

    }*/
}