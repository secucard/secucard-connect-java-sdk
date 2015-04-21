package com.secucard.connect.util;

import java.util.HashMap;

/**
 * Puts data to thread locals and pulls them out again.
 */
public class ThreadLocalUtil {

  private final static ThreadLocal<ThreadVariables> THREAD_VARIABLES = new ThreadLocal<ThreadVariables>() {

    /**
     * @see java.lang.ThreadLocal#initialValue()
     */
    @Override
    protected ThreadVariables initialValue() {
      return new ThreadVariables();
    }
  };

  public static Object get(String name) {
    return THREAD_VARIABLES.get().get(name);
  }

  public static Object get(String name, InitialValue initialValue) {
    Object o = THREAD_VARIABLES.get().get(name);
    if (o == null) {
      THREAD_VARIABLES.get().put(name, initialValue.create());
      return get(name);
    } else {
      return o;
    }
  }

  public static void set(String name, Object value) {
    THREAD_VARIABLES.get().put(name, value);
  }

  public static void remove() {
    THREAD_VARIABLES.remove();
  }

  private static class ThreadVariables extends HashMap<String, Object> {
  }

  public static abstract class InitialValue {

    public abstract Object create();

  }

}
