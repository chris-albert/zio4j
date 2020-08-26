package zio4j;

import zio.CanFail;
import zio4j.std.Either;
import zio4j.std.Nothing;

import java.util.function.Function;
import java.util.function.Supplier;

public class ZIO<R, E, A> {

  private final CanFail<E> canFail = CanFail.canFail();

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

//  public ZIO<R, Nothing, Either<E, A>> either() {
//    var a = (ZIO<R, Nothing, Either<E, A>>) of(underlying().either(CanFail.canFail())
//        .map(Either::fromScala));
//    return null;
//  }

  public <B> ZIO<R, E, B> map(Function<A, B> func) {
    return of(underlying.map(func::apply));
  }

  public <B> ZIO<R, E, B> flatMap(Function<A, ZIO<R, E, B>> func) {
    return of(underlying.flatMap(a -> func.apply(a).underlying));
  }

  public <EE> ZIO<R, EE, A> mapError(Function<E, EE> func) {
    return of(underlying.mapError(func::apply, canFail));
  }
}
