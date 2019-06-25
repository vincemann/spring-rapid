package io.github.vincemann.demo.service.springDataJPA.unit;

import io.github.vincemann.demo.model.Owner;
import io.github.vincemann.demo.service.springDataJPA.OwnerJPAService;
import io.github.vincemann.demo.jpaRepositories.OwnerRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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
