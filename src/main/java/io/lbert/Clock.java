package io.lbert;

import lombok.Value;

import java.time.Duration;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.concurrent.TimeUnit;

public interface Clock {

  IO<Long> currentTime(TimeUnit unit);
  IO<OffsetDateTime> currentDateTime();
  IO<Long> nanoTime();
  IO<Unit> sleep(Duration duration);

  @Value(staticConstructor = "of")
  class Live implements Clock {

    Scheduler scheduler;

    @Override
    public IO<Long> currentTime(TimeUnit unit) {
      return IO.effect(java.lang.System::currentTimeMillis)
        .map(l -> unit.convert(l, TimeUnit.MILLISECONDS));
    }

    @Override
    public IO<OffsetDateTime> currentDateTime() {
      return currentTime(TimeUnit.MILLISECONDS)
        .flatMap(millis ->
          IO.effect(ZoneId::systemDefault).flatMap(zone ->
            IO.effect(() -> Instant.ofEpochMilli(millis)).flatMap(instant ->
              IO.effect(() -> OffsetDateTime.ofInstant(instant, zone))
            )
          )
        );
    }

    @Override
    public IO<Long> nanoTime() {
      return IO.effect(java.lang.System::nanoTime);
    }

    @Override
    public IO<Unit> sleep(Duration duration) {
      return null;
    }
  }
}
