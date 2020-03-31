package io.github.vincemann.generic.crud.lib.controller.dtoMapper.context;

import com.google.common.collect.Lists;
import io.github.vincemann.generic.crud.lib.model.IdentifiableEntity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DtoMappingContextBuilder {
    private String defaultRole;
    private DtoMappingContext mc;

    public DtoMappingContextBuilder() {
        this.mc = new DtoMappingContext();
        this.mc.setIgnoreRole(true);
    }

    public DtoMappingContextBuilder(String defaultRole) {
        this.mc = new DtoMappingContext();
        if(defaultRole!=null){
            this.mc.setIgnoreRole(false);
        }
        this.defaultRole = defaultRole;
    }

    public static DtoMappingContextBuilder ignoreRole(){
        return new DtoMappingContextBuilder();
    }


    public static DtoMappingContextBuilder withDefaultRole(String defaultRole){
        return new DtoMappingContextBuilder(defaultRole);
    }


    protected List<Integer> getAllEndpoints(){
        return Arrays.asList(DtoUsingEndpoint.CREATE, DtoUsingEndpoint.FIND, DtoUsingEndpoint.FIND_ALL, DtoUsingEndpoint.FULL_UPDATE, DtoUsingEndpoint.PARTIAL_UPDATE);
    }

    protected List<Integer> getFindEndpoints(){
        return Arrays.asList(DtoUsingEndpoint.FIND, DtoUsingEndpoint.FIND_ALL);
    }

    protected List<Integer> getWriteEndpoints(){
        return Arrays.asList(DtoUsingEndpoint.CREATE, DtoUsingEndpoint.FULL_UPDATE, DtoUsingEndpoint.PARTIAL_UPDATE);
    }

    protected List<Integer> getUpdateEndpoints(){
        return Arrays.asList(DtoUsingEndpoint.FULL_UPDATE, DtoUsingEndpoint.PARTIAL_UPDATE);
    }

    /**
     * Uses one dto class for all crud operations
     * @param defaultDtoClass
     * @return
     */
    public DtoMappingContextBuilder forAll(Class<? extends IdentifiableEntity> defaultDtoClass){
        List<DtoMappingInfo> infoList = createInfo(getAllEndpoints());
        for (DtoMappingInfo info : infoList) {
            mc.getMappingEntries().put(info,defaultDtoClass);
        }
        return this;
    }

    public DtoMappingContextBuilder forResponse(Class<? extends IdentifiableEntity> responseDtoClass){
        return forDirection(Direction.RESPONSE,responseDtoClass);
    }

    public DtoMappingContextBuilder forRequest(Class<? extends IdentifiableEntity> responseDtoClass){
        return forDirection(Direction.REQUEST,responseDtoClass);
    }

    private DtoMappingContextBuilder forDirection(Direction direction,Class<? extends IdentifiableEntity> responseDtoClass){
        List<Integer> allEndpoints = getAllEndpoints();
        List<DtoMappingInfo> infoList = new ArrayList<>();
        for (Integer endpoint : allEndpoints) {
            infoList.add(createInfo(endpoint,direction));
        }
        for (DtoMappingInfo info : infoList) {
            mc.getMappingEntries().put(info,responseDtoClass);
        }
        return this;
    }

    public DtoMappingContextBuilder forFind(Class<? extends IdentifiableEntity> readDtoClass){
        List<DtoMappingInfo> infoList = createInfo(getFindEndpoints());
        for (DtoMappingInfo info : infoList) {
            mc.getMappingEntries().put(info,readDtoClass);
        }
        return this;
    }

    public DtoMappingContextBuilder forWrite(Class<? extends IdentifiableEntity> writeDtoClass){
        List<DtoMappingInfo> infoList = createInfo(getWriteEndpoints());
        for (DtoMappingInfo info : infoList) {
            mc.getMappingEntries().put(info,writeDtoClass);
        }
        return this;
    }

    public DtoMappingContextBuilder forUpdate(Class<? extends IdentifiableEntity> updateDtoClass){
        List<DtoMappingInfo> infoList = createInfo(getUpdateEndpoints());
        for (DtoMappingInfo info : infoList) {
            mc.getMappingEntries().put(info,updateDtoClass);
        }
        return this;
    }

    public DtoMappingContextBuilder forEndpoint(Integer endpoint, Class<? extends IdentifiableEntity> dtoClass){
        mc.getMappingEntries().put(createInfo(endpoint,Direction.REQUEST),dtoClass);
        mc.getMappingEntries().put(createInfo(endpoint,Direction.RESPONSE),dtoClass);
        return this;
    }


    public DtoMappingContextBuilder forEndpoint(Integer endpoint, Direction direction, Class<? extends IdentifiableEntity> dtoClass){
        mc.getMappingEntries().put(createInfo(endpoint,direction),dtoClass);
        return this;
    }

    public DtoMappingContextBuilder forEndpointAndRoles(Integer endpoint, Direction direction, List<String> authorities, Class<? extends IdentifiableEntity> dtoClass){
        DtoMappingInfo info = createInfo(endpoint, direction);
        info.setAuthorities(authorities);
        mc.getMappingEntries().put(info,dtoClass);
        return this;
    }

    public DtoMappingContextBuilder forInfo(DtoMappingInfo info, Class<? extends IdentifiableEntity> dtoClass){
        mc.getMappingEntries().put(info,dtoClass);
        return this;
    }

    public DtoMappingContext build(){
        //create ignore Role Map
        mc.getMappingEntries().entrySet().forEach(e ->{
            DtoMappingInfo ignoreRoleInfo = new DtoMappingInfo(e.getKey());
            ignoreRoleInfo.getAuthorities().clear();
            mc.getMappingEntriesIgnoreRole().put(ignoreRoleInfo,e.getValue());
        });
        return mc;
    }


    /**
     * Create info for all given endpoints and for all directions
     * @param endpoints
     * @return
     */
    private List<DtoMappingInfo> createInfo(List<Integer> endpoints) {
        List<DtoMappingInfo> infoList = new ArrayList<>();
        for (Integer endpoint : endpoints) {
            infoList.add(createInfo(endpoint,Direction.REQUEST));
            infoList.add(createInfo(endpoint,Direction.RESPONSE));
        }
        return infoList;
    }

    private DtoMappingInfo createInfo(Integer endpoint, Direction direction){
        return DtoMappingInfo.builder()
                .authorities(Lists.newArrayList(defaultRole))
                .endpoint(endpoint)
                .direction(direction)
                .build();
    }
}
