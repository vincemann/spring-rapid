package vincemann.github.generic.crud.lib.controller.springAdapter.mediaTypeStrategy;

public interface MediaTypeStrategy{
    <DTO> DTO readDTOFromBody(String body,Class<DTO> dtoClass) throws DTOReadingException;
    String getMediaType();
    boolean isBodyOfGivenType(String body, Class type);
}
