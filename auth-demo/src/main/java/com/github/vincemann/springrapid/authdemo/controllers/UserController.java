package com.github.vincemann.springrapid.authdemo.controllers;

import com.github.vincemann.springrapid.auth.controller.AbstractUserController;
import com.github.vincemann.springrapid.auth.controller.UserDtoMappingContextBuilder;
import com.github.vincemann.springrapid.authdemo.dto.*;
import com.github.vincemann.springrapid.authdemo.model.User;
import com.github.vincemann.springrapid.authdemo.repositories.UserRepository;
import com.github.vincemann.springrapid.authdemo.service.MyUserService;
import com.github.vincemann.springrapid.core.controller.dto.mapper.context.Direction;
import com.github.vincemann.springrapid.core.controller.dto.mapper.context.DtoMappingContext;
import com.github.vincemann.springrapid.core.controller.dto.mapper.context.DtoRequestInfo;
import com.github.vincemann.springrapid.core.security.Roles;
import com.github.vincemann.springrapid.core.service.exception.BadEntityException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;

@Controller
public class UserController extends AbstractUserController<User, Long, MyUserService>  {

    @Autowired
    private UserRepository userRepository;

    @Override
    protected DtoMappingContext provideDtoMappingContext(UserDtoMappingContextBuilder builder) {
        return builder
                .withAllPrincipals()
                .withAllRoles()
                .forEndpoint(getUpdateUrl(), UserUpdatesOwnDto.class)

                .withAllPrincipals()
                .withAllRoles()
                .forEndpoint(getAuthProperties().getController().getSignupUrl(), Direction.REQUEST, MySignupDto.class)
                .forEndpoint(getAuthProperties().getController().getSignupUrl(), Direction.RESPONSE, MyFindOwnUserDto.class)

                .withAllPrincipals()
                .withAllRoles()
                .forEndpoint(getAuthProperties().getController().getVerifyUserUrl(),Direction.RESPONSE, MyFindOwnUserDto.class)

                .withAllRoles()
                .withPrincipal(DtoRequestInfo.Principal.OWN)
                .forResponse(MyFindOwnUserDto.class)

                .withAllPrincipals()
                .withRoles(Roles.ADMIN)
                .forEndpoint(getUpdateUrl(), MyFullUserDto.class)
                .build();
    }

//    @GetMapping("/user/changePassword")
//    public ModelAndView showChangePasswordPage(final ModelMap model, @RequestParam("code") final String code) {
////        final String result = securityUserService.validatePasswordResetToken(code);
////        model.addAttribute("messageKey", messageKey);
//        return new ModelAndView("redirect:/updatePassword");
//    }

    @GetMapping("/show-users")
    public String showUsers(Model model) {
        System.err.println("show users queried");
        model.addAttribute("users", userRepository.findAll());
        return "show-users";
    }

    @GetMapping("/reset-pass")
    public String showResetPassword(Model model, HttpServletRequest request) throws BadEntityException {
        System.err.println("reset passs queried");
        //        String code = readRequestParam(request, "code");
        //        model.addAttribute("code",code);
        model.addAttribute("changePasswordDto",new ChangePasswordDto());
        return "my-reset-password";
    }

    @PostMapping("/change-password")
    public String changePassword(HttpServletRequest request, @ModelAttribute ChangePasswordDto changePasswordDto) throws BadEntityException {
        System.err.println("change passs queried");
//        String code = readRequestParam(request, "code");
//        System.err.println("code: " + code);

        System.err.println("dto: " + changePasswordDto);
        System.err.println("showing users, bc why not");
        return "redirect:/show-users";
    }


}