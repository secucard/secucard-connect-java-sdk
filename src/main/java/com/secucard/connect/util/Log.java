package com.secucard.connect.util;

import com.secucard.connect.ClientConfiguration;
import com.secucard.connect.ClientException;

import java.util.logging.*;

/**
 * Logging util which uses java.util.logging.
 */
public class Log {
  private final Logger logger;

  static {
    init();
  }

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

  private static void init() {
    try {
      ClientConfiguration cfg = ClientConfiguration.getDefault();
      if (cfg.isLogIgnoreGlobal()) {
        Logger logger = Logger.getLogger("com.secucard.connect");
        LogFormatter formatter = new LogFormatter(cfg.getLogFormat());
        if (cfg.getLogPath() != null) {
          FileHandler fileHandler = new FileHandler(cfg.getLogPath(), cfg.getLogLimit(), cfg.getLogCount(), true);
          fileHandler.setFormatter(formatter);
          logger.addHandler(fileHandler);
        }
        ConsoleHandler consoleHandler = new ConsoleHandler();
        consoleHandler.setFormatter(formatter);
        consoleHandler.setLevel(Level.ALL);
        logger.addHandler(consoleHandler);
        logger.setLevel(Level.parse(cfg.getLogLevel()));
      }
    } catch (Exception e) {
      throw new ClientException("Error initializing logging.", e);
    }
  }
}
