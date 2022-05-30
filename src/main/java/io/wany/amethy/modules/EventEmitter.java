package io.wany.amethy.modules;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.function.Consumer;

public class EventEmitter {
  private final HashMap<String, List<Consumer<Object[]>>> listeners;

  public EventEmitter() {
    this.listeners = new HashMap<>();
  }

  public EventEmitter on(String event, Consumer<Object[]> callback) {
    if (!this.listeners.keySet().contains(event)) {
      listeners.put(event, new ArrayList<>());
    }
    listeners.get(event).add(callback);
    return this;
  }

  public EventEmitter emit(String event, Object... args) {
    if (!this.listeners.keySet().contains(event)) {
      return this;
    }
    listeners.get(event).forEach(callback -> {
      callback.accept(args);
    });
    return this;
  }

}
