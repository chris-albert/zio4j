# ZIO4J
This is a wrapper around java for the awesome scala [ZIO](https://zio.dev/) library.

For simplicity im starting with everything being a `Task[A]`, but in ZIO4J im calling it `IO<A>`, 
since I like the `IO` name better. I haven't been able to figure out the variance stuff in java
to get the environment or the error channel working properly, but I still think there is use 
for a good async lib in java, since their ecosystem has trash.

See the [tests](https://github.com/chris-albert/zio4j/blob/master/src/test/java/io/lbert/IOTest.java) for examples.
 