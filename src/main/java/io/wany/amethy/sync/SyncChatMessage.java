package io.wany.amethy.sync;

import java.util.UUID;

import org.bukkit.Bukkit;

import com.google.gson.JsonObject;

import io.wany.amethy.modules.Crypto;
import io.wany.amethy.modules.Message;
import net.kyori.adventure.text.Component;

public class SyncChatMessage {

  protected String server;
  protected String name;
  protected Component message;
  protected long datetime;

  protected String key;

  protected SyncChatMessage(String server, String name, Component message, long datetime) {
    this.server = server;
    this.name = name;
    this.message = message;
    this.datetime = datetime;
    this.key = new Crypto(server + "/" + name + "/" + datetime + "/" + Message.stringify(message)).hash();
  }

  protected SyncChatMessage(String server, String name, String message, long datetime) {
    this.server = server;
    this.name = name;
    this.message = Message.of(message);
    this.datetime = datetime;
    this.key = new Crypto(server + "/" + name + "/" + datetime + "/" + message).hash();
  }

  protected JsonObject jsonify() {
    JsonObject object = new JsonObject();
    object.addProperty("name", this.name);
    object.addProperty("message", Message.stringify(message));
    object.addProperty("datetime", this.datetime);
    return object;
  }

  protected String key() {
    return this.key;
  }

  protected void broadcast() {
    Bukkit.broadcast(Message.of("[" + this.server + "] " + this.name + ": ", this.message));
  }

}
