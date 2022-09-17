package io.wany.amethy.terminal;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.attribute.BasicFileAttributes;

import org.apache.commons.io.FileUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import io.wany.amethy.modules.Console;
import io.wany.amethy.modules.network.HTTPRequest;

public class TerminalFilesystem {

  public static JsonArray readDirectory(File path) {
    JsonArray array = new JsonArray();
    if (path.isDirectory()) {
      JsonObject parent = readPathinfo(path.getParentFile());
      parent.remove("name");
      parent.addProperty("name", "..");
      array.add(parent);
      JsonObject current = readPathinfo(path);
      current.remove("name");
      current.addProperty("name", ".");
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
    try {
      BasicFileAttributes attr = Files.readAttributes(path.toPath(), BasicFileAttributes.class);
      object.addProperty("creationdate", attr.creationTime().toString());
      object.addProperty("modifieddate", attr.lastModifiedTime().toString());
    } catch (Exception ignored) {

    }
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

  public static void uploadFile(String client, String pathString) {
    JsonObject object = new JsonObject();
    object.addProperty("client", client);

    JsonObject data = new JsonObject();
    File path = new File(pathString);
    String id = null;
    if (path.exists()) {
      data.add("data", readPathinfo(path));

      try {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpPost uploadFile = new HttpPost("https://api.wany.io/amethy/terminal/files?o=" + Terminal.KEY);
        MultipartEntityBuilder builder = MultipartEntityBuilder.create();
        builder.addBinaryBody(
            "file",
            new FileInputStream(path),
            ContentType.APPLICATION_OCTET_STREAM,
            path.getName());

        HttpEntity multipart = builder.build();
        uploadFile.setEntity(multipart);
        CloseableHttpResponse response = httpClient.execute(uploadFile);
        HttpEntity responseEntity = response.getEntity();

        if (responseEntity != null) {
          String retSrc = EntityUtils.toString(responseEntity);
          JsonObject res = JsonParser.parseString(retSrc).getAsJsonObject();
          id = res.get("data").getAsString();
        }

        if (id != null) {
          data.addProperty("id", id);
        } else {
          data.addProperty("error", "Error On Upload (ID)");
        }
      } catch (Exception e) {
        e.printStackTrace();
        data.addProperty("error", "Error On Upload");
      }
    } else {
      data.addProperty("error", "File Not Exist");
    }

    object.add("data", data);
    Terminal.event("fs-file-download", object);
  }

  public static void downloadFile(String client, String id, String pathString) {
    JsonObject object = new JsonObject();
    object.addProperty("client", client);

    JsonObject data = new JsonObject();

    File file = new File(pathString);

    try {
      if (file.exists()) {
        file.delete();
      }
      file.getParentFile().mkdirs();
      file.createNewFile();
    } catch (Exception e) {
      data.addProperty("error", "File Make Error");
      return;
    }

    try {
      BufferedInputStream bis = new BufferedInputStream(
          new URL("https://api.wany.io/amethy/terminal/files/" + id + "?o=" + Terminal.KEY).openStream());
      FileOutputStream fis = new FileOutputStream(file);
      byte[] buffer = new byte[1024];
      int count = 0;
      while ((count = bis.read(buffer, 0, 1024)) != -1) {
        fis.write(buffer, 0, count);
      }
      fis.close();
      bis.close();
      HTTPRequest.JSONDelete("https://api.wany.io/amethy/terminal/files/" + id, new JsonObject(), Terminal.KEY);
    } catch (Exception e) {
      data.addProperty("error", "File Download Error");
      return;
    }

    object.add("data", data);
    Terminal.event("fs-file-upload", object);
  }

  public static void deleteFile(String client, String pathString) {
    JsonObject object = new JsonObject();
    object.addProperty("client", client);

    JsonObject data = new JsonObject();
    File path = new File(pathString);
    if (path.exists()) {
      try {
        if (path.isDirectory()) {
          FileUtils.deleteDirectory(path);
        } else {
          FileUtils.delete(path);
        }
      } catch (Exception e) {
        data.addProperty("error", "File Delete Error");
        return;
      }
    } else {
      data.addProperty("error", "File Not Exist");
    }

    object.add("data", data);
    Terminal.event("fs-file-delete", object);
  }

}
