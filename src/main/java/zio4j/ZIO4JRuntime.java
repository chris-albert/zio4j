package zio4j;

import zio.Runtime;

public class ZIO4JRuntime {

  private static final Runtime<Object> zioRuntime = Runtime.apply(new Object(), ZIO4JPlatform.of());

  public static <A> A unsafeRun(Task<A> io) {
    return zioRuntime.unsafeRunTask(io::getZIO);
  }

}
