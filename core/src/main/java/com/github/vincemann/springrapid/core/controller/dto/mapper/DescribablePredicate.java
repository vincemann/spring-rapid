package com.github.vincemann.springrapid.core.controller.dto.mapper;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public abstract class DescribablePredicate<T> implements Predicate<T> {
    private String description;

    public DescribablePredicate(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public DescribablePredicate<T> and(Predicate<? super T> other) {
        String newDescription = this.description;
        if (other instanceof DescribablePredicate) {
            newDescription += " AND " + ((DescribablePredicate<? super T>) other).getDescription();
        } else {
            newDescription += " AND " + other.toString();
        }
        return new DescribablePredicate<>(newDescription) {
            @Override
            public boolean test(T t) {
                return DescribablePredicate.this.test(t) && other.test(t);
            }
        };
    }

    public DescribablePredicate<T> or(Predicate<? super T> other) {
        String newDescription = this.description;
        if (other instanceof DescribablePredicate) {
            newDescription += " OR " + ((DescribablePredicate<? super T>) other).getDescription();
        } else {
            newDescription += " OR " + other.toString();
        }
        return new DescribablePredicate<>(newDescription) {
            @Override
            public boolean test(T t) {
                return DescribablePredicate.this.test(t) || other.test(t);
            }
        };
    }

    public DescribablePredicate<T> negate() {
        String newDescription = "NOT (" + this.description + ")";
        return new DescribablePredicate<>(newDescription) {
            @Override
            public boolean test(T t) {
                return !DescribablePredicate.this.test(t);
            }
        };
    }

}
