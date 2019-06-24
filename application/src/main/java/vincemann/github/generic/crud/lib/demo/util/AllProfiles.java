package vincemann.github.generic.crud.lib.demo.util;

import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.context.annotation.Conditional;
import org.springframework.core.type.AnnotatedTypeMetadata;
import org.springframework.util.Assert;
import org.springframework.util.MultiValueMap;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
@Conditional(AllProfiles.AllProfilesCondition.class)
public @interface AllProfiles {
    String[] value();

    static class AllProfilesCondition implements Condition {
        @Override
        public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
            if (context.getEnvironment() != null) {
                MultiValueMap<String, Object> attrs = metadata.getAllAnnotationAttributes(AllProfiles.class.getName());
                if (attrs != null) {
                    // check that all match
                    for (Object value : attrs.get("value")) {
                        // check that 1 set of profiles matches
                        String[] profiles = (String[]) value;
                        Assert.notEmpty(profiles, "Must specify at least one profile");
                        for (String profile : profiles) {
                            if (!context.getEnvironment()
                                    .acceptsProfiles(profile)) {
                                return false;
                            }
                        }
                        // this set of profiles matches but there could be more
                    }
                    return true;
                }
            }
            // environment or attrs was null, no match
            return false;
        }
    }
}
