/*
 * Relation.java
 * Copyright (C) 2019 Guowei Chen <icgw@outlook.com>
 *
 * Distributed under terms of the GPL license.
 */

package cn.amss.semanticweb.util;

import cn.amss.semanticweb.util.Element;

public abstract class Relation extends Element
{
  protected final static int EQUIVALENCE = 0;
  protected final static int SUBSUMES    = 1;
  protected final static int SUBSUMES_BY = 2;

  protected int m_relation      = -1;
  protected double m_confidence = 1.0f;

  public final int getRelation() {
    return m_relation;
  }

  public final double getMeasure() {
    return m_confidence;
  }

  public final boolean isEQ() {
    return m_relation == EQUIVALENCE;
  }

  public final boolean isLT() {
    return m_relation == SUBSUMES_BY;
  }

  public final boolean isGT() {
    return m_relation == SUBSUMES;
  }

  private final static String relationElement(String e) {
    return String.format(getElementFormat("relation", "", "%s"), e);
  }

  private final static String measureElement(double m) {
    return String.format(getElementFormat("measure", "rdf:datatype=\"xsd:float\"", "%.1f"), m);
  }

  public final String getRelationElement() {
    if (isEQ()) {
      return relationElement("=");
    }
    else if (isLT()) {
      return relationElement("&lt;");
    }
    else if (isGT()) {
      return relationElement("&gt;");
    }
    return relationElement("?");
  }

  public final String getMeasureElement() {
    return measureElement(m_confidence);
  }
}
