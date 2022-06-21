package io.vividcode.loomfaq.structured;

import io.vividcode.loomfaq.Helper;
import java.time.Duration;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import jdk.incubator.concurrent.StructuredTaskScope;

public class InvokeAll {

  public static void main(String[] args) throws Exception {
    System.out.println(Helper.timed(() -> new InvokeAll().invokeAll()));
  }

  public long invokeAll() throws InterruptedException, ExecutionException {
    try (var scope = new StructuredTaskScope.ShutdownOnFailure()) {
      var futures = subTasks().map(scope::fork).toList();
      scope.join();
      scope.throwIfFailed();
      return futures.stream().map(Future::resultNow).reduce(0, Integer::sum);
    }
  }

  private Stream<Callable<Integer>> subTasks() {
    return IntStream.range(0, 10_000).mapToObj(i -> () -> {
      try {
        Thread.sleep(
            Duration.ofSeconds(ThreadLocalRandom.current().nextLong(3)));
      } catch (InterruptedException e) {
        // ignore
      }
      return i;
    });
  }
}
