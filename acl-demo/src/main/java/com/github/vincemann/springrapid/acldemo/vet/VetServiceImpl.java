package com.github.vincemann.springrapid.acldemo.vet;

import com.github.vincemann.springrapid.acl.service.AclUserService;
import com.github.vincemann.springrapid.auth.service.AbstractUserService;
import com.github.vincemann.springrapid.auth.Root;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;




@Root
@Service
public class VetServiceImpl
        extends AclUserService<Vet,Long, VetRepository>
                implements VetService {

    @Transactional
    @Override
    public Optional<Vet> findByLastName(String lastName) {
        return getRepository().findByLastName(lastName);
    }
}
