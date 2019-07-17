package io.github.vincemann.generic.crud.lib.controller.dtoMapper.idResolver.uniDir;

import io.github.vincemann.generic.crud.lib.controller.dtoMapper.EntityMappingException;
import io.github.vincemann.generic.crud.lib.controller.dtoMapper.idResolver.uniDir.abs.UniDirEntityResolverTest;
import io.github.vincemann.generic.crud.lib.controller.dtoMapper.idResolver.uniDir.testEntities.UniDirEntityParent;
import io.github.vincemann.generic.crud.lib.controller.dtoMapper.idResolver.uniDir.testEntities.UniDirEntityParentDto;
import io.github.vincemann.generic.crud.lib.service.exception.NoIdException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class UniDirParentResolverTest extends UniDirEntityResolverTest {

    private UniDirParentResolver uniDirParentResolver;

    @BeforeEach
    @Override
    public void setUp() throws NoIdException {
        super.setUp();
        this.uniDirParentResolver = new UniDirParentResolver(getCrudServiceFinder());
    }

    @Test
    public void resolveServiceEntityIds() throws EntityMappingException {
        //given
        UniDirEntityParent unfinishedMappedUniDirParent = new UniDirEntityParent();
        UniDirEntityParentDto parentDto = new UniDirEntityParentDto();
        parentDto.setChildId(getUniDirEntityParentsChild().getId());
        //when
        uniDirParentResolver.resolveServiceEntityIds(unfinishedMappedUniDirParent,parentDto);
        //then
        Assertions.assertEquals(getUniDirEntityParentsChild(),unfinishedMappedUniDirParent.getEntityChild());
    }

    @Test
    void resolveDtoIds() {
        //given
        UniDirEntityParentDto unfinishedMappedUniDirParentDto = new UniDirEntityParentDto();
        UniDirEntityParent parent = new UniDirEntityParent();
        parent.setEntityChild(getUniDirEntityParentsChild());
        //when
        uniDirParentResolver.resolveDtoIds(unfinishedMappedUniDirParentDto,parent);
        //then
        Assertions.assertEquals(getUniDirEntityParentsChild().getId(),unfinishedMappedUniDirParentDto.getChildId());
    }
}
