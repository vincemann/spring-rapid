package io.github.vincemann.generic.crud.lib.test.equalChecker;

import de.danielbechler.diff.ObjectDiffer;
import de.danielbechler.diff.ObjectDifferBuilder;
import de.danielbechler.diff.node.DiffNode;
import de.danielbechler.diff.node.Visit;
import junit.framework.AssertionFailedError;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import javax.persistence.Entity;
import java.util.Collection;

@Slf4j
@Component
@Primary
public class IgnoreEntitiesReflectionComparator<T> implements ReflectionComparator<T> {
    private static final String ENTITY_GROUP = "entities";

    private ObjectDiffer objectDiffer;

    public IgnoreEntitiesReflectionComparator() {
//        this.objectDiffer = ObjectDifferBuilder.startBuilding()
//                .filtering()
//                .omitNodesWithState(DiffNode.State.IGNORED)
//                .omitNodesWithState(DiffNode.State.CIRCULAR)
//                .and()
//                .build();
        this.objectDiffer = ObjectDifferBuilder.startBuilding()
                .categories()
                .ofType(Entity.class)
                .toBe(ENTITY_GROUP)
                .ofType((Class<Collection<Entity>>)(Class<?>)Collection.class)
                .toBe(ENTITY_GROUP)
                .and()
                .inclusion()
                .exclude()
                .category(ENTITY_GROUP)
                .and()
                .build();

    }

    @Override
    public boolean isEqual(T expected, T actual) {
        try {
            //order in Lists is ignored
            DiffNode diff = objectDiffer.compare(expected, actual);
            log(diff);
//            ReflectionComparator reflectionComparator = ReflectionComparatorFactory.createRefectionComparator(ReflectionComparatorMode.LENIENT_ORDER);
//            Difference difference = reflectionComparator.getDifference(expected, actual);
//            ReflectionAssert.assertReflectionEquals(expected, actual, ReflectionComparatorMode.LENIENT_ORDER);
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
