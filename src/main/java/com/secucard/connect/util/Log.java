package com.secucard.connect.util;

import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

/**
 * Logging util which uses java.util.logging.
 */
public class Log {
  private final Logger logger;

  public Log(Class type) {
    this.logger = Logger.getLogger(type.getName());
  }

  public void debug(Object... args) {
    log(Level.FINE, args);
  }

  public void trace(Object... args) {
    log(Level.FINEST, args);
  }

  public void info(Object... args) {
    log(Level.INFO, args);
  }

  public void warn(Object... args) {
    log(Level.WARNING, args);
  }

  public void error(Object... args) {
    log(Level.SEVERE, args);
  }

  private void log(Level level, Object... args) {
    if (logger.isLoggable(level)) {
      LogRecord record = new LogRecord(level, getMessage(args));
      record.setThrown(getThrowable(args));
      setLogSource(record);
      logger.log(record);
    }
  }

  private void setLogSource(LogRecord record) {
    record.setSourceClassName(logger.getName());  // default source is logger name
    for (StackTraceElement el : new Throwable().getStackTrace()) {
      if (!el.getClassName().equals(Log.class.getName())) {
        // take fist occurrence not being Log.class
        record.setSourceClassName("[" + record.getThreadID() + "] " + el.toString());

        break;
      }
    }
  }

  private static Throwable getThrowable(Object... args) {
    if (args != null) {
      for (Object arg : args) {
        if (arg != null && arg instanceof Throwable) {
          return (Throwable) arg;
        }
      }
    }
    return null;
  }

  private static String getMessage(Object... args) {
    if (args == null || args.length == 0) {
      return null;
    }
    StringBuilder sb = new StringBuilder();
    for (Object arg : args) {
      if (arg == null || !(arg instanceof Throwable)) {
        sb.append(arg);
      }
    }
    return sb.toString();
  }
}
