package zio4j;

import zio4j.std.Either;
import zio4j.std.Nothing;

import java.util.function.Supplier;

public class ZIO<R, E, A> {

  private final zio.ZIO<R, E, A> underlying;

  private ZIO(zio.ZIO<R, E, A> zio) {
    this.underlying = zio;
  }

  public zio.ZIO<R, E, A> underlying() {
    return this.underlying;
  }

  private static <R, E, A> ZIO<R, E, A> of(zio.ZIO<R, E, A> zio) {
    return new ZIO<>(zio);
  }

  public static <R, E, A> ZIO<R, E, A> fromZIO(zio.ZIO<R, E, A> zio) {
    return of(zio);
  }

  public static <A> ZIO<Object, Throwable, A> succeed(A value) {
    return of(zio.ZIO.apply(() -> value));
  }

  public static <A> ZIO<Object, Throwable, A> effect(Supplier<A> supplier) {
    return of(zio.ZIO.effect(supplier::get));
  }

  @SuppressWarnings("unchecked")
  public static <E, A> ZIO<Object, E, A> fail(E error) {
    return of((zio.ZIO<Object, E, A> )zio.ZIO.fail(() -> error));
  }

  public ZIO<Object, Nothing, Either<E, A>> either() {

    return null;
  }


}
