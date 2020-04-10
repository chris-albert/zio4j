package io.lbert;

import zio.CanFail;
import zio.ZIO;

import java.util.function.Function;
import java.util.function.Supplier;

public class IO<A> {

  private final ZIO<Object, Throwable, A> zio;

  private IO(ZIO<Object, Throwable, A> zio) {
    this.zio = zio;
  }

  public ZIO<Object, Throwable, A> getZIO() {
    return zio;
  }

  private static <A> IO<A> of(ZIO<Object, Throwable, A> zio) {
    return new IO<>(zio);
  }

  public static <A> IO<A> succeed(Supplier<A> supplier) {
    return of(ZIO.apply(supplier::get));
  }

  @SuppressWarnings("unchecked")
  public static <A> IO<A> fail(Throwable throwable) {
    return of((ZIO<Object, Throwable, A>) ZIO.fail(() -> throwable));
  }

  public IO<Either<Throwable, A>> either() {
    final ZIO<Object, Throwable, Either<Throwable, A>> a =
      zio.either(CanFail.canFail())
      .map(e -> {
        Either<Throwable, A> b = e.fold(Either::left, Either::right);
        return b;
      })
      .mapError((o) -> new Throwable(), CanFail.canFail());
    return of(a);
  }

  public <B> IO<B> map(Function<A, B> func) {
    return of(zio.map(func::apply));
  }

  public IO<A> mapError(Function<Throwable, Throwable> func) {
    return of(zio.mapError(func::apply, CanFail.canFail()));
  }
}
