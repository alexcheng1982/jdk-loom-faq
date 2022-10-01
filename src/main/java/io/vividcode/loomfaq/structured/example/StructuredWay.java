package io.vividcode.loomfaq.structured.example;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import jdk.incubator.concurrent.StructuredTaskScope;

public class StructuredWay {
  int takeAction(String input) throws InterruptedException, ExecutionException {
    try (var scope = new StructuredTaskScope.ShutdownOnFailure()) {
      Future<Integer> v1 = scope.fork(() -> callOp1(input));
      Future<Integer> v2 = scope.fork(() -> callOp2(input));
      scope.join();
      scope.throwIfFailed();
      return combine(v1.resultNow(), v2.resultNow());
    }
  }

  int combine(int result1, int result2) throws InterruptedException, ExecutionException {
    try (var scope = new StructuredTaskScope.ShutdownOnFailure()) {
      Future<Integer> r = scope.fork(() -> callOp3(result1, result2));
      scope.join();
      scope.throwIfFailed();
      return r.resultNow();
    }
  }

  private int callOp3(int result1, int result2) {
    return result1 * result2;
  }

  private int callOp2(String input) {
    return 1;
  }

  private int callOp1(String input) {
    return 2;
  }

  public static void main(String[] args) throws ExecutionException, InterruptedException {
    System.out.println(new StructuredWay().takeAction("hello"));
  }
}
