package vincemann.github.generic.crud.lib.demo.service.springDataJPA.unit;

import org.mockito.junit.jupiter.MockitoExtension;
import vincemann.github.generic.crud.lib.demo.model.Owner;
import vincemann.github.generic.crud.lib.demo.springDataJPA.OwnerRepository;
import vincemann.github.generic.crud.lib.demo.service.springDataJPA.OwnerJPAService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

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
