/*
 * Utils.java
 * Copyright (C) 2019 Guowei Chen <icgw@outlook.com>
 *
 * Distributed under terms of the GPL license.
 */

package cn.ac.amss.semanticweb.lang;

public class Utils
{
  private Utils() {
  }

  public static final String escapeXml(String s) {
    return s.replaceAll("&", "&amp;")
            .replaceAll(">", "&gt;")
            .replaceAll("<", "&lt;")
            .replaceAll("\"", "&quot;")
            .replaceAll("'", "&apos;");
  }

  public static final String unescapeXml(String s) {
    return s.replaceAll("&amp;", "&")
            .replaceAll("&gt;", ">")
            .replaceAll("&lt", "<")
            .replaceAll("&quot;", "\"")
            .replaceAll("'", "&apos;");
  }
}
