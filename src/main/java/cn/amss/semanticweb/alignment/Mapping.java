/*
 * Mapping.java
 * Copyright (C) 2019 Guowei Chen <icgw@outlook.com>
 *
 * Distributed under terms of the GPL license.
 */

package cn.amss.semanticweb.alignment;

import java.util.Set;
import java.util.HashSet;

import cn.amss.semanticweb.alignment.MappingCell;

public class Mapping
{
  private Set<MappingCell> m_mapping = null;

  public Mapping() {
    m_mapping = new HashSet<>();
  }

  public boolean add(MappingCell c) {
    return m_mapping.add(c);
  }

  public boolean add(String entity1, String entity2) {
    return add(new MappingCell(entity1, entity2));
  }

  public int size() {
    return m_mapping.size();
  }

  public final String getContent(int indent) {
    MappingCell.setIndent(indent);
    if (m_mapping == null) return "";

    StringBuilder content = new StringBuilder();
    for (MappingCell m : m_mapping) {
      content.append(m.getMappingCellElement());
    }
    return content.toString();
  }

  @Override
  public String toString() {
    return getContent(0);
  }
}
