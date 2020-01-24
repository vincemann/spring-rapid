package io.github.vincemann.generic.crud.lib.controller.dtoMapper.idResolver;

import io.github.vincemann.generic.crud.lib.controller.dtoMapper.idResolver.biDir.BiDirChildIdResolver;
import io.github.vincemann.generic.crud.lib.controller.dtoMapper.idResolver.biDir.BiDirParentIdResolver;
import io.github.vincemann.generic.crud.lib.controller.dtoMapper.idResolver.uniDir.UniDirChildIdResolver;
import io.github.vincemann.generic.crud.lib.controller.dtoMapper.idResolver.uniDir.UniDirParentIdResolver;
import io.github.vincemann.generic.crud.lib.service.finder.CrudServiceFinder;
import org.junit.jupiter.api.BeforeEach;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.mock;

//class IdResolvingDtoMapperTest {
//
//    private IdResolvingDtoMapper mapper;
//    private CrudServiceFinder crudServiceFinder;
//
//
//    @BeforeEach
//    void setUp() {
//        crudServiceFinder = mock(CrudServiceFinder.class);
//
//        BiDirChildIdResolver biDirChildIdResolver = new BiDirChildIdResolver(crudServiceFinder);
//        UniDirChildIdResolver uniDirChildIdResolver = new UniDirChildIdResolver(crudServiceFinder);
//        UniDirParentIdResolver uniDirParentIdResolver = new UniDirParentIdResolver(crudServiceFinder);
//        BiDirParentIdResolver biDirParentIdResolver = new BiDirParentIdResolver(crudServiceFinder);
//        List<EntityIdResolver> entityIdResolverList = Arrays.asList(biDirChildIdResolver,uniDirChildIdResolver,uniDirParentIdResolver,biDirParentIdResolver);
//        this.mapper = new IdResolvingDtoMapper(entityIdResolverList);
//    }
//}