package zio4j;

import lombok.Value;
import zio4j.std.Unit;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public interface Console {

  Task<Unit> putStr(String str);
  Task<Unit> putStrLn(String str);
  Task<String> getStrLn();

  @Value(staticConstructor = "of")
  class Live implements Console {

    @Override
    public Task<Unit> putStr(String str) {
      return Task.effect(() -> {
        java.lang.System.out.print(str);
        return Unit.of();
      });
    }

    @Override
    public Task<Unit> putStrLn(String str) {
      return Task.effect(() -> {
        java.lang.System.out.println(str);
        return Unit.of();
      });
    }

    @Override
    public Task<String> getStrLn() {
      return Task.effect(() -> {
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
