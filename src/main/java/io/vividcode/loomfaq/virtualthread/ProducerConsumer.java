package io.vividcode.loomfaq.virtualthread;

import io.vividcode.loomfaq.CheckedRunnable;
import io.vividcode.loomfaq.Helper;
import java.time.Duration;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

/**
 * A simple example using producer/consumer pattern
 */
public class ProducerConsumer {

  private final BlockingQueue<Integer> numbers = new ArrayBlockingQueue<>(10);
  private final int count = 100;
  private final int delay = 30;
  private final CountDownLatch latch = new CountDownLatch(count);

  public static void main(String[] args) throws Exception {
    Helper.timed((CheckedRunnable) () -> new ProducerConsumer().run());
  }

  public void run() throws InterruptedException {
    for (int i = 0; i < count; i++) {
      produce(i);
    }
    for (int i = 0; i < count * 2; i++) {
      consume();
    }
    latch.await();
  }

  void produce(int number) {
    Thread.ofVirtual()
        .start(() -> {
          try {
            Thread.sleep(Duration.ofSeconds(ThreadLocalRandom.current().nextInt(delay)));
            numbers.put(number);
          } catch (InterruptedException e) {
            // ignore
          }
        });
  }

  void consume() {
    Thread.ofVirtual()
        .start(() -> {
          try {
            Integer value = numbers.poll(delay, TimeUnit.SECONDS);
            if (value != null) {
              System.out.printf("Consumed %d%n", value);
              latch.countDown();
            }
          } catch (InterruptedException e) {
            // ignore
          }
        });
  }
}
