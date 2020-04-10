package io.lbert;

import zio.ZIO;

public class App {

  public static void main(String[] args) {

    final var z = zio.ZIO.succeed(() -> 10);

    System.out.println("Testing IO unsafeRun");

  }
}
