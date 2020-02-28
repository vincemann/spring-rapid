package io.github.vincemann.generic.crud.lib.test.deepCompare;

import de.danielbechler.diff.ObjectDiffer;
import de.danielbechler.diff.ObjectDifferBuilder;
import de.danielbechler.diff.node.DiffNode;
import de.danielbechler.diff.node.Visit;
import lombok.extern.slf4j.Slf4j;
import org.opentest4j.AssertionFailedError;

import javax.persistence.Entity;

@Slf4j
public class EntityReflectionComparator implements ReflectionComparator<Object> {

    private ObjectDiffer objectDiffer;

    public EntityReflectionComparator(ObjectDiffer objectDiffer) {
        this.objectDiffer = objectDiffer;
    }

    public static EntityReflectionComparator EQUALS_FOR_ENTITIES(){
        ObjectDiffer objectDiffer = ObjectDifferBuilder.startBuilding()
                .comparison()
                .ofType(Entity.class)
                .toUseEqualsMethod()
                .and()
                .build();
        return new EntityReflectionComparator(objectDiffer);
    }

    public static EntityReflectionComparator IGNORE_ID(){
        ObjectDiffer objectDiffer = ObjectDifferBuilder.startBuilding()
                .inclusion()
                .exclude()
                .propertyName("id")
                .and()
                .build();
        return new EntityReflectionComparator(objectDiffer);
    }

    @Override
    public boolean isEqual(Object expected, Object actual) {
        try {
            //order in Lists is ignored
            DiffNode diff = objectDiffer.compare(expected, actual);
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
