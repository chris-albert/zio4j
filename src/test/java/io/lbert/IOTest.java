package io.lbert;

import org.junit.Test;

import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.Assert.*;

public class IOTest {

  @Test
  public void succeed() {
    final IO<Integer> io = IO.succeed(() -> 10);
    final Integer out = IORuntime.unsafeRun(io);
    assertEquals((Integer) 10, out);
  }

  @Test(expected = MyError.class)
  public void fail() {
    final IO<Integer> io = IO.fail(new MyError());
    IORuntime.unsafeRun(io);
  }

  @Test
  public void mapHappy() {
    final IO<Integer> io = IO.succeed(() -> 10);
    final IO<Integer> mapped = io.map(i -> i + 10);
    final Integer out = IORuntime.unsafeRun(mapped);
    assertEquals((Integer) 20, out);
  }

  @Test(expected = MyError.class)
  public void mapSadShouldntCallMapFunction() {
    final IO<Integer> io = IO.fail(new MyError());

    AtomicInteger count = new AtomicInteger(0);
    final IO<Integer> mapped = io.map(i -> {
      count.incrementAndGet();
      return i + 10;
    });
    IORuntime.unsafeRun(mapped);
    assertEquals(0, count.get());
  }

  @Test
  public void flatMapHappy() {
    final IO<Integer> io = IO.succeed(() -> 10);
    final IO<Integer> mapped = io.flatMap(i -> IO.succeed(() -> i + 10));
    final Integer out = IORuntime.unsafeRun(mapped);
    assertEquals((Integer) 20, out);
  }

  @Test(expected = MyOtherError.class)
  public void mapError() {
    final IO<Integer> io = IO.fail(new MyError());

    final IO<Integer> mapped = io.mapError(i ->
      new MyOtherError()
    );
    IORuntime.unsafeRun(mapped);
  }

  @Test
  public void eitherInRightPath() {
    final IO<Integer> io = IO.succeed(() -> 10);
    final IO<Either<Throwable, Integer>> either = io.either();
    final Either<Throwable, Integer> out = IORuntime.unsafeRun(either);
    assertEquals(out, Either.right(10));
  }

  @Test
  public void eitherInLeftPath() {
    final MyError myError = new MyError();
    final IO<Integer> io = IO.fail(myError);
    final IO<Either<Throwable, Integer>> either = io.either();
    final Either<Throwable, Integer> out = IORuntime.unsafeRun(either);
    assertEquals(out, Either.left(myError));
  }

  public static class MyError extends Throwable {}
  public static class MyOtherError extends Throwable {}
}
