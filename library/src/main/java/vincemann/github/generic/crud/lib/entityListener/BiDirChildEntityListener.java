package vincemann.github.generic.crud.lib.entityListener;

import vincemann.github.generic.crud.lib.model.biDir.BiDirChild;

import javax.persistence.PreRemove;

public class BiDirChildEntityListener {

    @PreRemove
    public void onPreRemove(BiDirChild biDirChild) throws IllegalAccessException {
        biDirChild.dismissParents();
    }
}
