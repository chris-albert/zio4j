package zio4j.std;

import lombok.NonNull;
import lombok.Value;

import java.util.function.Function;

public interface Either<L, R> {

  <LL> Either<LL, R> lMap(Function<L, LL> func);

  <LL> Either<LL, R> lFlatMap(Function<L, Either<LL, R>> func);

  <RR> Either<L, RR> rMap(Function<R, RR> func);

  <RR> Either<L, RR> rFlatMap(Function<R, Either<L, RR>> func);

  <A> A fold(Function<L, A> leftMap, Function<R, A> rightMap);

  boolean isLeft();

  default boolean isRight() {
    return !isLeft();
  }

  Option<L> leftOption();

  Option<R> rightOption();

  @NonNull
  static <L, R> Either<L, R> right(R r) {
    return Right.of(r);
  }

  @NonNull
  static <L, R> Either<L, R> left(L l) {
    return Left.of(l);
  }

  @Value(staticConstructor = "of")
  class Left<L, R> implements Either<L, R> {

    L left;

    @Override
    public <LL> Either<LL, R> lMap(Function<L, LL> func) {
      return new Left<>(func.apply(left));
    }

    @Override
    public <LL> Either<LL, R> lFlatMap(Function<L, Either<LL, R>> func) {
      return func.apply(left);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <RR> Either<L, RR> rMap(Function<R, RR> func) {
      return (Either<L, RR>) this;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <RR> Either<L, RR> rFlatMap(Function<R, Either<L, RR>> func) {
      return (Either<L, RR>) this;
    }

    @Override
    public <A> A fold(Function<L, A> leftMap, Function<R, A> rightMap) {
      return leftMap.apply(left);
    }

    @Override
    public boolean isLeft() {
      return true;
    }

    @Override
    public Option<L> leftOption() {
      return Option.some(left);
    }

    @Override
    public Option<R> rightOption() {
      return Option.none();
    }
  }

  @Value(staticConstructor = "of")
  class Right<L, R> implements Either<L, R> {

    R right;

    @Override
    @SuppressWarnings("unchecked")
    public <LL> Either<LL, R> lMap(Function<L, LL> func) {
      return (Either<LL, R>) this;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <LL> Either<LL, R> lFlatMap(Function<L, Either<LL, R>> func) {
      return (Either<LL, R>) this;
    }

    @Override
    public <RR> Either<L, RR> rMap(Function<R, RR> func) {
      return new Right<>(func.apply(right));
    }

    @Override
    public <RR> Either<L, RR> rFlatMap(Function<R, Either<L, RR>> func) {
      return func.apply(right);
    }

    @Override
    public <A> A fold(Function<L, A> leftMap, Function<R, A> rightMap) {
      return rightMap.apply(right);
    }

    @Override
    public boolean isLeft() {
      return false;
    }

    @Override
    public Option<L> leftOption() {
      return Option.none();
    }

    @Override
    public Option<R> rightOption() {
      return Option.some(right);
    }
  }
}