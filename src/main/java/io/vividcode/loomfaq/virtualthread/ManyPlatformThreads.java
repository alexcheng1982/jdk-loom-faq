package io.vividcode.loomfaq.virtualthread;

import java.time.Duration;
import java.util.concurrent.Executors;
import java.util.stream.IntStream;

/** Start a large number of platform threads. It may take a longer time to finish. */
public class ManyPlatformThreads {

  public static void main(String[] args) {
    try (var executor = Executors.newFixedThreadPool(20_000)) {
      IntStream.range(0, 20_000)
          .forEach(
              i ->
                  executor.submit(
                      () -> {
                        Thread.sleep(Duration.ofSeconds(1));
                        return i;
                      }));
    }
  }
}
