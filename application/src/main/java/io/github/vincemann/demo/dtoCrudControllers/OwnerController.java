package io.github.vincemann.demo.dtoCrudControllers;

import io.github.vincemann.demo.dtos.OwnerDto;
import io.github.vincemann.demo.plugins.AclPlugin;
import io.github.vincemann.demo.plugins.PersonNameSavingPlugin;
import io.github.vincemann.demo.model.Owner;
import io.github.vincemann.demo.service.OwnerService;
import io.github.vincemann.generic.crud.lib.controller.springAdapter.DtoCrudControllerSpringAdapter;
import io.github.vincemann.generic.crud.lib.controller.springAdapter.EndpointsExposureDetails;
import io.github.vincemann.generic.crud.lib.controller.springAdapter.idFetchingStrategy.IdFetchingStrategy;
import io.github.vincemann.generic.crud.lib.controller.springAdapter.mediaTypeStrategy.MediaTypeStrategy;
import io.github.vincemann.generic.crud.lib.controller.springAdapter.validationStrategy.ValidationStrategy;
import io.github.vincemann.generic.crud.lib.controller.dtoMapper.DtoMapper;
import io.github.vincemann.generic.crud.lib.service.EndpointService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class OwnerController extends DtoCrudControllerSpringAdapter<Owner, OwnerDto,Long,OwnerService> {


    @Autowired
    public OwnerController(OwnerService crudService,
                           MediaTypeStrategy mediaTypeStrategy,
                           IdFetchingStrategy<Long> longIdFetchingStrategy,
                           DtoMapper dtoMapper,
                           ValidationStrategy validationStrategy,
                           EndpointsExposureDetails endpointsExposureDetails,
                           EndpointService endpointService){
        super(
                crudService,
                endpointService,
                longIdFetchingStrategy,
                mediaTypeStrategy,
                validationStrategy,
                dtoMapper,
                endpointsExposureDetails,
                new AclPlugin(),
                new PersonNameSavingPlugin()
                );
    }


    @RequestMapping("/owners")
    public String listOwners(Model model){
        model.addAttribute("owners",getCrudService().findAll());
        return "owners/index";
    }
}
