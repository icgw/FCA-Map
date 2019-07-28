/*
 * MappingCell.java
 * Copyright (C) 2019 Guowei Chen <icgw@outlook.com>
 *
 * Distributed under terms of the GPL license.
 */

package cn.amss.semanticweb.alignment;

import java.util.Objects;

import cn.amss.semanticweb.util.Relation;

public class MappingCell extends Relation
{
  private String m_entity1 = "";
  private String m_entity2 = "";

  public MappingCell(String entity1, String entity2, int relation, double confidence) {
    m_entity1    = entity1;
    m_entity2    = entity2;
    m_relation   = relation;

    setMeasure(confidence);
  }

  public MappingCell(String entity1, String entity2, String relation_text, String confidence_text) {
    m_entity1    = entity1;
    m_entity2    = entity2;
    m_relation   = getRelationFromText(relation_text);

    try {
      double d = Double.parseDouble(confidence_text);
      setMeasure(d);
    } catch (Exception e) {
      m_confidence = 0.0f;
    }
  }

  public MappingCell(String entity1, String entity2) {
    this(entity1, entity2, EQUIVALENCE, 1.0f);
  }

  public String getEntity1() {
    return m_entity1;
  }

  public String getEntity2() {
    return m_entity2;
  }

  private final static String entityElement(int n, String entity_uri) {
    return String.format("<entity%d rdf:resource=\"%s\"/>", n, entity_uri);
  }

  public final String getEntityElement1() {
    return entityElement(1, m_entity1);
  }

  public final String getEntityElement2() {
    return entityElement(2, m_entity2);
  }

  private final String getCellElementWithIndent(int indent) {
    StringBuilder cell = new StringBuilder();
    cell.append(getElementWithIndentln("<Cell>", indent));
    cell.append(getElementWithIndentln(getEntityElement1(), indent + 1));
    cell.append(getElementWithIndentln(getEntityElement2(), indent + 1));
    cell.append(getElementWithIndentln(getRelationElement(), indent + 1));
    cell.append(getElementWithIndentln(getMeasureElement(), indent + 1));
    cell.append(getElementWithIndentln("</Cell>", indent));
    return cell.toString();
  }

  private final String getMappingCellElementWithIndent(int indent) {
    StringBuilder mapping_cell = new StringBuilder();
    mapping_cell.append(getElementWithIndentln("<map>", indent));
    mapping_cell.append(getCellElementWithIndent(indent + 1));
    mapping_cell.append(getElementWithIndentln("</map>", indent));
    return mapping_cell.toString();
  }

  public final String getCellElement() {
    return getCellElementWithIndent(m_indent);
  }

  public final String getMappingCellElement() {
    return getMappingCellElementWithIndent(m_indent);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    MappingCell that = (MappingCell) o;

    return m_relation == that.m_relation &&
           m_confidence == that.m_confidence &&
           Objects.equals(m_entity1, that.m_entity1) &&
           Objects.equals(m_entity2, that.m_entity2);
  }

  @Override
  public int hashCode() {
    return Objects.hash(m_entity1, m_entity2, m_relation, m_confidence);
  }
}
