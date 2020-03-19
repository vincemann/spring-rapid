package io.github.vincemann.generic.crud.lib.test.compare;

import de.danielbechler.diff.ObjectDiffer;
import de.danielbechler.diff.ObjectDifferBuilder;
import de.danielbechler.diff.node.DiffNode;
import de.danielbechler.diff.node.Visit;
import de.danielbechler.diff.path.NodePath;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.opentest4j.AssertionFailedError;

import javax.persistence.Entity;

@Getter
@Setter
@Slf4j
public abstract class AbstractEntityComparator {

    private ObjectDiffer objectDiffer;


    public AbstractEntityComparator() {
        this.objectDiffer = createDefaultBuilder().build();
    }

    public ObjectDifferBuilder createDefaultBuilder(){
        return ObjectDifferBuilder.startBuilding()
                .comparison()
                .ofType(Entity.class)
                .toUseEqualsMethod()
                .and();
    }

    public boolean isEqual(Object expected, Object actual) {
        try {
            //order in Lists is ignored
            DiffNode diff = objectDiffer.compare(expected, actual);
            if(diff.isRootNode()){
                return !diff.hasChildren();
            }
            log(diff);
            return !diff.hasChanges();
        } catch (AssertionFailedError e) {
            log.debug("Objects are not considered equal by EqualChecker: " + e.getMessage());
            return false;
        }
    }

    private void log(DiffNode diff){
        diff.visit(new DiffNode.Visitor()
        {
            public void node(DiffNode node, Visit visit)
            {
                System.out.println(node.getPath() + " => " + node.getState());
            }
        });
    }
}
