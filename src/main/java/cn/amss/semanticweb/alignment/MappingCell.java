/*
 * MappingCell.java
 * Copyright (C) 2019 Guowei Chen <icgw@outlook.com>
 *
 * Distributed under terms of the GPL license.
 */

package cn.amss.semanticweb.alignment;

public class MappingCell
{
  // TODO:
  private String m_entity1;
  private String m_entity2;

  public MappingCell(String entity1, String entity2) {
    m_entity1 = entity1;
    m_entity2 = entity2;
  }

  public String getEntity1() {
    return m_entity1;
  }

  public String getEntity2() {
    return m_entity2;
  }
}
