package vincemann.github.generic.crud.lib.model.biDir;

import vincemann.github.generic.crud.lib.service.exception.UnknownChildTypeException;
import vincemann.github.generic.crud.lib.service.exception.UnknownParentTypeException;
import vincemann.github.generic.crud.lib.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

public interface BiDirChild extends BiDirEntity {
    Map<Class,Field[]> biDirParentFieldsCache = new HashMap<>();

    public default void findAndSetParent(BiDirParent parentToSet) throws UnknownParentTypeException, IllegalAccessException {
        AtomicBoolean atLeastOneParentSet = new AtomicBoolean(false);
        for(Field parentField: findParentFields()){
            if(parentToSet.getClass().equals(parentField.getType())){
                parentField.setAccessible(true);
                parentField.set(this,parentToSet);
                atLeastOneParentSet.set(true);
            }
        }
        if(!atLeastOneParentSet.get()){
            throw new UnknownParentTypeException(this.getClass(),parentToSet.getClass());
        }
    }

    public default void addChildToParents() throws IllegalAccessException {
        Collection<BiDirParent> parents = findParents();
        for(BiDirParent parent: parents){
            if(parent!=null) {
                parent.addChild(this);
            }else {
                System.err.println("found null parent of biDirChild with type: " +getClass().getSimpleName());
            }
        }
    }

    public default boolean findAndSetParentIfNull(BiDirParent parentToSet) throws IllegalAccessException {
        AtomicBoolean atLeastOneParentSet = new AtomicBoolean(false);
        for(Field parentField: findParentFields()){
            if(parentToSet.getClass().equals(parentField.getType())){
                parentField.setAccessible(true);
                if(parentField.get(this)==null) {
                    parentField.set(this, parentToSet);
                }
            }
        }
        return atLeastOneParentSet.get();
    }
    
    public default Field[] findParentFields(){
        Field[] parentFieldsFromCache = biDirParentFieldsCache.get(this.getClass());
        if(parentFieldsFromCache==null){
            Field[] parentFields = ReflectionUtils.getDeclaredFieldsAnnotatedWith(getClass(), BiDirParentEntity.class, true);
            biDirParentFieldsCache.put(this.getClass(),parentFields);
            return parentFields;
        }else {
            return parentFieldsFromCache;
        }
    }


    public default Collection<BiDirParent> findParents() throws IllegalAccessException {
        Collection<BiDirParent> result = new ArrayList<>();
        Field[] parentFields = findParentFields();
        for(Field parentField: parentFields) {
            parentField.setAccessible(true);
            BiDirParent biDirParent = (BiDirParent) parentField.get(this);
            result.add(biDirParent);
        }
        return result;
    }

    public default void dismissParents() throws UnknownChildTypeException, UnknownParentTypeException, IllegalAccessException {
        for(BiDirParent parent: findParents()){
            if(parent!=null) {
                parent.dismissChild(this);
                this.dismissParent(parent);
            }else {
                System.err.println("parent Reference of BiDirChild with type: " + this.getClass().getSimpleName() +" was not set when deleting -> parent was deleted before child");
            }
        }
    }

    public default void dismissParent(BiDirParent parentToDelete) throws UnknownParentTypeException, IllegalAccessException {
        AtomicBoolean atLeastOneParentRemoved = new AtomicBoolean(false);
        Field[] parentFields = findParentFields();
        for(Field parentField: parentFields){
            parentField.setAccessible(true);
            BiDirParent  parent = (BiDirParent) parentField.get(this);
            if(parent!=null) {
                if (parentToDelete.getClass().equals(parent.getClass())) {
                    parentField.set(this,null);
                    atLeastOneParentRemoved.set(true);
                }
            }else {
                System.err.println("parent reference was null when wanting to dismiss");
            }
        }

        if(!atLeastOneParentRemoved.get()){
            throw new UnknownParentTypeException(this.getClass(),parentToDelete.getClass());
        }
    }
}
