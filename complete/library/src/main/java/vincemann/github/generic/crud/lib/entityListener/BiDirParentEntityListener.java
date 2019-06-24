package vincemann.github.generic.crud.lib.entityListener;

import vincemann.github.generic.crud.lib.model.biDir.BiDirParent;

import javax.persistence.PreRemove;

public class BiDirParentEntityListener {

    @PreRemove
    public void onPreRemove(BiDirParent biDirParent) throws IllegalAccessException {
        biDirParent.dismissChildren();
    }
}
