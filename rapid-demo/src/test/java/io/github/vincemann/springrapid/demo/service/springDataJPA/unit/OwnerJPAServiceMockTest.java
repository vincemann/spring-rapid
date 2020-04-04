package io.github.vincemann.springrapid.demo.service.springDataJPA.unit;

import static org.mockito.ArgumentMatchers.any;

/*
@ExtendWith(MockitoExtension.class)
public class OwnerJPAServiceMockTest {

    @Mock
    private OwnerRepository ownerRepository;

    @InjectMocks
    private OwnerJPAService ownerJPAService;

    @Test
    public void findByLastName(){
        Optional<Owner> returnOwner = Optional.of(Owner.builder().lastName("meier").build());
        returnOwner.get().setId(11L);
        when(ownerRepository.findByLastName(any())).thenReturn(returnOwner);

        Optional<Owner> ownerOptional = ownerRepository.findByLastName("meier");
        Assertions.assertTrue(ownerOptional.isPresent());
        Assertions.assertEquals(returnOwner.get(),ownerOptional.get());
    }
}
*/