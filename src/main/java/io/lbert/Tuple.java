package io.lbert;

import lombok.Value;

@Value(staticConstructor = "of")
public class Tuple<T1, T2> {

  T1 _1;
  T2 _2;

}
