package zio4j;

import com.google.common.collect.ImmutableList;
import org.junit.Test;
import zio4j.std.Either;
import zio4j.std.Option;
import zio4j.std.tuple.Tuple2;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;

import static org.junit.Assert.*;

public class TaskTest {

  private final Function<Integer, Integer> incrementFunction = i -> i + 1;

  private final Function<Integer, Task<Integer>> incrementIOFunction = i -> Task.succeed(incrementFunction.apply(i));

  @Test
  public void succeed() {
    final Task<Integer> io = Task.succeed(10);
    final Integer out = ZIO4JRuntime.unsafeRun(io);
    assertEquals((Integer) 10, out);
  }

  @Test(expected = MyError.class)
  public void fail() {
    final Task<Integer> io = Task.fail(new MyError());
    ZIO4JRuntime.unsafeRun(io);
  }

  @Test
  public void effect() {
    final Task<Integer> io = Task.effect(() -> 10);
    final Integer out = ZIO4JRuntime.unsafeRun(io);
    assertEquals((Integer) 10, out);
  }

  @Test
  public void mapHappy() {
    final Task<Integer> io = Task.succeed(10);
    final Task<Integer> mapped = io.map(i -> i + 10);
    final Integer out = ZIO4JRuntime.unsafeRun(mapped);
    assertEquals((Integer) 20, out);
  }

  @Test(expected = MyError.class)
  public void mapSadShouldntCallMapFunction() {
    final Task<Integer> io = Task.fail(new MyError());

    AtomicInteger count = new AtomicInteger(0);
    final Task<Integer> mapped = io.map(i -> {
      count.incrementAndGet();
      return i + 10;
    });
    ZIO4JRuntime.unsafeRun(mapped);
    assertEquals(0, count.get());
  }

  @Test
  public void flatMapHappy() {
    final Task<Integer> io = Task.succeed(10);
    final Task<Integer> mapped = io.flatMap(i -> Task.succeed(i + 10));
    final Integer out = ZIO4JRuntime.unsafeRun(mapped);
    assertEquals((Integer) 20, out);
  }

  @Test(expected = MyOtherError.class)
  public void mapError() {
    final Task<Integer> io = Task.fail(new MyError());

    final Task<Integer> mapped = io.mapError(i ->
      new MyOtherError()
    );
    ZIO4JRuntime.unsafeRun(mapped);
  }

  @Test
  public void eitherInRightPath() {
    final Task<Integer> io = Task.succeed(10);
    final Task<Either<Throwable, Integer>> either = io.either();
    final Either<Throwable, Integer> out = ZIO4JRuntime.unsafeRun(either);
    assertEquals(out, Either.right(10));
  }

  @Test
  public void eitherInLeftPath() {
    final MyError myError = new MyError();
    final Task<Integer> io = Task.fail(myError);
    final Task<Either<Throwable, Integer>> either = io.either();
    final Either<Throwable, Integer> out = ZIO4JRuntime.unsafeRun(either);
    assertEquals(out, Either.left(myError));
  }

  @Test
  public void zip() {
    final Task<Integer> io1 = Task.succeed(10);
    final Task<Integer> io2 = Task.succeed(11);
    final Task<Tuple2<Integer, Integer>> zipped = io1.zip(io2);
    final Tuple2<Integer, Integer> out = ZIO4JRuntime.unsafeRun(zipped);
    assertEquals(Tuple2.of(10, 11), out);
  }

  @Test
  public void zipRight() {
    final Task<Integer> io1 = Task.succeed(10);
    final Task<Integer> io2 = Task.succeed(11);
    final Task<Integer> zippedRight = io1.zipRight(io2);
    assertEquals((Integer) 11, ZIO4JRuntime.unsafeRun(zippedRight));
  }

  @Test
  public void zipLeft() {
    final Task<Integer> io1 = Task.succeed(10);
    final Task<Integer> io2 = Task.succeed(11);
    final Task<Integer> zippedRight = io1.zipLeft(io2);
    assertEquals((Integer) 10, ZIO4JRuntime.unsafeRun(zippedRight));
  }

  @Test
  public void foreach() {
    final ImmutableList<Integer> list = ImmutableList.of(1, 2, 3, 4);
    final Task<ImmutableList<Integer>> eached = Task.foreach(list, incrementIOFunction);
    final ImmutableList<Integer> out = ZIO4JRuntime.unsafeRun(eached);
    assertEquals(ImmutableList.of(2,3,4,5), out);
  }

  @Test
  public void foreachPar() {
    final ImmutableList<Integer> list = ImmutableList.of(1, 2, 3, 4);
    final Task<ImmutableList<Integer>> eached = Task.foreachPar(list, incrementIOFunction);
    final ImmutableList<Integer> out = ZIO4JRuntime.unsafeRun(eached);
    assertEquals(ImmutableList.of(2,3,4,5), out);
  }

  @Test
  public void fromOptionSome() {
    final var some = Option.some(10);
    final var io = Task.fromOption(some, new MyError());
    assertEquals((Integer) 10, ZIO4JRuntime.unsafeRun(io));
  }

  @Test
  public void fromOptionNone() {
    final var myError = new MyError();
    final var none = Option.none();
    final var io = Task.fromOption(none, myError);
    assertEquals(Either.left(myError), ZIO4JRuntime.unsafeRun(io.either()));
  }

  @Test
  public void fromEitherRight() {
    final Either<Throwable, Integer> right = Either.right(10);
    final var io = Task.fromEither(right);
    assertEquals((Integer) 10, ZIO4JRuntime.unsafeRun(io));
  }

  @Test
  public void fromEitherLeft() {
    final var myError = new MyError();
    final Either<Throwable, Integer> left = Either.left(myError);
    final var io = Task.fromEither(left);
    assertEquals(Either.left(myError), ZIO4JRuntime.unsafeRun(io.either()));
  }

  @Test
  public void absolveRight() {
    final Task<Either<Throwable, Integer>> right = Task.succeed(Either.right(10));
    final var io = Task.absolve(right);
    assertEquals((Integer) 10, ZIO4JRuntime.unsafeRun(io));
  }

  @Test
  public void absolveLeft() {
    final var myError = new MyError();
    final Task<Either<Throwable, Integer>> left = Task.succeed(Either.left(myError));
    final var io = Task.absolve(left);
    assertEquals(Either.left(myError), ZIO4JRuntime.unsafeRun(io.either()));
  }

  @Test
  public void catchAll() {
    final var myError = new MyError();
    final var io = Task.fail(myError);
    final var fixed = io.catchAll(t -> Task.succeed(10));
    assertEquals((Integer) 10, ZIO4JRuntime.unsafeRun(fixed));
  }

  @Test
  public void catchAllWithNoErrorShouldntCallFunc() {
    final var io = Task.succeed(10);
    final var count = new AtomicInteger(0);
    final var fixed = io.catchAll(t -> {
      count.incrementAndGet();
      return Task.succeed(10);
    });
    assertEquals((Integer) 10, ZIO4JRuntime.unsafeRun(fixed));
    assertEquals(0, count.get());
  }

  @Test
  public void orElse() {
    final var myError = new MyError();
    final var io = Task.fail(myError);
    final var fixed = io.orElse(() -> Task.succeed(10));
    assertEquals((Integer) 10, ZIO4JRuntime.unsafeRun(fixed));
  }

  @Test
  public void orElseWithSuccess() {
    final var io = Task.succeed(10);
    final var fixed = io.orElse(() -> Task.succeed(100));
    assertEquals((Integer) 10, ZIO4JRuntime.unsafeRun(fixed));
  }

  @Test
  public void foldWithFailure() {
    final var myError = new MyError();
    final var io = Task.fail(myError);
    final var fixed = io.fold(t -> 100, a -> 200);
    assertEquals((Integer) 100, ZIO4JRuntime.unsafeRun(fixed));
  }

  @Test
  public void foldWithSuccess() {
    final var io = Task.succeed(10);
    final var fixed = io.fold(t -> 100, a -> 200);
    assertEquals((Integer) 200, ZIO4JRuntime.unsafeRun(fixed));
  }

  @Test
  public void foldMWithFailure() {
    final var myError = new MyError();
    final var io = Task.fail(myError);
    final var fixed = io.foldM(t -> Task.succeed(100), a -> Task.succeed(200));
    assertEquals((Integer) 100, ZIO4JRuntime.unsafeRun(fixed));
  }

  @Test
  public void foldMWithSuccess() {
    final var io = Task.succeed(10);
    final var fixed = io.foldM(t -> Task.succeed(100), a -> Task.succeed(200));
    assertEquals((Integer) 200, ZIO4JRuntime.unsafeRun(fixed));
  }

  public static class MyError extends Throwable {}
  public static class MyOtherError extends Throwable {}
}
