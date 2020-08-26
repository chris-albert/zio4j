package zio4j;

import scala.concurrent.ExecutionContext;
import scala.runtime.Nothing$;
import zio.Cause;
import zio.internal.Executor;
import zio.internal.NamedThreadFactory;
import zio.internal.Platform;
import zio.internal.Tracing;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ZIO4JPlatform implements Platform {

  private static final Integer defaultYieldOpCount = 2048;

  private ZIO4JPlatform() { }

  public static ZIO4JPlatform of() {
    return new ZIO4JPlatform();
  }

  @Override
  public Executor executor() {
    return Executor.fromExecutionContext(
      defaultYieldOpCount,
      ExecutionContext.fromExecutor(getExecutor())
    );
  }

  @Override
  public Tracing tracing() {
    return Tracing.enabled();
  }

  @Override
  public boolean fatal(Throwable t) {
    return t instanceof VirtualMachineError;
  }

  @Override
  public Nothing$ reportFatal(Throwable t) {
    t.printStackTrace();
    try {
      java.lang.System.exit(-1);
      throw t;
    } catch (Throwable tt) {
      throw new RuntimeException(tt);
    }
  }

  @Override
  public void reportFailure(Cause<Object> cause) {
    if(cause.died()) {
      java.lang.System.err.println(cause.prettyPrint());
    }
  }

  public static ThreadPoolExecutor getExecutor() {

    final var corePoolSize  = 10;
    final var maxPoolSize   = corePoolSize;
    final var keepAliveTime = 60000L;
    final var timeUnit      = TimeUnit.MILLISECONDS;
    final var workQueue     = new LinkedBlockingQueue<Runnable>();
    final var threadFactory = new NamedThreadFactory("zio-default-async", true);

    final var threadPool = new ThreadPoolExecutor(
      corePoolSize,
      maxPoolSize,
      keepAliveTime,
      timeUnit,
      workQueue,
      threadFactory
    );

    threadPool.allowCoreThreadTimeOut(true);

    return threadPool;
  }

}

