package io.vividcode.loomfaq.structured.example;

public class OldWay {
  int takeAction(String input) {
    int result1 = callOp1(input);
    int result2 = callOp2(input);
    return callOp3(result1, result2);
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
}
