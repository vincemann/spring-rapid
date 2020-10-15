package com.github.vincemann.springlemon.auth.controller;

import com.github.vincemann.springlemon.auth.LemonProperties;
import com.github.vincemann.springrapid.core.controller.GenericCrudController;
import com.github.vincemann.springrapid.core.controller.dto.mapper.context.AbstractDtoMappingContextBuilder;
import com.github.vincemann.springrapid.core.controller.dto.mapper.context.CrudDtoMappingContextBuilder;
import com.google.common.collect.Sets;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

//lemon uses full urls in properties, thus i can get the full urls from there
public class UserDtoMappingContextBuilder extends AbstractDtoMappingContextBuilder<AbstractUserController,UserDtoMappingContextBuilder> {


    public UserDtoMappingContextBuilder(AbstractUserController controller) {
        super(controller);
    }

    @Override
    protected List<String> getAllEndpoints() {
        List<String> allEndpoints = super.getAllEndpoints();
        allEndpoints.addAll(Sets.newHashSet(
                getController().getLemonProperties().userController.signupUrl,
                getController().getLemonProperties().userController.resetPasswordUrl,
                getController().getLemonProperties().userController.fetchByEmailUrl,
                getController().getLemonProperties().userController.changeEmailUrl,
                getController().getLemonProperties().userController.verifyUserUrl));
        return allEndpoints;
    }

    @Override
    protected List<String> getFindEndpoints() {
        List<String> findEndpoints = super.getFindEndpoints();
        findEndpoints.add(getController().getLemonProperties().userController.fetchByEmailUrl);
        return findEndpoints;
    }
    
}
