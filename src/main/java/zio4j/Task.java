package zio4j;

import com.google.common.collect.ImmutableList;
import scala.jdk.CollectionConverters;
import zio.CanFail;
import zio.ZIO;
import zio4j.std.Either;
import zio4j.std.Option;
import zio4j.std.tuple.Tuple2;

import java.util.function.Function;
import java.util.function.Supplier;

public class Task<A> {

  private final ZIO<Object, Throwable, A> zio;

  private Task(ZIO<Object, Throwable, A> zio) {
    this.zio = zio;
  }

  public ZIO<Object, Throwable, A> getZIO() {
    return zio;
  }

  public static <A> Task<A> fromZIO(ZIO<Object, Throwable, A> zio) {
    return of(zio);
  }

  private static <A> Task<A> of(ZIO<Object, Throwable, A> zio) {
    return new Task<>(zio);
  }

  public static <A> Task<A> succeed(A value) {
    return of(ZIO.apply(() -> value));
  }

  public static <A> Task<A> effect(Supplier<A> supplier) {
    return of(ZIO.effect(supplier::get));
  }

  @SuppressWarnings("unchecked")
  public static <A> Task<A> fail(Throwable throwable) {
    return of((ZIO<Object, Throwable, A>) ZIO.fail(() -> throwable));
  }

  @SuppressWarnings("unchecked")
  public Task<Either<Throwable, A>> either() {
    return of(zio.either(CanFail.canFail())
        .map(e -> (Either<Throwable, A>) e.fold(Either::left, Either::right))
        .mapError((o) -> new Throwable(), CanFail.canFail()));
  }

  public <B> Task<B> map(Function<A, B> func) {
    return of(zio.map(func::apply));
  }

  public <B> Task<B> flatMap(Function<A, Task<B>> func) {
    return of(zio.flatMap(a -> func.apply(a).zio));
  }

  public Task<A> mapError(Function<Throwable, Throwable> func) {
    return of(zio.mapError(func::apply, CanFail.canFail()));
  }

  public <B> Task<Tuple2<A, B>> zip(Task<B> ioB) {
    return of(
        zio.zip(ioB.zio)
            .map(t -> Tuple2.of(t._1, t._2))
    );
  }

  public <B> Task<B> zipRight(Task<B> ioB) {
    return zip(ioB).map(Tuple2::get_2);
  }

  public <B> Task<A> zipLeft(Task<B> ioB) {
    return zip(ioB).map(Tuple2::get_1);
  }

  public static <A, B> Task<ImmutableList<B>> foreach(Iterable<A> it, Function<A, Task<B>> func) {
    return of(ZIO
        .foreach(CollectionConverters.IterableHasAsScala(it).asScala(), a -> func.apply(a).zio)
        .map(sl -> ImmutableList.copyOf(CollectionConverters.IterableHasAsJava(sl).asJava()))
    );
  }

  public static <A, B> Task<ImmutableList<B>> foreachPar(Iterable<A> it, Function<A, Task<B>> func) {
    return of(ZIO
        .foreachPar(CollectionConverters.IterableHasAsScala(it).asScala(), a -> func.apply(a).zio)
        .map(sl -> ImmutableList.copyOf(CollectionConverters.IterableHasAsJava(sl).asJava()))
    );
  }

  public static <A> Task<A> fromOption(Option<A> opt, Throwable ifNone) {
    return opt.fold(Task::succeed, () -> Task.fail(ifNone));
  }

  public static <A> Task<A> fromEither(Either<Throwable, A> either) {
    return either.fold(Task::fail, Task::succeed);
  }

  public static <A> Task<A> absolve(Task<Either<Throwable, A>> eitherIO) {
    return eitherIO.flatMap(either ->
        either.fold(Task::fail, Task::succeed)
    );
  }

  public <B> Task<B> catchAll(Function<Throwable, Task<B>> catchAllFunc) {
    return of(zio.catchAll(t -> catchAllFunc.apply(t).zio, CanFail.canFail()));
  }

  public <B> Task<B> orElse(Supplier<Task<B>> other) {
    return catchAll(t -> other.get());
  }

  public <B> Task<B> fold(Function<Throwable, B> errorFunc, Function<A, B> successFunc) {
    return this.map(successFunc).catchAll(t -> Task.succeed(errorFunc.apply(t)));
  }

  public <B> Task<B> foldM(Function<Throwable, Task<B>> errorFunc, Function<A, Task<B>> successFunc) {
    return this.flatMap(successFunc).catchAll(errorFunc);
  }

}
