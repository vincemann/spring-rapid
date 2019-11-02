package io.github.vincemann.generic.crud.lib.controller.dtoMapper.idResolver.biDir.abs;

import io.github.vincemann.generic.crud.lib.controller.dtoMapper.idResolver.biDir.testEntities.BiDirEntityChild;
import io.github.vincemann.generic.crud.lib.controller.dtoMapper.idResolver.biDir.testEntities.BiDirEntityParent;
import io.github.vincemann.generic.crud.lib.controller.dtoMapper.idResolver.biDir.testEntities.BiDirSecondEntityParent;
import io.github.vincemann.generic.crud.lib.model.IdentifiableEntity;
import io.github.vincemann.generic.crud.lib.service.CrudService;
import io.github.vincemann.generic.crud.lib.service.finder.CrudServiceFinder;
import io.github.vincemann.generic.crud.lib.service.exception.NoIdException;
import lombok.Getter;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.mockito.Mockito.when;

@Getter
/**
 * Requires @MockitoSettings(strictness = Strictness.LENIENT) class level annotation on ChildClass
 */
public abstract class BiDirEntityResolverTest {

    @Mock
    private CrudServiceFinder crudServiceFinder;
    @Mock
    private CrudService<BiDirEntityChild,Long> entityChildCrudService;
    @Mock
    private CrudService<BiDirEntityParent,Long> entityParentCrudService;
    @Mock
    private CrudService<BiDirSecondEntityParent,Long> secondEntityParentCrudService;

    private BiDirEntityParent biDirEntityParent = new BiDirEntityParent();
    private BiDirSecondEntityParent biDirSecondEntityParent = new BiDirSecondEntityParent();
    private BiDirEntityChild biDirChild = new BiDirEntityChild();

    @BeforeEach
    public void setUp() throws NoIdException {
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

        Map<Class<? extends IdentifiableEntity>,CrudService> classCrudServiceMap = new HashMap<>();
        classCrudServiceMap.put(BiDirEntityParent.class,entityParentCrudService);
        classCrudServiceMap.put(BiDirSecondEntityParent.class,secondEntityParentCrudService);
        classCrudServiceMap.put(BiDirEntityChild.class,entityChildCrudService);

        when(crudServiceFinder.getCrudServices())
                .thenReturn(classCrudServiceMap);
    }
}
