package com.github.vincemann.springrapid.entityrelationship.controller.uniDir;


import com.github.vincemann.springrapid.core.service.exception.EntityNotFoundException;
import com.github.vincemann.springrapid.entityrelationship.controller.dtomapper.UniDirParentIdResolver;
import com.github.vincemann.springrapid.entityrelationship.controller.uniDir.abs.UniDirEntityResolverTest;
import com.github.vincemann.springrapid.entityrelationship.controller.uniDir.testEntities.UniDirEntityParent;
import com.github.vincemann.springrapid.entityrelationship.controller.uniDir.testEntities.UniDirEntityParentDto;
import com.github.vincemann.springrapid.core.service.exception.BadEntityException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class UniDirParentResolverTest extends UniDirEntityResolverTest {

    private UniDirParentIdResolver uniDirParentIdResolver;

    @BeforeEach
    @Override
    public void setUp() throws BadEntityException {
        super.setUp();
        this.uniDirParentIdResolver = new UniDirParentIdResolver(getCrudServiceLocator());
    }

    @Test
    public void resolveServiceEntityIds() throws EntityNotFoundException, BadEntityException {
        //given
        UniDirEntityParent unfinishedMappedUniDirParent = new UniDirEntityParent();
        UniDirEntityParentDto parentDto = new UniDirEntityParentDto();
        parentDto.setChildId(getUniDirEntityParentsChild().getId());
        //when
        uniDirParentIdResolver.resolveEntityIds(unfinishedMappedUniDirParent,parentDto);
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
        uniDirParentIdResolver.resolveDtoIds(unfinishedMappedUniDirParentDto,parent);
        //then
        Assertions.assertEquals(getUniDirEntityParentsChild().getId(),unfinishedMappedUniDirParentDto.getChildId());
    }
}
