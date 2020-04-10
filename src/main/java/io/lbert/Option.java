package io.lbert;

import lombok.NonNull;

import java.util.function.Function;
import java.util.function.Supplier;

public interface Option<A> {

  <B> Option<B> map(Function<A, B> func);
  <B> Option<B> flatMap(Function<A, Option<B>> func);
  A getOrElse(Supplier<A> other);
  boolean isDefined();
  default boolean isEmpty() {
    return !isDefined();
  }

  @SuppressWarnings("unchecked")
  static <A> None<A> none() {
    return (None<A>) None.NONE;
  }

  @NonNull
  static <A> Some<A> some(A a) {
    return new Some<>(a);
  }

  @SuppressWarnings("unchecked")
  static <A> Option<A> of(A a) {
    return a == null ? (Option<A>) none() : some(a);
  }

  class Some<A> implements Option<A> {

    public final A value;

    private Some(A a) {
      this.value = a;
    }

    @Override
    public <B> Option<B> map(Function<A, B> func) {
      return new Some<>(func.apply(value));
    }

    @Override
    public <B> Option<B> flatMap(Function<A, Option<B>> func) {
      return func.apply(value);
    }

    @Override
    public A getOrElse(Supplier<A> other) {
      return value;
    }

    @Override
    public boolean isDefined() {
      return true;
    }

    @Override
    public String toString() {
      return "Some(" + value.toString() + ")";
    }

    @Override
    public boolean equals(Object o) {
      if(o == this) {
        return true;
      }

      if(!(o instanceof Some)) {
        return false;
      }

      var some = (Some) o;
      return some.value.equals(value);
    }
  }

  class None<A> implements Option<A> {

    private static final Option<?> NONE = new None<>();

    private None() {}

    @Override
    @SuppressWarnings("unchecked")
    public <B> Option<B> map(Function<A, B> func) {
      return (Option<B>) NONE;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <B> Option<B> flatMap(Function<A, Option<B>> func) {
      return (Option<B>) NONE;
    }

    @Override
    public A getOrElse(Supplier<A> other) {
      return other.get();
    }

    @Override
    public boolean isDefined() {
      return false;
    }

    @Override
    public String toString() {
      return "None";
    }

    @Override
    public boolean equals(Object o) {
      if(o == this) {
        return true;
      }

      return o instanceof None;
    }
  }
}