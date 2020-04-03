package io.github.vincemann.generic.crud.lib.controller.dtoMapper.context;

import com.google.common.collect.Lists;
import io.github.vincemann.generic.crud.lib.model.IdentifiableEntity;

import java.util.ArrayList;
import java.util.List;

public class DtoMappingContextBuilder {
    private List<String> currentRoles = new ArrayList<>();
    private DtoMappingContext mc;

    public DtoMappingContextBuilder() {
        this.mc = new DtoMappingContext();
        this.mc.setIgnoreRole(true);
    }

    public static DtoMappingContextBuilder builder(){
        return new DtoMappingContextBuilder();
    }


    public DtoMappingContextBuilder withRoles(String... roles){
        this.currentRoles =Lists.newArrayList(roles);
        this.mc.setIgnoreRole(false);
        return this;
    }

    public DtoMappingContextBuilder withoutRole(){
        this.currentRoles = new ArrayList<>();
        return this;
    }




    protected List<String> getAllEndpoints(){
        return Lists.newArrayList(CrudDtoEndpoint.CREATE, CrudDtoEndpoint.FIND, CrudDtoEndpoint.FIND_ALL, CrudDtoEndpoint.FULL_UPDATE, CrudDtoEndpoint.PARTIAL_UPDATE);
    }

    protected List<String> getFindEndpoints(){
        return Lists.newArrayList(CrudDtoEndpoint.FIND, CrudDtoEndpoint.FIND_ALL);
    }

    protected List<String> getWriteEndpoints(){
        return Lists.newArrayList(CrudDtoEndpoint.CREATE, CrudDtoEndpoint.FULL_UPDATE, CrudDtoEndpoint.PARTIAL_UPDATE);
    }

    protected List<String> getUpdateEndpoints(){
        return Lists.newArrayList(CrudDtoEndpoint.FULL_UPDATE, CrudDtoEndpoint.PARTIAL_UPDATE);
    }

    /**
     * Uses one dto class for all crud operations
     * @param defaultDtoClass
     * @return
     */
    public DtoMappingContextBuilder forAll(Class<? extends IdentifiableEntity> defaultDtoClass){
        List<DtoMappingInfo> infoList = createInfos(getAllEndpoints());
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
        List<String> allEndpoints = getAllEndpoints();
        List<DtoMappingInfo> infoList = new ArrayList<>();
        for (String endpoint : allEndpoints) {
            infoList.add(createInfo(endpoint,direction));
        }
        for (DtoMappingInfo info : infoList) {
            mc.getMappingEntries().put(info,responseDtoClass);
        }
        return this;
    }

    public DtoMappingContextBuilder forFind(Class<? extends IdentifiableEntity> readDtoClass){
        List<DtoMappingInfo> infoList = createInfos(getFindEndpoints());
        for (DtoMappingInfo info : infoList) {
            mc.getMappingEntries().put(info,readDtoClass);
        }
        return this;
    }

    public DtoMappingContextBuilder forWrite(Class<? extends IdentifiableEntity> writeDtoClass){
        List<DtoMappingInfo> infoList = createInfos(getWriteEndpoints());
        for (DtoMappingInfo info : infoList) {
            mc.getMappingEntries().put(info,writeDtoClass);
        }
        return this;
    }

    public DtoMappingContextBuilder forUpdate(Direction direction,Class<? extends IdentifiableEntity> updateDtoClass){
        List<String> updateEndpoints = getUpdateEndpoints();
        for (String updateEndpoint : updateEndpoints) {
            mc.getMappingEntries().put(createInfo(updateEndpoint,direction),updateDtoClass);
        }
        return this;
    }

    public DtoMappingContextBuilder forUpdate(Class<? extends IdentifiableEntity> updateDtoClass){
        List<String> updateEndpoints = getUpdateEndpoints();
        for (String updateEndpoint : updateEndpoints) {
            mc.getMappingEntries().put(createInfo(updateEndpoint,Direction.REQUEST),updateDtoClass);
            mc.getMappingEntries().put(createInfo(updateEndpoint,Direction.RESPONSE),updateDtoClass);
        }
        return this;
    }

    public DtoMappingContextBuilder forEndpoint(String endpoint, Class<? extends IdentifiableEntity> dtoClass){
        mc.getMappingEntries().put(createInfo(endpoint,Direction.REQUEST),dtoClass);
        mc.getMappingEntries().put(createInfo(endpoint,Direction.RESPONSE),dtoClass);
        return this;
    }


    public DtoMappingContextBuilder forEndpoint(String endpoint, Direction direction, Class<? extends IdentifiableEntity> dtoClass){
        mc.getMappingEntries().put(createInfo(endpoint,direction),dtoClass);
        return this;
    }

    public DtoMappingContextBuilder forEndpointAndRoles(String endpoint, Direction direction, List<String> authorities, Class<? extends IdentifiableEntity> dtoClass){
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
    private List<DtoMappingInfo> createInfos(List<String> endpoints) {
        List<DtoMappingInfo> infoList = new ArrayList<>();
        for (String endpoint : endpoints) {
            infoList.add(createInfo(endpoint,Direction.REQUEST));
            infoList.add(createInfo(endpoint,Direction.RESPONSE));
        }
        return infoList;
    }

    private DtoMappingInfo createInfo(String endpoint, Direction direction){
        return DtoMappingInfo.builder()
                .authorities(currentRoles)
                .endpoint(endpoint)
                .direction(direction)
                .build();
    }
}
