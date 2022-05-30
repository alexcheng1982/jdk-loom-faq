package io.vividcode.loomfaq;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Create virtual threads")
public class CreateVirtualThreadTests {

  @Test
  @DisplayName("Simple virtual thread")
  void createVirtualThread() throws InterruptedException {
    var thread = Thread.ofVirtual()
        .start(() -> System.out.println("I'm running"));
    thread.join();
    assertEquals("", thread.getName());
    assertTrue(thread.isVirtual());
    assertTrue(thread.isDaemon());
  }

  @Test
  @DisplayName("Disable thread-local")
  void disableThreadLocal() throws InterruptedException {
    var threadLocal = ThreadLocal.withInitial(() -> 1);
    Thread.ofVirtual()
        .allowSetThreadLocals(false)
        .start(() -> assertEquals(1, threadLocal.get()))
        .join();

    Thread.ofVirtual()
        .allowSetThreadLocals(false)
        .start(() -> assertThrows(UnsupportedOperationException.class, () -> threadLocal.set(100))).join();
  }

  @Test
  @DisplayName("Disable inheritable thread-local")
  void disableInheritableThreadLocal() throws InterruptedException {
    var threadLocal = new InheritableThreadLocal<Integer>();
    Thread.ofVirtual()
        .name("parent")
        .start(() -> {
          threadLocal.set(300);
          Thread.ofVirtual()
              .name("child")
              .inheritInheritableThreadLocals(false)
              .start(() -> assertNull(threadLocal.get()));
        }).join();
  }
}
