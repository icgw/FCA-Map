/*
 * Element.java
 * Copyright (C) 2019 Guowei Chen <icgw@outlook.com>
 *
 * Distributed under terms of the GPL license.
 */

package cn.amss.semanticweb.util;

public abstract class Element
{
  private final static String META_ELEMENT_PATTERN = "<%1$s %2$s>%3$s</%1$s>";
  private final static String META_INDENT = "%%%ds%%s%%n";
  private final static int INDENT_SIZE = 2;

  protected static int m_indent = 0;

  public final static void setIndent(int i) {
    m_indent = i;
  }

  protected final static String startElement(String e) {
    return String.format("<%s>", e);
  }

  protected final static String endElement(String e) {
    return String.format("</%s>", e);
  }

  protected final static String getElementFormat(String name, String property, String content_format) {
    if (property == null || property.isEmpty()) {
      return String.format(META_ELEMENT_PATTERN, name, "\b", content_format);
    }
    return String.format(META_ELEMENT_PATTERN, name, property, content_format);
  }

  protected final static String getElementWithIndentln(String element, int indent) {
    if (indent <= 0) {
      return String.format("%s%n", element);
    }

    String format_with_indent = String.format(META_INDENT, indent * INDENT_SIZE);
    return String.format(format_with_indent, "", element);
  }
}
