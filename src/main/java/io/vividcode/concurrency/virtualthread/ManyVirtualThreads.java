package io.vividcode.concurrency.virtualthread;

import java.time.Duration;
import java.util.concurrent.Executors;
import java.util.stream.IntStream;

public class ManyVirtualThreads {

  public static void main(String[] args) {
    try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
      IntStream.range(0, 10_000).forEach(i -> executor.submit(() -> {
        Thread.sleep(Duration.ofSeconds(1));
        return i;
      }));
    }
  }
}
