import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.function.Consumer;

public class Test {

  public static void main(String... args) {
    EventEmitter e = new Test.EventEmitter();
    e.on("test", (args1) -> {
      System.out.println("test1");
    });
    e.on("test", (args1) -> {
      System.out.println("test2");
    });
    e.on("sans", (args1) -> {
      System.out.println("sans1");
    });
    e.on("sans", (args1) -> {
      System.out.println("sans2");
    });
    e.emit("sans", "a");
    e.emit("test", "a");
  }

  public class EventEmitter {
    private HashMap<String, List<Consumer>> listeners;

    public EventEmitter() {
      this.listeners = new HashMap<>();
    }

    public void on(String event, Consumer callbask) {
      if (!this.listeners.keySet().contains(event)) {
        listeners.put(event, new ArrayList<>());
      }
      listeners.get(event).add(callbask);
    }

    public void emit(String event, Object... args) {
      listeners.get(event).forEach(callback -> {
        callback.accept(args);
      });
    }
  }

}
