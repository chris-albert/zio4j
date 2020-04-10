package io.lbert;

import lombok.Value;

public interface System {

  IO<Option<String>> env(String variable);
  IO<Option<String>> property(String variable);
  IO<String> lineSeparator();

  @Value(staticConstructor = "of")
  class Live implements System {

    @Override
    public IO<Option<String>> env(String variable) {
      return IO.effect(() -> Option.of(java.lang.System.getenv(variable)));
    }

    @Override
    public IO<Option<String>> property(String variable) {
      return IO.effect(() -> Option.of(java.lang.System.getProperty(variable)));
    }

    @Override
    public IO<String> lineSeparator() {
      return IO.effect(java.lang.System::lineSeparator);
    }
  }
}
