package io.vividcode.concurrency;

import java.time.Duration;
import java.util.concurrent.Callable;

public class Helper {

  public static <T> T timed(Callable<T> callable) throws Exception {
    long current = System.nanoTime();
    T result = callable.call();
    System.out.printf("Duration: %s", Duration.ofNanos(System.nanoTime() - current));
    return result;
  }
}
