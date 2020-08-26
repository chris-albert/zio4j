package zio4j.std.tuple;

import lombok.Value;

@Value(staticConstructor = "of")
public class Tuple2<T1, T2> {
  T1 _1;
  T2 _2;
}
