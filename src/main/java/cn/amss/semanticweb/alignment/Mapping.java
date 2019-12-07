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

import org.apache.jena.ontology.OntModel;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.StmtIterator;

public class Mapping implements Iterable<MappingCell>
{
  private Set<MappingCell> m_mapping = null;

  private OntModel m_source = null;
  private OntModel m_target = null;

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

  public void setOntSourceTarget(OntModel source, OntModel target) {
    m_source = source;
    m_target = target;
  }

  public OntModel getSource() {
    return m_source;
  }

  public OntModel getTarget() {
    return m_target;
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

  public String listMappingCellSPO(MappingCell mc) {
    if (mc == null || m_source == null || m_target == null) return "null";

    StringBuilder sb = new StringBuilder();
    Resource r1 = m_source.getResource(mc.getEntity1());
    sb.append(String.format("%n<<<<<<<"));
    sb.append(String.format("%n* %s%n", r1.getURI()));
    for (StmtIterator stmt = r1.listProperties(); stmt.hasNext(); ) {
      sb.append(String.format("** %s%n", stmt.nextStatement()));
    }

    sb.append(String.format("======="));

    Resource r2 = m_target.getResource(mc.getEntity2());
    sb.append(String.format("%n* %s%n", r2.getURI()));
    for (StmtIterator stmt = r2.listProperties(); stmt.hasNext(); ) {
      sb.append(String.format("** %s%n", stmt.nextStatement()));
    }
    sb.append(String.format(">>>>>>>%n"));

    return sb.toString();
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
