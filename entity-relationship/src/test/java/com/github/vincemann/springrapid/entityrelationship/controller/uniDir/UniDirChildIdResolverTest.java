package com.github.vincemann.springrapid.entityrelationship.controller.uniDir;


import com.github.vincemann.springrapid.core.service.exception.EntityNotFoundException;
import com.github.vincemann.springrapid.entityrelationship.controller.dtomapper.UniDirChildIdResolver;
import com.github.vincemann.springrapid.entityrelationship.controller.uniDir.abs.UniDirEntityResolverTest;
import com.github.vincemann.springrapid.entityrelationship.controller.uniDir.testEntities.UniDirEntityChild;
import com.github.vincemann.springrapid.entityrelationship.controller.uniDir.testEntities.UniDirEntityChildDto;
import com.github.vincemann.springrapid.core.service.exception.BadEntityException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class UniDirChildIdResolverTest extends UniDirEntityResolverTest {

    private UniDirChildIdResolver uniDirChildIdResolver;

    @BeforeEach
    @Override
    public void setUp() throws BadEntityException {
        super.setUp();
        this.uniDirChildIdResolver = new UniDirChildIdResolver(getCrudServiceLocator());
    }

    @Test
    public void resolveServiceEntityIds() throws EntityNotFoundException, BadEntityException {
        //given
        UniDirEntityChild unfinishedMappedUniDirChild = new UniDirEntityChild();
        UniDirEntityChildDto childDto = new UniDirEntityChildDto();
        childDto.setParentId(getUniDirEntityChildsParent().getId());
        //when
        uniDirChildIdResolver.resolveEntityIds(unfinishedMappedUniDirChild,childDto);
        //then
        Assertions.assertEquals(getUniDirEntityChildsParent(),unfinishedMappedUniDirChild.getUniDirEntityChildsParent());
    }

    @Test
    void resolveDtoIds() {
        //given
        UniDirEntityChildDto unfinishedMappedUniDirChildDto = new UniDirEntityChildDto();
        UniDirEntityChild child = new UniDirEntityChild();
        child.setUniDirEntityChildsParent(getUniDirEntityChildsParent());
        //when
        uniDirChildIdResolver.resolveDtoIds(unfinishedMappedUniDirChildDto,child);
        //then
        Assertions.assertEquals(getUniDirEntityChildsParent().getId(),unfinishedMappedUniDirChildDto.getParentId());
    }
}
