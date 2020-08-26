package zio4j;

import lombok.Value;
import zio4j.std.Unit;

import java.time.Duration;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.concurrent.TimeUnit;

public interface Clock {

  Task<Long> currentTime(TimeUnit unit);
  Task<OffsetDateTime> currentDateTime();
  Task<Long> nanoTime();
  Task<Unit> sleep(Duration duration);

  @Value(staticConstructor = "of")
  class Live implements Clock {

    Scheduler scheduler;

    @Override
    public Task<Long> currentTime(TimeUnit unit) {
      return Task.effect(java.lang.System::currentTimeMillis)
        .map(l -> unit.convert(l, TimeUnit.MILLISECONDS));
    }

    @Override
    public Task<OffsetDateTime> currentDateTime() {
      return currentTime(TimeUnit.MILLISECONDS)
        .flatMap(millis ->
            Task.effect(ZoneId::systemDefault).flatMap(zone ->
                Task.effect(() -> Instant.ofEpochMilli(millis)).flatMap(instant ->
                    Task.effect(() -> OffsetDateTime.ofInstant(instant, zone))
            )
          )
        );
    }

    @Override
    public Task<Long> nanoTime() {
      return Task.effect(java.lang.System::nanoTime);
    }

    @Override
    public Task<Unit> sleep(Duration duration) {
      return null;
    }
  }
}
