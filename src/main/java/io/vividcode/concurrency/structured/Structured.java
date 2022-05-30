package io.vividcode.concurrency.structured;

import io.vividcode.concurrency.Helper;
import java.time.Duration;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import jdk.incubator.concurrent.StructuredTaskScope;

public class Structured {

  public static void main(String[] args) throws Exception {
    System.out.println(Helper.timed(() -> new Structured().calculate()));
  }

  public int calculate() throws InterruptedException, ExecutionException {
    try (var scope = new StructuredTaskScope.ShutdownOnFailure()) {
      Future<Integer> v1 = scope.fork(this::op1);
      int v2 = calculateInner();
      scope.join();
      scope.throwIfFailed();
      return v1.resultNow() * v2;
    }
  }

  private int calculateInner() throws InterruptedException, ExecutionException {
    try (var scope = new StructuredTaskScope.ShutdownOnFailure()) {
      Future<Integer> v21 = scope.fork(this::op21);
      Future<Integer> v22 = scope.fork(this::op22);
      scope.join();
      scope.throwIfFailed();
      return v21.resultNow() + v22.resultNow();
    }
  }

  private int op1() {
    try {
      Thread.sleep(Duration.ofSeconds(3));
    } catch (InterruptedException e) {
      // ignored
    }
    System.out.println("OP 1");
    return 1;
  }

  private int op22() {
    try {
      Thread.sleep(Duration.ofSeconds(2));
    } catch (InterruptedException e) {
      // ignored
    }
    System.out.println("OP 2.2");
    return 3;
  }

  private int op21() {
    try {
      Thread.sleep(Duration.ofSeconds(4));
    } catch (InterruptedException e) {
      // ignored
    }
    System.out.println("OP 2.1");
    return 3;
  }
}
