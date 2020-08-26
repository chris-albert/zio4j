package zio4j;

import zio4j.std.Unit;

public interface ZIO4JApp {

  Task<Unit> run(String[] args);
}
