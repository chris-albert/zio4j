package io.lbert;

import com.google.common.collect.ImmutableList;
import scala.jdk.CollectionConverters;
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

  public static <A> IO<A> succeed(A value) {
    return of(ZIO.apply(() -> value));
  }

  public static <A> IO<A> effect(Supplier<A> supplier) {
    return of(ZIO.apply(supplier::get));
  }

  @SuppressWarnings("unchecked")
  public static <A> IO<A> fail(Throwable throwable) {
    return of((ZIO<Object, Throwable, A>) ZIO.fail(() -> throwable));
  }

  @SuppressWarnings("unchecked")
  public IO<Either<Throwable, A>> either() {
    return of(zio.either(CanFail.canFail())
      .map(e -> (Either<Throwable, A>) e.fold(Either::left, Either::right))
      .mapError((o) -> new Throwable(), CanFail.canFail()));
  }

  public <B> IO<B> map(Function<A, B> func) {
    return of(zio.map(func::apply));
  }

  public <B> IO<B> flatMap(Function<A, IO<B>> func) {
    return of(zio.flatMap(a -> func.apply(a).zio));
  }

  public IO<A> mapError(Function<Throwable, Throwable> func) {
    return of(zio.mapError(func::apply, CanFail.canFail()));
  }

  public <B> IO<Tuple<A, B>> zip(IO<B> ioB) {
    return of(
      zio.zip(ioB.zio)
        .map(t -> Tuple.of(t._1, t._2))
    );
  }

  public <B> IO<B> zipRight(IO<B> ioB) {
    return zip(ioB).map(Tuple::get_2);
  }

  public <B> IO<A> zipLeft(IO<B> ioB) {
    return zip(ioB).map(Tuple::get_1);
  }

  public static <A, B> IO<ImmutableList<B>> foreach(Iterable<A> it, Function<A, IO<B>> func) {
    return of(ZIO
      .foreach(CollectionConverters.IterableHasAsScala(it).asScala(), a -> func.apply(a).zio)
      .map(sl -> ImmutableList.copyOf(CollectionConverters.IterableHasAsJava(sl).asJava()))
    );
  }

  public static <A, B> IO<ImmutableList<B>> foreachPar(Iterable<A> it, Function<A, IO<B>> func) {
    return of(ZIO
      .foreachPar(CollectionConverters.IterableHasAsScala(it).asScala(), a -> func.apply(a).zio)
      .map(sl -> ImmutableList.copyOf(CollectionConverters.IterableHasAsJava(sl).asJava()))
    );
  }

  public static <A> IO<A> fromOption(Option<A> opt, Throwable ifNone) {
    return opt.fold(IO::succeed, () -> IO.fail(ifNone));
  }

  public static <A> IO<A> fromEither(Either<Throwable, A> either) {
    return either.fold(IO::fail, IO::succeed);
  }

  public static <A> IO<A> absolve(IO<Either<Throwable, A>> eitherIO) {
    return eitherIO.flatMap(either ->
      either.fold(IO::fail, IO::succeed)
    );
  }
}
