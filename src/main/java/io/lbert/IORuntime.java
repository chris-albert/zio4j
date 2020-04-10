package io.lbert;

import zio.Runtime;

public class IORuntime {

  private static final Runtime<Object> zioRuntime = Runtime.apply(new Object(), IOPlatform.of());

  public static <A> A unsafeRun(IO<A> io) {
    return zioRuntime.unsafeRunTask(io::getZIO);
  }

}
