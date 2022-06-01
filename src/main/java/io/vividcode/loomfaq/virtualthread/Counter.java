package io.vividcode.loomfaq.virtualthread;

public class Counter {

  private int count = 0;

  synchronized void increase() {
    count++;
  }

  public int getCount() {
    return count;
  }

  public static void main(String[] args) {
    var counter = new Counter();
    int count = 10_000;
    for (int i = 0; i < count; i++) {
      try {
        Thread.ofVirtual().start(counter::increase)
            .join();
      } catch (InterruptedException e) {
        // ignore
      }
    }
    System.out.println(counter.getCount());
  }
}
