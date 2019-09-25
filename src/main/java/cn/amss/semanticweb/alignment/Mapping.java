/*
 * Mapping.java
 * Copyright (C) 2019 Guowei Chen <icgw@outlook.com>
 *
 * Distributed under terms of the GPL license.
 */

package cn.amss.semanticweb.alignment;

import java.util.Set;
import java.util.HashSet;
import java.util.Objects;
import java.util.Iterator;

import org.apache.jena.rdf.model.Resource;

public class Mapping implements Iterable<MappingCell>
{
  private Set<MappingCell> m_mapping = null;

  public Mapping() {
    m_mapping = new HashSet<>();
  }

  public boolean add(MappingCell c) {
    return m_mapping.add(c);
  }

  public boolean addAll(Mapping m) {
    return m_mapping.addAll(m.m_mapping);
  }

  public boolean addAll(Set<MappingCell> m) {
    return m_mapping.addAll(m);
  }

  public boolean removeAll(Mapping m) {
    return m_mapping.removeAll(m.m_mapping);
  }

  public boolean removeAll(Set<MappingCell> m) {
    return m_mapping.removeAll(m);
  }

  public boolean add(String entity1, String entity2) {
    return add(new MappingCell(entity1, entity2));
  }

  public boolean add(Resource resource1, Resource resource2) {
    return add(new MappingCell(resource1, resource2));
  }

  public boolean add(String entity1, String entity2, String relation, String confidence) {
    return add(new MappingCell(entity1, entity2, relation, confidence));
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

  public final void clear() {
    m_mapping.clear();
  }

  public boolean isEmpty() {
    return m_mapping == null || m_mapping.isEmpty();
  }

  @Override
  public Iterator<MappingCell> iterator() {
    return m_mapping.iterator();
  }

  @Override
  public String toString() {
    return getContent(0);
  }

  @Override
  public int hashCode() {
    return Objects.hash(m_mapping);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    Mapping that = (Mapping) o;
    return Objects.equals(m_mapping, that.m_mapping);
  }
}
