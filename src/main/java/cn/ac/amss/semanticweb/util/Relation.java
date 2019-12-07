/*
 * Relation.java
 * Copyright (C) 2019 Guowei Chen <icgw@outlook.com>
 *
 * Distributed under terms of the GPL license.
 */

package cn.ac.amss.semanticweb.util;

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

  protected final void setMeasure(double confidence) {
    if (confidence > 1.0f) {
      m_confidence = 1.0f;
    }
    else if (confidence < 0.0f) {
      m_confidence = 0.0f;
    } else {
      m_confidence = confidence;
    }
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

  public final int getRelationFromText(String r) {
    if (r == null || r.isEmpty()) return -1;

    if (r.equals("=")) {
      return EQUIVALENCE;
    }
    else if (r.equals("&lt;")) {
      return SUBSUMES_BY;
    }
    else if (r.equals("&gt;")) {
      return SUBSUMES;
    }
    else if (r.equals("?")) {
      return -1;
    }

    return -1;
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
