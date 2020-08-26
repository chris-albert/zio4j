package zio4j;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import lombok.Value;

import java.time.Duration;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

@Value(staticConstructor = "of")
public class Scheduler {

  ScheduledExecutorService scheduledExecutorService;

  public static Scheduler getDefault() {
    return of(Executors.newScheduledThreadPool(1, new ThreadFactoryBuilder().build()));
  }

  public Supplier<Boolean> schedule(Runnable task, Duration duration) {
    if(duration.isZero()) {
      task.run();
      return () -> false;
    } else {
      final var future = scheduledExecutorService.schedule(new Runnable() {
        @Override
        public void run() {
          task.run();
        }
      }, duration.getNano(), TimeUnit.NANOSECONDS);
      return () -> future.cancel(true);
    }
  }

}
