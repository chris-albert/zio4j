package zio4j;

import lombok.Value;

public interface Random {

  Task<Boolean> nextBoolean();
  Task<Integer> nextInteger();

  @Value(staticConstructor = "of")
  class Live implements Random {

    java.util.Random random;

    @Override
    public Task<Boolean> nextBoolean() {
      return Task.effect(random::nextBoolean);
    }

    @Override
    public Task<Integer> nextInteger() {
      return Task.effect(random::nextInt);
    }
  }
}
