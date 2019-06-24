package vincemann.github.generic.crud.lib.dtoMapper;

import vincemann.github.generic.crud.lib.controller.exception.EntityMappingException;
import org.modelmapper.ModelMapper;
import vincemann.github.generic.crud.lib.model.IdentifiableEntity;

import java.io.Serializable;

public class BasicDTOMapper<Src extends IdentifiableEntity<Id>,Dest extends IdentifiableEntity<Id>,Id extends Serializable> implements DTOMapper<Src,Dest,Id> {

    private Class<Dest> destClass;
    private ModelMapper modelMapper;

    public BasicDTOMapper(Class<Dest> destClass, ModelMapper modelMapper) {
        this.destClass = destClass;
        this.modelMapper = modelMapper;
    }

    public BasicDTOMapper(Class<Dest> destClass) {
        this.destClass = destClass;
        this.modelMapper= new ModelMapper();
    }

    @Override
    public Dest map(Src source) throws EntityMappingException {
        try {
            return modelMapper.map(source,destClass);
        }catch (RuntimeException e){
            throw new EntityMappingException(e);
        }
    }

    public void setModelMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    public Class<Dest> getDestClass() {
        return destClass;
    }

    public ModelMapper getModelMapper() {
        return modelMapper;
    }
}
