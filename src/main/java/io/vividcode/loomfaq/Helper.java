package io.vividcode.loomfaq;

import java.time.Duration;
import java.util.concurrent.Callable;

public class Helper {

  public static <T> T timed(Callable<T> callable) throws Exception {
    long current = System.nanoTime();
    T result = callable.call();
    System.out.printf("Duration: %s%n", Duration.ofNanos(System.nanoTime() - current));
    return result;
  }

  public static void timed(Runnable runnable) {
    try {
      timed(() -> {
        runnable.run();
        return null;
      });
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  public static void timed(CheckedRunnable runnable) throws Exception {
    timed(() -> {
      runnable.run();
      return null;
    });
  }
}
