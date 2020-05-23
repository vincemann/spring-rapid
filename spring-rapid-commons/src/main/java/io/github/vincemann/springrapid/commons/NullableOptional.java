package io.github.vincemann.springrapid.commons;

import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Stream;

/**
 * Java's {@link java.util.Optional}, but null is an accepted value.
 */
public class NullableOptional<T> {
    private boolean empty;
    private final T value;

    private NullableOptional() {
        this.empty=true;
        this.value = null;
    }

    private NullableOptional(T value) {
        this.empty=false;
        this.value = value;
    }

    public static <T> NullableOptional<T> empty() {
        NullableOptional<T> t = new NullableOptional<>();
        return t;
    }

    public static <T> NullableOptional<T> of(T value) {
        return new NullableOptional(value);
    }

    public static <T> NullableOptional<T> ofNullable(T value) {
        return value == null ? empty() : of(value);
    }

    public T get() {
        return this.value;
    }

    public boolean isPresent() {
        return !empty;
    }

    public boolean isEmpty() {
        return this.value == null;
    }

    public void ifPresent(Consumer<? super T> action) {
        if (this.value != null) {
            action.accept(this.value);
        }

    }

    public void ifPresentOrElse(Consumer<? super T> action, Runnable emptyAction) {
        if (this.value != null) {
            action.accept(this.value);
        } else {
            emptyAction.run();
        }

    }

    public NullableOptional<T> filter(Predicate<? super T> predicate) {
        if (!this.isPresent()) {
            return this;
        } else {
            return predicate.test(this.value) ? this : empty();
        }
    }

    public <U> NullableOptional<U> map(Function<? super T, ? extends U> mapper) {
        Objects.requireNonNull(mapper);
        return !this.isPresent() ? empty() : ofNullable(mapper.apply(this.value));
    }

    public <U> NullableOptional<U> flatMap(Function<? super T, ? extends NullableOptional<? extends U>> mapper) {
        Objects.requireNonNull(mapper);
        if (!this.isPresent()) {
            return empty();
        } else {
            NullableOptional<U> r = (NullableOptional) mapper.apply(this.value);
            return r;
        }
    }

    public NullableOptional<T> or(Supplier<? extends NullableOptional<? extends T>> supplier) {
        Objects.requireNonNull(supplier);
        if (this.isPresent()) {
            return this;
        } else {
            NullableOptional<T> r = (NullableOptional) supplier.get();
            return r;
        }
    }

    public Stream<T> stream() {
        return !this.isPresent() ? Stream.empty() : Stream.of(this.value);
    }

    public T orElse(T other) {
        return this.value != null ? this.value : other;
    }

    public T orElseGet(Supplier<? extends T> supplier) {
        return this.value != null ? this.value : supplier.get();
    }

    public T orElseThrow() {
        if (this.value == null) {
            throw new NoSuchElementException("No value present");
        } else {
            return this.value;
        }
    }

    public <X extends Throwable> T orElseThrow(Supplier<? extends X> exceptionSupplier) throws Throwable {
        if (this.value != null) {
            return this.value;
        } else {
            throw (Throwable) exceptionSupplier.get();
        }
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        } else if (!(obj instanceof NullableOptional)) {
            return false;
        } else {
            NullableOptional<?> other = (NullableOptional) obj;
            return Objects.equals(this.value, other.value);
        }
    }

    public int hashCode() {
        return Objects.hashCode(this.value);
    }

    public String toString() {
        return this.empty ? String.format("NullableOptional[%s]", this.value) : "NullableOptional.empty";
    }
}
