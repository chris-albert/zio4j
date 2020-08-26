package zio4j.std;

public class Unit {

  private static final Unit Instance = Unit.of();

  private Unit () { }

  public static Unit of() {
    return Instance;
  }
}
