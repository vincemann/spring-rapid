package com.github.vincemann.springrapid.core.controller.dto.mapper.context;

import com.github.vincemann.springrapid.core.controller.DtoClassLocator;
import com.github.vincemann.springrapid.core.controller.RoleFallbackDtoClassLocator;
import com.github.vincemann.springrapid.core.model.IdentifiableEntityImpl;
import com.github.vincemann.springrapid.core.util.Lists;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

class CrudDtoMappingContextBuilderTest {

    DtoMappingContext context;
    DtoRequestInfo findInfo;
    DtoRequestInfo createInfo;
    DtoRequestInfo findAllInfo;
    List<String> roles;
    String userRole = "ROLE_USER";
    String adminRole = "ROLE_ADMIN";
    String peekRole = "ROLE_PEEK_DETAILED_USER_INFO";
    DtoRequestInfo updateInfo;
    
    DtoClassLocator locator;


    class PrivilegedFindDto extends IdentifiableEntityImpl<Long> {

    }

    class CreateDto extends IdentifiableEntityImpl<Long>{

    }

    class AdminUpdateForeignUserDto extends IdentifiableEntityImpl<Long>{

    }

    class AdminUpdateOwnDto extends IdentifiableEntityImpl<Long>{

    }

    class LessPrivilegedFindDto extends IdentifiableEntityImpl<Long>{

    }

    private static final String FIND_URL = "/api/core/find";
    private static final String FIND_ALL_URL = "/api/core/findAll";
    private static final String CREATE_URL = "/api/core/create";
    private static final String UPDATE_URL = "/api/core/update";

    @BeforeEach
    void setUp() {
        locator = new RoleFallbackDtoClassLocator();
        roles = Lists.newArrayList(userRole,peekRole);
        // controller not needed bc all endpoints are manually supplied
        context = new CrudDtoMappingContextBuilder(null)
                //user role and peek role
                .withRoles(roles.toArray(new String[0]))
                .forEndpoint(FIND_URL,Direction.RESPONSE, PrivilegedFindDto.class)
                .forEndpoint(FIND_ALL_URL,Direction.RESPONSE,PrivilegedFindDto.class)

                .withRoles(adminRole)
                .withPrincipal(DtoRequestInfo.Principal.FOREIGN)
                .forEndpoint(UPDATE_URL,Direction.REQUEST,AdminUpdateForeignUserDto.class)
                .withPrincipal(DtoRequestInfo.Principal.OWN)
                .forEndpoint(UPDATE_URL,Direction.REQUEST,AdminUpdateOwnDto.class)


                .withAllRoles()
                .withAllPrincipals()
                .forEndpoint(CREATE_URL,CreateDto.class)
                .forEndpoint(FIND_URL,Direction.RESPONSE,LessPrivilegedFindDto.class)
                .build();

        findInfo = DtoRequestInfo.builder()
                .direction(Direction.RESPONSE)
                .endpoint(FIND_URL)
                .build();

        findAllInfo = DtoRequestInfo.builder()
                .endpoint(FIND_ALL_URL)
                .direction(Direction.RESPONSE)
                .build();

        createInfo = DtoRequestInfo.builder()
                .direction(Direction.REQUEST)
                .endpoint(CREATE_URL)
                .build();

        updateInfo = DtoRequestInfo.builder()
                .direction(Direction.REQUEST)
                .endpoint(UPDATE_URL)
                .build();
    }

    @Test
    void findCreateEntryWithoutRoles_shouldFind() throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        //when
        createInfo.setAuthorities(new ArrayList<>());
        Class<?> foundClass = locator.find(createInfo,context);
        //then
        Assertions.assertEquals(CreateDto.class,foundClass);
    }

    @Test
    void findCreateEntryWithRoles_shouldFindAndSilentIgnoreRoles() throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        //when
        createInfo.setAuthorities(roles);
        Class<?> foundClass = locator.find(createInfo,context);
        //then
        Assertions.assertEquals(CreateDto.class,foundClass);
    }

    @Test
    void findFindEntryWithRoles_shouldFindPrivEntry() throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        //when
        findInfo.setAuthorities(roles);
        Class<?> foundClass = locator.find(findInfo,context);
        //then
        Assertions.assertEquals(PrivilegedFindDto.class,foundClass);
    }

    @Test
    void findFindEntryWithoutRoles_shouldFindLessPrivEntry() throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        findInfo.setAuthorities(new ArrayList<>());
        Class<?> foundClass = locator.find(findInfo,context);
        //then
        Assertions.assertEquals(LessPrivilegedFindDto.class,foundClass);
    }

    @Test
    void findOnlyAdminEntryWithoutRoles_shouldNotFind() throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        findAllInfo.setAuthorities(new ArrayList<>());
        Assertions.assertThrows(IllegalArgumentException.class,()-> locator.find(findAllInfo,context));
//        Class<?> dtoClass = locator.find(findAllInfo, context);
//        Assertions.assertEquals(LessPrivilegedFindDto.class,dtoClass);
    }

    @Test
    void findOnlyAdminEntryWithAllPlusMoreRoles_shouldFind() throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        ArrayList<String> moreRoles = Lists.newArrayList(roles);
        moreRoles.add("NEW_FANCY_ROLE");
        findAllInfo.setAuthorities(moreRoles);
        Class<?> foundClass = locator.find(findAllInfo,context);
        Assertions.assertEquals(PrivilegedFindDto.class,foundClass);
    }

    @Test
    void findFindEntryWithNotEnoughRoles_shouldFindLessPriv() throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        ArrayList<String> lessRoles = Lists.newArrayList(roles);
        lessRoles.remove(0);
        findInfo.setAuthorities(lessRoles);
        Class<?> foundClass = locator.find(findInfo,context);
        Assertions.assertEquals(LessPrivilegedFindDto.class,foundClass);
    }

    @Test
    void findUnknownEntry_shouldNotFind(){
        DtoRequestInfo unknown = DtoRequestInfo.builder()
                .authorities(new ArrayList<>())
                .direction(Direction.REQUEST)
                .endpoint(FIND_URL)
                .build();
        Assertions.assertThrows(IllegalArgumentException.class,()-> locator.find(unknown,context));
    }

    @Test
    void findFindEntryWithWrongRoles_shouldFindLessPriv() throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        ArrayList<String> wrongRoles = Lists.newArrayList(roles);
        wrongRoles.remove(roles.size()-1);
        wrongRoles.add("WRONG_ROLE");
        findInfo.setAuthorities(wrongRoles);
        Class<?> foundClass = locator.find(findInfo,context);
        Assertions.assertEquals(LessPrivilegedFindDto.class,foundClass);
    }

    @Test
    public void adminUpdatesOwn(){
        updateInfo.setAuthorities(Lists.newArrayList(adminRole));
        updateInfo.setPrincipal(DtoRequestInfo.Principal.OWN);
        Class<?> foundClass = locator.find(updateInfo,context);
        Assertions.assertEquals(AdminUpdateOwnDto.class,foundClass);
    }

    @Test
    public void adminUpdatesForeign(){
        updateInfo.setAuthorities(Lists.newArrayList(adminRole));
        updateInfo.setPrincipal(DtoRequestInfo.Principal.FOREIGN);
        Class<?> foundClass = locator.find(updateInfo,context);
        Assertions.assertEquals(AdminUpdateForeignUserDto.class,foundClass);
    }

}