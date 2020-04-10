package io.lbert;

public class Unit {

  private static final Unit Instance = new Unit();

  private Unit () { }

  public static Unit of() {
    return Instance;
  }
}
