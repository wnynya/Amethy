package io.wany.amethy.terminal;

import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.Logger;
import org.apache.logging.log4j.message.Message;

public class TerminalConsoleLogFilter implements Filter {

  public boolean disabled = false;

  public void disable() {
    disabled = true;
  }

  private static boolean boomed = false;

  public static void boom() {
    boomed = true;
  }

  @Override
  public State getState() {
    return null;
  }

  @Override
  public void initialize() {

  }

  @Override
  public boolean isStarted() {
    return false;
  }

  @Override
  public boolean isStopped() {
    return false;
  }

  @Override
  public void start() {

  }

  @Override
  public void stop() {

  }

  @Override
  public Result filter(LogEvent event) {
    if (!disabled) {
      try {
        String message = event.getMessage().getFormattedMessage();
        StringBuilder stack = new StringBuilder();
        Throwable thrown = event.getThrown();
        if (thrown != null) {
          stack.append("\r\n");
          stack.append(thrown.getClass().getName()).append(" => ");
          stack.append(thrown.getMessage());
          StackTraceElement[] stea = thrown.getStackTrace();
          for (StackTraceElement ste : stea) {
            stack.append("\r\n\tat ").append(ste.getFileName()).append(":").append(ste.getLineNumber()).append(" (")
                .append(ste.getClassName()).append(".").append(ste.getMethodName()).append(")");
          }
          Throwable thrownCause = thrown.getCause();
          while (thrownCause != null) {
            stack.append("\r\nCaused by: ");
            stack.append(thrownCause.getClass().getName());
            StackTraceElement[] cstea = thrownCause.getStackTrace();
            for (StackTraceElement ste : cstea) {
              stack.append("\r\n\tat ").append(ste.getFileName()).append(":").append(ste.getLineNumber()).append(" (")
                  .append(ste.getClassName()).append(".").append(ste.getMethodName()).append(")");
            }
            thrownCause = thrownCause.getCause();
          }
        }
        message += stack;

        long time = event.getTimeMillis();

        String thread = event.getThreadName();

        String level = event.getLevel().name();

        String logger = event.getLoggerName();

        try {
          TimeUnit.MICROSECONDS.sleep(1);
        } catch (InterruptedException ignored) {
        }

        TerminalConsole.Log log = new TerminalConsole.Log(message, time, level, thread, logger);

        if (Terminal.WEBSOCKET.isConnected() && TerminalConsole.offlineLogs.size() <= 0) {
          TerminalConsole.sendLog(log);
        } else {
          TerminalConsole.offlineLogs.add(log);
        }

      } catch (Exception ignored) {
      }
    }
    return null;
  }

  @Override
  public Result filter(Logger logger, Level level, Marker marker, String msg, Object... params) {
    return null;
  }

  @Override
  public Result filter(Logger logger, Level level, Marker marker, String message, Object p0) {
    return null;
  }

  @Override
  public Result filter(Logger logger, Level level, Marker marker, Object msg, Throwable t) {
    return null;
  }

  @Override
  public Result filter(Logger logger, Level level, Marker marker, Message msg, Throwable t) {
    return null;
  }

  @Override
  public Result filter(Logger logger, Level level, Marker marker, String message, Object p0, Object p1) {
    return null;
  }

  @Override
  public Result filter(Logger logger, Level level, Marker marker, String message, Object p0, Object p1, Object p2) {
    return null;
  }

  @Override
  public Result filter(Logger logger, Level level, Marker marker, String message, Object p0, Object p1, Object p2,
      Object p3) {
    return null;
  }

  @Override
  public Result filter(Logger logger, Level level, Marker marker, String message, Object p0, Object p1, Object p2,
      Object p3, Object p4) {
    return null;
  }

  @Override
  public Result filter(Logger logger, Level level, Marker marker, String message, Object p0, Object p1, Object p2,
      Object p3, Object p4, Object p5) {
    return null;
  }

  @Override
  public Result filter(Logger logger, Level level, Marker marker, String message, Object p0, Object p1, Object p2,
      Object p3, Object p4, Object p5, Object p6) {
    return null;
  }

  @Override
  public Result filter(Logger logger, Level level, Marker marker, String message, Object p0, Object p1, Object p2,
      Object p3, Object p4, Object p5, Object p6, Object p7) {
    return null;
  }

  @Override
  public Result filter(Logger logger, Level level, Marker marker, String message, Object p0, Object p1, Object p2,
      Object p3, Object p4, Object p5, Object p6, Object p7, Object p8) {
    return null;
  }

  @Override
  public Result filter(Logger logger, Level level, Marker marker, String message, Object p0, Object p1, Object p2,
      Object p3, Object p4, Object p5, Object p6, Object p7, Object p8, Object p9) {
    return null;
  }

  @Override
  public Result getOnMatch() {
    return null;
  }

  @Override
  public Result getOnMismatch() {
    return null;
  }

}
