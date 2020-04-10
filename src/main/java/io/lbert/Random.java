package io.lbert;

import lombok.Value;

public interface Random {

  IO<Boolean> nextBoolean();
  IO<Integer> nextInteger();

  @Value(staticConstructor = "of")
  class Live implements Random {

    java.util.Random random;

    @Override
    public IO<Boolean> nextBoolean() {
      return IO.effect(random::nextBoolean);
    }

    @Override
    public IO<Integer> nextInteger() {
      return IO.effect(random::nextInt);
    }
  }
}
