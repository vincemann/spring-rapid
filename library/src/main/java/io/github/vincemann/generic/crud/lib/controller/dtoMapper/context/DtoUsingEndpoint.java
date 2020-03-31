package io.github.vincemann.generic.crud.lib.controller.dtoMapper.context;

public class DtoUsingEndpoint {
    private DtoUsingEndpoint(){}

    public static final int CREATE = 0;
    public static final int FULL_UPDATE = 1;
    public static final int PARTIAL_UPDATE = 2;
    public static final int FIND = 3;
    public static final int FIND_ALL = 4;
}
