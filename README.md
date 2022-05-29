# JDK Project Loom FAQ

## General

### What's Project Loom?

> **Project Loom** is to intended to explore, incubate and deliver Java VM features and APIs built on top of them for the purpose of supporting easy-to-use, high-throughput lightweight concurrency and new programming models on the Java platform. 
>
> [Project Loom Wiki](https://wiki.openjdk.java.net/display/loom/Main)



### How can I use Project Loom?

According to the JDK release process, features in Project Loom will be broken down into several JEPs and made available in different JDK releases.

| Feature                                                     | Target JDK Release | Status    |
| ----------------------------------------------------------- | ------------------ | --------- |
| [Virtual Threads](https://openjdk.java.net/jeps/425)        | 19                 | Preview   |
| [Structured Concurrency](https://openjdk.java.net/jeps/428) | 19                 | Incubator |

In the meantime, you can download Project Loom early-access builds from [OpenJDK.net](https://jdk.java.net/loom/).

## Virtual Thread

### What's virtual thread?

Before Project Loom, there is only one type of threads in Java, which is called *platform thread* in Project Loom. Platform threads are typically mapped 1:1 to kernel threads scheduled by the operating system. In Project Loom, virtual threads are introduced as a new type of threads.

Virtual threads are typically `user-mode threads` scheduled by the Java runtime rather than the operating system.

Platform and virtual threads are both represented using `java.lang.Thread`.

### How to create virtual threads?

The first approach to create virtual threads is using the `Thread.ofVirtual` method.

In the code below, a new virtual thread is created and started. The return value is an instance of `java.lang.Thread` object.

```java
var thread = Thread.ofVirtual().name("my virtual thread")
        .start(() -> System.out.println("I'm running"))
```

The second approach is using `Thread.startVirtualThread(Runnable task)` method. This is the same as calling `Thread.ofVirtual().start(task)`.

The third approach is using `ThreadFactory`.

```java
var factory = Thread.ofVirtual().factory();
var thread = factory.newThread(() -> System.out.println("Create in factory"));
```

### How to check if a thread is virtual?

The new `isVirtual` method in `java.lang.Thread` returns `true` is this thread is a virtual thread.

### Does a virtual thread has name?

A virtual thread doesn't have a name by default. The `getName` method returns the empty string if a thread name is not set.

The thread name can be set using the `setName` method, or using the `name` method of `Thread.Builder` returned from `Thread.ofVirtual`.

It's recommended to always set a name for debugging and error diagnosis purpose.

### Can virtual threads be non-daemon threads?

No. Virtual threads are always daemon threads. So they cannot prevent JVM from terminating. Calling `setDaemon(false)` on a virtual thread will throw an `IllegalArgumentException` exception.

### Can the priority of a virtual thread be changed?

No. Virtual threads have a fixed priority of `Thread.NORM_PRIORITY`. The `Thread.setPriority(int)` method has no effect on virtual threads.

### Can virtual threads support thread-local variables?

Yes. Virtual threads support both thread-local variables (`ThreadLocal`) and inheritable thread-local variables (`InheritableThreadLocal`). 

### Can thread local variables be disabled for virtual threads?

Yes. If no thread-local variables are required, the support can be disabled using methods in `Thread.Builder`.

To disable thread-local variables, you can use the `allowSetThreadLocals(boolean allow)` method.

When thread-local variables are not allowed:

* Using the `ThreadLocal.set(Object)` method to set a value for a thread-local variable will throw `UnsupportedOperationException`. 
* The`ThreadLocal.get()` method always returns the initial value.

In the code below, calling `threadLocal.set(100)` throws `UnsupportedOperationException`.

```java
ThreadLocal<Integer> threadLocal = new ThreadLocal<>();
Thread.ofVirtual()
    .allowSetThreadLocals(false)
    .start(() -> threadLocal.set(100)) // throws UnsupportedOperationException
    .join();
```

In the code below, the initial value `1` of the thread-local variable is printed out.

```java
ThreadLocal<Integer> threadLocal = ThreadLocal.withInitial(() -> 1);
Thread.ofVirtual()
    .allowSetThreadLocals(false)
    .start(() -> System.out.println(threadLocal.get()))
    .join();
```

To not inherit the values of inheritable thread-local variables, you can use the `inheritInheritableThreadLocals(boolean inherit)` method.

In the code below, the `InheritableThreadLocal` object has its value set to `300` in the parent thread. However, the child thread disabled inheritance of inheritable thread-local variables, the `InheritableThreadLocal`object has the value `null` in the child thread.

```java
var inheritableThreadLocal = new InheritableThreadLocal<Integer>();
Thread.ofVirtual()
    .name("parent")
    .start(() -> {
      inheritableThreadLocal.set(300);
      Thread.ofVirtual()
          .name("child")
          .inheritInheritableThreadLocals(false)
          .start(() -> System.out.println(inheritableThreadLocal.get()));
    }).join();
```

