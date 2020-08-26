package zio4j;

import lombok.Value;
import zio4j.std.Option;

public interface System {

  Task<Option<String>> env(String variable);
  Task<Option<String>> property(String variable);
  Task<String> lineSeparator();

  @Value(staticConstructor = "of")
  class Live implements System {

    @Override
    public Task<Option<String>> env(String variable) {
      return Task.effect(() -> Option.of(java.lang.System.getenv(variable)));
    }

    @Override
    public Task<Option<String>> property(String variable) {
      return Task.effect(() -> Option.of(java.lang.System.getProperty(variable)));
    }

    @Override
    public Task<String> lineSeparator() {
      return Task.effect(java.lang.System::lineSeparator);
    }
  }
}
