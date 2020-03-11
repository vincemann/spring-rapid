package io.github.vincemann.generic.crud.lib.test.service.result.matcher.compare;

import io.github.vincemann.generic.crud.lib.model.IdentifiableEntity;
import io.github.vincemann.generic.crud.lib.test.service.result.matcher.compare.resolve.CompareEntityPlaceholder;
import org.junit.jupiter.api.Assertions;

import java.util.ArrayList;
import java.util.List;

public class CompareEntityMatcher {
    private IdentifiableEntity compareRoot;
    private CompareEntityPlaceholder rootCompareResolvable;
    private List<IdentifiableEntity> compareTos = new ArrayList<>();
    private List<CompareEntityPlaceholder> compareToResolvables = new ArrayList<>();

    public CompareEntityMatcher(IdentifiableEntity compareRoot) {
        this.compareRoot = compareRoot;
    }

    public CompareEntityMatcher(CompareEntityPlaceholder rootCompareResolvable) {
        this.rootCompareResolvable = rootCompareResolvable;
    }

    public CompareEntityMatcher with(IdentifiableEntity compareTo){
        compareTos.add(compareTo);
        return this;
    }

    public CompareEntityMatcher with(CompareEntityPlaceholder compareTo){
        compareToResolvables.add(compareTo);
        return this;
    }

    private void resolvePlaceholders(){
        if(rootCompareResolvable!=null){
            Assertions.assertNull(compareRoot,"cannot specify compare root entity and compare root entity placeholder at the same time");
            this.compareRoot =
        }
    }
}
