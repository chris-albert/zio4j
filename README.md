# ZIO4J
This is a wrapper around java for the awesome scala [ZIO](https://zio.dev/) library.

For simplicity im starting with everything being a `Task[A]`, but in ZIO4J im calling it `IO<A>`, 
since I like the `IO` name better. I haven't been able to figure out the variance stuff in java
to get the environment or the error channel working properly, but I still think there is use 
for a good async/concurrency lib in java.


You can create a successful ZIO effect:
```java
final IO<Integer> io = IO.succeed(10);
```

and you can create a failed effect
```java
final IO<Integer> io = IO.fail(new Throwable());
```

There are lots more examples in the [tests](https://github.com/chris-albert/zio4j/blob/master/src/test/java/io/lbert/IOTest.java)


You can do kinda ZIO things by using constructor style dependency injection:

```java
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
``` 
