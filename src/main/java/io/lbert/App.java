package io.lbert;

import lombok.Value;

public class App {

  public static void main(String[] args) {

    final Program program = Program.of(Console.Live.of());

    IORuntime.unsafeRun(program.run());
  }

  @Value(staticConstructor = "of")
  private static class Program {

    Console console;

    public IO<Unit> run() {
      return console.putStrLn("Hi, this is a java ZIO test....")
        .flatMap(u    -> console.putStrLn("What is your name?"))
        .flatMap(uu   -> console.getStrLn())
        .flatMap(name -> console.putStrLn("Hello " + name + ", nice to meet you"))
      ;
    }
  }
}
