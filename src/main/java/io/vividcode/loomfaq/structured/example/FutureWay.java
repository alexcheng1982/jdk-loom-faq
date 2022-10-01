package io.vividcode.loomfaq.structured.example;

import java.util.concurrent.CompletableFuture;

public class FutureWay {
  CompletableFuture<Integer> takeAction(String input) {
    var result1 = callOp1(input);
    var result2 = callOp2(input);
    return result1
        .thenCombine(result2, (v1, v2) -> new int[] {v1, v2})
        .thenCompose(values -> callOp3(values[0], values[1]));
  }

  private CompletableFuture<Integer> callOp3(int result1, int result2) {
    return CompletableFuture.completedFuture(result1 * result2);
  }

  private CompletableFuture<Integer> callOp2(String input) {
    return CompletableFuture.completedFuture(1);
  }

  private CompletableFuture<Integer> callOp1(String input) {
    return CompletableFuture.completedFuture(2);
  }
}
