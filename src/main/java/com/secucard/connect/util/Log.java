/*
 * Copyright (c) 2015. hp.weber GmbH & Co secucard KG (www.secucard.com)
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0.
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.secucard.connect.util;

import com.secucard.connect.SecucardConnect;
import com.secucard.connect.client.ClientError;

import java.util.logging.*;

/**
 * Logging util which uses java.util.logging.
 * Use system property "com.secucard.connect.config" to pass the client config file path.
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
    String path = System.getProperty("com.secucard.connect.config");
    try {
      SecucardConnect.Configuration cfg = path == null ? SecucardConnect.Configuration.get(): SecucardConnect.Configuration.get(path);
      if (cfg.logIgnoreGlobal) {
        Logger logger = Logger.getLogger(cfg.logger);
        LogFormatter formatter = new LogFormatter(cfg.logFormat);
        if (cfg.logPattern != null) {
          FileHandler fileHandler = new FileHandler(cfg.logPattern, cfg.logLimit, cfg.logCount, true);
          fileHandler.setFormatter(formatter);
          fileHandler.setLevel(Level.ALL);
          logger.addHandler(fileHandler);
        }
        ConsoleHandler consoleHandler = new ConsoleHandler();
        consoleHandler.setLevel(Level.ALL);
        consoleHandler.setFormatter(formatter);
        logger.addHandler(consoleHandler);
        logger.setLevel(Level.parse(cfg.logLevel));
      }
    } catch (Exception e) {
      throw new ClientError("Error initializing logging.", e);
    }
  }
}
