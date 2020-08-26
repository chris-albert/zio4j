# ZIO4J
This is a wrapper around java for the awesome scala [ZIO](https://zio.dev/) library.

The idea here it to make using the ZIO library easy to use from Java.


You can create a successful ZIO effect:
```java
final Task<Integer> task = Task.succeed(10);
```

and you can create a failed effect
```java
final Task<Integer> task = Taskfail(new Throwable());
```

There are lots more examples in the [tests](https://github.com/chris-albert/zio4j/blob/master/src/test/java/zio4j.TaskTest.java)


You can do kinda ZIO things by using constructor style dependency injection:

```java
import lombok.Value;

public class App {

  public static void main(String[] args) {

    final Program program = Program.of(Console.Live.of());

    ZIO4JRuntime.unsafeRun(program.run());
  }

  @Value(staticConstructor = "of")
  private static class Program {

    Console console;

    public Task<Unit> run() {
      return console.putStrLn("Hi, this is a java ZIO test....")
        .flatMap(u    -> console.putStrLn("What is your name?"))
        .flatMap(uu   -> console.getStrLn())
        .flatMap(name -> console.putStrLn("Hello " + name + ", nice to meet you"))
      ;
    }
  }
}
``` 
