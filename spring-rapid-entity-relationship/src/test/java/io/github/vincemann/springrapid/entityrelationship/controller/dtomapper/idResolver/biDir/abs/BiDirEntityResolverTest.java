package io.github.vincemann.springrapid.entityrelationship.controller.dtomapper.idResolver.biDir.abs;

import io.github.vincemann.springrapid.entityrelationship.controller.dtomapper.idResolver.biDir.testEntities.BiDirEntityChild;
import io.github.vincemann.springrapid.entityrelationship.controller.dtomapper.idResolver.biDir.testEntities.BiDirEntityParent;
import io.github.vincemann.springrapid.entityrelationship.controller.dtomapper.idResolver.biDir.testEntities.BiDirSecondEntityParent;
import io.github.vincemann.springrapid.core.service.CrudService;
import io.github.vincemann.springrapid.core.service.exception.BadEntityException;
import io.github.vincemann.springrapid.core.service.locator.CrudServiceLocator;
import lombok.Getter;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

import static org.mockito.Mockito.when;

@Getter
/**
 * Requires @MockitoSettings(strictness = Strictness.LENIENT) class level annotation on ChildClass
 */
public abstract class BiDirEntityResolverTest {

    @Mock
    private CrudServiceLocator crudServiceLocator;
    @Mock
    private CrudService<BiDirEntityChild,Long, CrudRepository<BiDirEntityChild,Long>> entityChildCrudService;
    @Mock
    private CrudService<BiDirEntityParent,Long,CrudRepository<BiDirEntityParent,Long>> entityParentCrudService;
    @Mock
    private CrudService<BiDirSecondEntityParent,Long,CrudRepository<BiDirSecondEntityParent,Long>> secondEntityParentCrudService;

    private BiDirEntityParent biDirEntityParent = new BiDirEntityParent();
    private BiDirSecondEntityParent biDirSecondEntityParent = new BiDirSecondEntityParent();
    private BiDirEntityChild biDirChild = new BiDirEntityChild();

    @BeforeEach
    public void setUp() throws BadEntityException {
        MockitoAnnotations.initMocks(this);

        Long entityParentId = 1L;
        Long secondEntityParentId = 2L;
        Long childId = 3L;
        biDirEntityParent.setId(entityParentId);
        biDirSecondEntityParent.setId(secondEntityParentId);
        biDirChild.setId(childId);

        when(entityParentCrudService.findById(entityParentId))
                .thenReturn(Optional.of(biDirEntityParent));
        when(secondEntityParentCrudService.findById(secondEntityParentId))
                .thenReturn(Optional.of(biDirSecondEntityParent));
        when(entityChildCrudService.findById(childId))
                .thenReturn(Optional.of(biDirChild));


        when(crudServiceLocator.find(BiDirEntityParent.class))
                .thenReturn(entityParentCrudService);
        when(crudServiceLocator.find(BiDirSecondEntityParent.class))
                .thenReturn(secondEntityParentCrudService);
        when(crudServiceLocator.find(BiDirEntityChild.class))
                .thenReturn(entityChildCrudService);
    }
}
