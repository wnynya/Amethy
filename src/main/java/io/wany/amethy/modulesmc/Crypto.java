package io.wany.amethy.modulesmc;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.util.Random;

public class Crypto {

  private static Random random = new Random();

  private String d;
  private String s = "";
  private String k = "";

  public Crypto(String d) {
    this.d = d;
  }

  public Crypto salt(String s) {
    if (s != null) {
      this.s = s;
    }
    return this;
  }

  public Crypto key(String k) {
    if (k != null) {
      this.k = k;
    }
    return this;
  }

  public String hash(HashAlgorithm a) {
    String as = "SHA-512";
    String bi = "%0128x";
    if (a.equals(HashAlgorithm.SHA_256)) {
      as = "SHA-256";
      bi = "%064x";
    } else if (a.equals(HashAlgorithm.SHA_256)) {
      as = "SHA-512";
      bi = "%0128x";
    }
    try {
      MessageDigest digest = MessageDigest.getInstance(as);
      digest.reset();
      digest.update((s + d).getBytes("utf8"));
      return String.format(bi, new BigInteger(1, digest.digest()));
    } catch (Exception e) {
      e.printStackTrace();
      return null;
    }
  }

  public String hash() {
    return this.hash(HashAlgorithm.SHA_512);
  }

  public static int randomNumber(int amp) {
    return random.nextInt(amp + 1);
  }

  public static String randomString(int length, String pool) {
    StringBuilder string = new StringBuilder();
    for (int i = 0; i < length; i++) {
      string.append(pool.charAt(((Double) Math.floor(randomNumber(pool.length() - 1))).intValue()));
    }
    return string.toString();
  }

  public enum HashAlgorithm {
    SHA_512,
    SHA_256
  }

  public enum CipherAlgorithm {
    AES_256_CBC
  }

}
