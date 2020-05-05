package io.github.vincemann.springrapid.core.controller.dtoMapper.context;

/**
 * Represents an Endpoint exposed by {@link io.github.vincemann.springrapid.core.controller.DtoCrudController} that
 * has a Dto either in the request or Response.
 *
 */
public class CrudDtoEndpoint {
    private CrudDtoEndpoint(){}

    public static final String CREATE = "create";
//    public static final String FULL_UPDATE = "fullUpdate";
//    public static final String PARTIAL_UPDATE = "partialUpdate";
    public static final String UPDATE = "update";
    public static final String FIND = "find";
    public static final String FIND_ALL = "findAll";
}
