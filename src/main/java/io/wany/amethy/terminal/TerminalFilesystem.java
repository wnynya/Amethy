package io.wany.amethy.terminal;

import java.io.File;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

public class TerminalFilesystem {

  public static JsonArray readDirectory(File path) {
    JsonArray array = new JsonArray();
    if (path.isDirectory()) {
      JsonObject parent = readPathinfo(path.getParentFile());
      parent.remove("name");
      parent.addProperty("name", ".");
      array.add(parent);
      JsonObject current = readPathinfo(path);
      current.remove("name");
      current.addProperty("name", "..");
      array.add(current);

      File[] contents = path.listFiles();
      if (contents != null) {
        for (File c : contents) {
          array.add(readPathinfo(c));
        }
      }
    } else {
      array.add(readPathinfo(path));
    }
    return array;
  }

  public static JsonObject readPathinfo(File path) {
    JsonObject object = new JsonObject();
    object.addProperty("name", path.getName());
    object.addProperty("path", path.getAbsolutePath());
    object.addProperty("type", path.isFile() ? "file" : "dir");
    object.addProperty("read", path.canRead());
    object.addProperty("write", path.canWrite());
    object.addProperty("execute", path.canExecute());
    object.addProperty("size", size(path));
    return object;
  }

  public static long size(File path) {
    if (path.isFile()) {
      return path.length();
    } else {
      return 0;
      /*
       * long length = 0;
       * File[] files = path.listFiles();
       * if (files != null) {
       * for (File file : files) {
       * if (file.isFile()) {
       * length += file.length();
       * } else {
       * length += size(file);
       * }
       * }
       * }
       * return length;
       */
    }
  }

  public static void sendDirectoryInfo(String client, String pathString) {
    JsonObject object = new JsonObject();
    object.addProperty("client", client);

    JsonObject data = new JsonObject();
    File path = new File(pathString);
    if (path.exists()) {
      data.add("data", readDirectory(path));
    } else {
      data.addProperty("error", "Path Not Exist");
    }

    object.add("data", data);
    Terminal.event("fs-dir-info", object);
  }
}
