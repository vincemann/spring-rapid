package vincemann.github.generic.crud.lib.util;

import org.modelmapper.ModelMapper;

public class BeanUtils {

    public static <T> T createDeepCopy(T source, Class<T> clazz){
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setDeepCopyEnabled(true);
        return modelMapper.map(source,clazz);
    }
}
