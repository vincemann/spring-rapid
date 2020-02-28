package io.github.vincemann.generic.crud.lib.test.deepCompare;

import de.danielbechler.diff.ObjectDiffer;
import de.danielbechler.diff.ObjectDifferBuilder;
import de.danielbechler.diff.node.DiffNode;
import de.danielbechler.diff.node.Visit;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.opentest4j.AssertionFailedError;

import javax.persistence.Entity;

@Slf4j
@Getter
@Setter
public class EntityReflectionComparator implements ReflectionComparator<Object> {

    private ObjectDiffer objectDiffer;
    private ObjectDifferBuilder builder;


    public EntityReflectionComparator(ObjectDifferBuilder builder) {
        this.objectDiffer = builder.build();
        this.builder=builder;
    }

    public static ObjectDifferBuilder EQUALS_FOR_ENTITIES(){
        return ObjectDifferBuilder.startBuilding()
                .comparison()
                .ofType(Entity.class)
                .toUseEqualsMethod()
                .and();

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
