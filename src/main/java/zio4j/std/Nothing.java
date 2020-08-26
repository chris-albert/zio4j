package zio4j.std;

/**
 * This is supposed to represent scala's `Nothing` type,
 * which can't be properly represented in Java. So this is just
 * a class that you can't create... in theory doing the same thing.
 */
public class Nothing {

  private Nothing() {}
}
