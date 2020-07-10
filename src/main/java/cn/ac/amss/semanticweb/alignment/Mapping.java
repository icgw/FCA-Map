/*
 * Mapping.java
 * Copyright (C) 2019 Guowei Chen <icgw@outlook.com>
 *
 * Distributed under terms of the GPL license.
 */

package cn.ac.amss.semanticweb.alignment;

import org.apache.jena.ontology.OntModel;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.StmtIterator;

import java.util.Set;
import java.util.HashSet;
import java.util.Objects;
import java.util.Iterator;
import java.util.Map;
import java.util.HashMap;
import java.util.Arrays;

public class Mapping implements Iterable<MappingCell>
{
  private Set<MappingCell> m_mapping = null;

  private OntModel m_source = null;
  private OntModel m_target = null;

  private Map<String, Integer> sourceEntity2Id = null;
  private Map<Integer, String> Id2SourceEntity = null;

  private Map<String, Integer> targetEntity2Id = null;
  private Map<Integer, String> Id2TargetEntity = null;

  private Map<Integer, Set<Integer>> sourceId2Targets = null;
  private Map<Integer, Set<Integer>> targetId2Sources = null;

  public Mapping() {
    m_mapping = new HashSet<>();

    sourceEntity2Id = new HashMap<>();
    Id2SourceEntity = new HashMap<>();

    targetEntity2Id = new HashMap<>();
    Id2TargetEntity = new HashMap<>();

    sourceId2Targets = new HashMap<>();
    targetId2Sources = new HashMap<>();
  }

  public boolean add(MappingCell c) {
    String e1 = c.getEntity1();
    String e2 = c.getEntity2();

    int i1 = sourceEntity2Id.getOrDefault(e1, sourceEntity2Id.size());
    sourceEntity2Id.put(e1, i1);

    int i2 = targetEntity2Id.getOrDefault(e2, targetEntity2Id.size());
    targetEntity2Id.put(e2, i2);

    if (!Id2SourceEntity.containsKey(i1)) Id2SourceEntity.put(i1, e1);

    if (!Id2TargetEntity.containsKey(i2)) Id2TargetEntity.put(i2, e2);

    Set<Integer> targets = sourceId2Targets.get(i1);
    if (targets == null) {
      sourceId2Targets.put(i1, new HashSet<>(Arrays.asList(i2)));
    } else {
      targets.add(i2);
    }

    Set<Integer> sources = targetId2Sources.get(i2);
    if (sources == null) {
      targetId2Sources.put(i2, new HashSet<>(Arrays.asList(i1)));
    } else {
      sources.add(i1);
    }

    return m_mapping.add(c);
  }

  public boolean addAll(Set<MappingCell> m) {
    boolean b = true;
    for (MappingCell mc : m) {
      b = (b && this.add(mc));
    }
    return b;
  }

  public boolean addAll(Mapping m) {
    return this.addAll(m.m_mapping);
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

  public boolean add(String entity1, String entity2, double confidence) {
    return add(new MappingCell(entity1, entity2, confidence));
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

  /**
   * Compute the closure of the mappings
   *
   * @return the closure of the mappings
   */
  public Mapping toClosure() {
    Mapping mClosure = new Mapping();

    Map<Integer, Set<Integer>> source2TargetsClosure = new HashMap<>();
    int n = sourceEntity2Id.size();

    for (int i = 0; i < n; ++i) {
      Set<Integer> sTargets = sourceId2Targets.get(i);
      if (sTargets == null) continue;

      Set<Integer> targets = new HashSet<>();

      Set<Integer> sources = new HashSet<>();
      for (int  t : sTargets) {
        Set<Integer> ss = targetId2Sources.get(t);
        if (ss == null) continue;
        sources.addAll(ss);
      }

      for (int s : sources) {
        Set<Integer> ts = sourceId2Targets.get(s);
        if (ts == null) continue;
        targets.addAll(ts);
      }

      source2TargetsClosure.put(i, targets);
    }

    for (Map.Entry<Integer, Set<Integer>> e : source2TargetsClosure.entrySet()) {
      int sid = e.getKey();
      for (int tid : e.getValue()) {
        String e1 = Id2SourceEntity.get(sid);
        String e2 = Id2TargetEntity.get(tid);
        if (e1 == null || e2 == null) continue;
        mClosure.add(e1, e2);
      }
    }

    return mClosure;
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
