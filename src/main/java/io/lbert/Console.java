package io.lbert;

import lombok.Value;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public interface Console {

  IO<Unit> putStr(String str);
  IO<Unit> putStrLn(String str);
  IO<String> getStrLn();

  @Value(staticConstructor = "of")
  class Live implements Console {

    @Override
    public IO<Unit> putStr(String str) {
      return IO.effect(() -> {
        java.lang.System.out.print(str);
        return Unit.of();
      });
    }

    @Override
    public IO<Unit> putStrLn(String str) {
      return IO.effect(() -> {
        java.lang.System.out.println(str);
        return Unit.of();
      });
    }

    @Override
    public IO<String> getStrLn() {
      return IO.effect(() -> {
        final BufferedReader reader =
          new BufferedReader(new InputStreamReader(java.lang.System.in));
        try {
          return reader.readLine();
        } catch (Throwable t) {
          throw new RuntimeException(t);
        }
      });
    }
  }
}
