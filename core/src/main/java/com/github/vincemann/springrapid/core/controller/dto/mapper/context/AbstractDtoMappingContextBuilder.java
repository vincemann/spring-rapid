package com.github.vincemann.springrapid.core.controller.dto.mapper.context;

import com.github.vincemann.springrapid.core.controller.GenericCrudController;
import com.github.vincemann.springrapid.core.util.Lists;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.List;

@Slf4j
//needs to use full urls for endpoint matching
public abstract class AbstractDtoMappingContextBuilder<C extends GenericCrudController, B extends AbstractDtoMappingContextBuilder> {

    private DtoMappingContext mc;
    private List<String> currentRoles = new ArrayList<>();
    private DtoRequestInfo.Principal currPrincipal = DtoRequestInfo.Principal.ALL;
    private List<String> currUrlParams;


    private C controller;

    public AbstractDtoMappingContextBuilder(C controller) {
        this.controller = controller;
        this.mc=new DtoMappingContext();
    }

    public B context(DtoMappingContext mc){
        this.mc=mc;
        return (B) this;
    }

    public B withRoles(String... roles){
        this.currentRoles = Lists.newArrayList(roles);
        return (B) this;
    }

    public B withAllRoles(){
        this.currentRoles = new ArrayList<>();
        return (B) this;
    }

    public B withPrincipal(DtoRequestInfo.Principal principal){
        Assert.notNull(principal);
        this.currPrincipal=principal;
        return (B) this;
    }

    public B withUrlParams(String... urlParams){
        this.currUrlParams = Lists.newArrayList(urlParams);
        return (B) this;
    }


    public B withoutUrlParams(){
        this.currUrlParams = new ArrayList<>();
        return (B) this;
    }

    public B withAllPrincipals(){
        this.currPrincipal= DtoRequestInfo.Principal.ALL;
        return (B) this;
    }


    protected List<String> getAllEndpoints(){
        ArrayList<String> all = Lists.newArrayList(controller.getCreateUrl());
        all.addAll(getWriteEndpoints());
        all.addAll(getFindEndpoints());
        return all;
    }

    protected List<String> getFindEndpoints(){
        return Lists.newArrayList(controller.getFindUrl(), controller.getFindAllUrl(), controller.getFindSomeUrl());
    }

    protected List<String> getWriteEndpoints(){
        ArrayList<String> writeEndpoints = Lists.newArrayList(controller.getCreateUrl());
        writeEndpoints.addAll(getUpdateEndpoints());
        return writeEndpoints;
    }

    protected List<String> getUpdateEndpoints(){
        return Lists.newArrayList(controller.getUpdateUrl());
    }

    /**
     * Uses one dto class for all crud operations
     * @param defaultDtoClass
     * @return
     */
    public B forAll(Class<?> defaultDtoClass){
        Assert.notNull(defaultDtoClass);
        List<DtoRequestInfo> infoList = create(getAllEndpoints());
        for (DtoRequestInfo info : infoList) {
            addEntry(info,defaultDtoClass);
        }
        return (B) this;
    }

    public B forResponse(Class<?> responseDtoClass){
        Assert.notNull(responseDtoClass);
        return forDirection(Direction.RESPONSE,responseDtoClass);
    }

    public B forRequest(Class<?> responseDtoClass){
        Assert.notNull(responseDtoClass);
        return forDirection(Direction.REQUEST,responseDtoClass);
    }

    private B forDirection(Direction direction,Class<?> responseDtoClass){
        Assert.notNull(direction);
        Assert.notNull(responseDtoClass);
        List<String> allEndpoints = getAllEndpoints();
        List<DtoRequestInfo> infoList = new ArrayList<>();
        for (String endpoint : allEndpoints) {
            infoList.add(create(endpoint,direction));
        }
        for (DtoRequestInfo info : infoList) {
            addEntry(info,responseDtoClass);
        }
        return (B) this;
    }

    public B forFind(Class<?> readDtoClass){
        Assert.notNull(readDtoClass);
        List<DtoRequestInfo> infoList = create(getFindEndpoints());
        for (DtoRequestInfo info : infoList) {
            addEntry(info,readDtoClass);
        }
        return (B) this;
    }

    public B forWrite(Class<?> writeDtoClass){
        Assert.notNull(writeDtoClass);
        List<DtoRequestInfo> infoList = create(getWriteEndpoints());
        for (DtoRequestInfo info : infoList) {
            addEntry(info,writeDtoClass);
        }
        return (B) this;
    }

    public B forUpdate(Direction direction,Class<?> updateDtoClass){
        Assert.notNull(updateDtoClass);
        Assert.notNull(direction);
        List<String> updateEndpoints = getUpdateEndpoints();
        for (String updateEndpoint : updateEndpoints) {
            addEntry(create(updateEndpoint,direction),updateDtoClass);
        }
        return (B) this;
    }

    public B forUpdate(Class<?> updateDtoClass){
        Assert.notNull(updateDtoClass);
        List<String> updateEndpoints = getUpdateEndpoints();
        for (String updateEndpoint : updateEndpoints) {
            addEntry(create(updateEndpoint,Direction.REQUEST),updateDtoClass);
            addEntry(create(updateEndpoint,Direction.RESPONSE),updateDtoClass);
        }
        return (B) this;
    }

    public B forEndpoint(String endpoint, Class<?> dtoClass){
        Assert.notNull(endpoint);
        Assert.notNull(dtoClass);
        addEntry(create(endpoint,Direction.REQUEST),dtoClass);
        addEntry(create(endpoint,Direction.RESPONSE),dtoClass);
        return (B) this;
    }


    public B forEndpoint(String endpoint, Direction direction, Class<?> dtoClass){
        Assert.notNull(endpoint);
        Assert.notNull(dtoClass);
        Assert.notNull(direction);
        addEntry(create(endpoint,direction),dtoClass);
        return (B) this;
    }

    public B forEndpoint(String endpoint, Direction direction,List<String> urlParams, Class<?> dtoClass){
        Assert.notNull(endpoint);
        Assert.notNull(dtoClass);
        Assert.notNull(direction);
        Assert.notNull(urlParams);
        Assert.notEmpty(urlParams);
        addEntry(create(endpoint,direction,urlParams),dtoClass);
        return (B) this;
    }

    public B forEndpoint(String endpoint, Direction direction,List<String> urlParams, List<String> roles, Class<?> dtoClass){
        Assert.notNull(endpoint);
        Assert.notNull(dtoClass);
        Assert.notNull(direction);
        Assert.notNull(roles);
        Assert.notNull(dtoClass);
        DtoRequestInfo info = create(endpoint, direction,urlParams,roles);
        info.setAuthorities(roles);
        addEntry(info,dtoClass);
        return (B) this;
    }

    public B forInfo(DtoRequestInfo info, Class<?> dtoClass){
        Assert.notNull(dtoClass);
        Assert.notNull(info);
        addEntry(info,dtoClass);
        return (B) this;
    }

    protected void addEntry(DtoRequestInfo info, Class<?> dtoClass){
        Class<?> old = mc.getMappingEntries().get(info);
        mc.getMappingEntries().put(info, dtoClass);
        if (old!=null) {
            if (!old.equals(dtoClass)) {
                if (log.isDebugEnabled())
                    log.debug("Overriding dto mapping info: " + info + ", old DtoClass: " + old.getSimpleName() + ", new DtoClass: " + dtoClass.getSimpleName());
            }
        }
    }

    public DtoMappingContext build(){
        //create ignore Role Map
//        mc.getMappingEntries().entrySet().forEach(e ->{
//            DtoMappingInfo ignoreRoleInfo = new DtoMappingInfo(e.getKey());
//            ignoreRoleInfo.getAuthorities().clear();
//            mc.getMappingEntriesIgnoreRole()
//                    .computeIfAbsent(ignoreRoleInfo, k -> e.getValue());
//        });
        return mc;
    }


    /**
     * Create info for all given endpoints and for all directions
     * @param endpoints
     * @return
     */
    private List<DtoRequestInfo> create(List<String> endpoints) {
        List<DtoRequestInfo> infoList = new ArrayList<>();
        for (String endpoint : endpoints) {
            infoList.add(create(endpoint,Direction.REQUEST));
            infoList.add(create(endpoint,Direction.RESPONSE));
        }
        return infoList;
    }

    private DtoRequestInfo create(String endpoint, Direction direction){
        return DtoRequestInfo.builder()
                .authorities(currentRoles)
                .endpoint(endpoint)
                .principal(currPrincipal)
                .urlParams(currUrlParams)
                .direction(direction)
                .build();
    }

    private DtoRequestInfo create(String endpoint, Direction direction, List<String> urlParams){
        return DtoRequestInfo.builder()
                .authorities(currentRoles)
                .endpoint(endpoint)
                .principal(currPrincipal)
                .direction(direction)
                .urlParams(urlParams)
                .build();
    }

    private DtoRequestInfo create(String endpoint, Direction direction, List<String> urlParams, List<String> roles){
        return DtoRequestInfo.builder()
                .authorities(roles)
                .endpoint(endpoint)
                .principal(currPrincipal)
                .direction(direction)
                .urlParams(urlParams)
                .build();
    }

    protected C getController() {
        return controller;
    }
}
