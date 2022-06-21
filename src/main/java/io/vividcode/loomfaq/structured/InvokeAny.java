package io.vividcode.loomfaq.structured;

import io.vividcode.loomfaq.Helper;
import java.time.Duration;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import jdk.incubator.concurrent.StructuredTaskScope;

public class InvokeAny {

  public static void main(String[] args) throws Exception {
    System.out.println(Helper.timed(() -> new InvokeAny().invokeAny()));
  }

  public long invokeAny() throws InterruptedException {
    try (var scope = new StructuredTaskScope.ShutdownOnSuccess<>()) {
      var futures = subTasks().map(scope::fork).toList();
      scope.join();
      return futures.stream().filter(f -> !f.isCancelled())
          .map(Future::resultNow)
          .reduce(0, Integer::sum);
    }
  }

  private Stream<Callable<Integer>> subTasks() {
    return IntStream.range(0, 1000).mapToObj(i -> () -> {
      try {
        Thread.sleep(
            Duration.ofSeconds(1 + ThreadLocalRandom.current().nextLong(5)));
      } catch (InterruptedException e) {
        // ignore
      }
      return i;
    });
  }
}
