/*
 * ClassRefinerImpl.java
 * Copyright (C) 2019 Guowei Chen <icgw@outlook.com>
 *
 * Distributed under terms of the GPL license.
 */

package cn.amss.semanticweb.enhancement.impl;

import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import java.util.HashSet;
import java.util.Arrays;

import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.StmtIterator;
import org.apache.jena.vocabulary.RDF;

import cn.amss.semanticweb.enhancement.ClassRefiner;
import cn.amss.semanticweb.enhancement.RefinerByFCA;
import cn.amss.semanticweb.alignment.Mapping;
import cn.amss.semanticweb.alignment.MappingCell;
import cn.amss.semanticweb.fca.Hermes;

public class ClassRefinerImpl extends RefinerByFCA implements ClassRefiner
{
  private Map<Resource, Set<Resource>> m_source_to_targets = null;
  private Map<Resource, Set<Resource>> m_target_to_sources = null;

  public ClassRefinerImpl() {
    m_source_to_targets = new HashMap<>();
    m_target_to_sources = new HashMap<>();
  }

  private final static void add(Map<Resource, Set<Resource>> m, Resource key, Resource value) {
    Set<Resource> v = m.get(key);
    if (v != null) {
      v.add(value);
    } else {
      m.put(key, new HashSet<Resource>(Arrays.asList(value)));
    }
  }

  private static void acquireInstancesFromClass(Resource clazz, Set<Resource> instances) {
    for (StmtIterator stmt = clazz.getModel().listStatements(null, RDF.type, clazz); stmt.hasNext(); ) {
      Resource s = stmt.nextStatement().getSubject();
      instances.add(s);
    }
  }

  private static void acquireInstancesFromMappingCell(MappingCell mc, Set<Resource> instances) {
    acquireInstancesFromClass(mc.getResource1(), instances);
    acquireInstancesFromClass(mc.getResource2(), instances);
  }

  @Override
  public void addInstanceAnchors(Mapping instance_anchors) {
    for (MappingCell mc : instance_anchors) {
      add(m_source_to_targets, mc.getResource1(), mc.getResource2());
      add(m_target_to_sources, mc.getResource2(), mc.getResource1());
    }
  }

  @Override
  public void validateClassAnchors(Mapping class_anchors, Mapping mappings) {
    Map<MappingCell, Set<Resource>> context = new HashMap<>();

    for (MappingCell mc : class_anchors) {
      Set<Resource> attributes = new HashSet<>();
      acquireInstancesFromMappingCell(mc, attributes);
      Set<Resource> inferred_attributes = new HashSet<>(attributes);

      for (Resource a : attributes) {
        Set<Resource> v1 = m_source_to_targets.get(a);
        if (v1 != null) {
          inferred_attributes.addAll(v1);
        }

        Set<Resource> v2 = m_target_to_sources.get(a);
        if (v2 != null) {
          inferred_attributes.addAll(v2);
        }
      }

      context.put(mc, inferred_attributes);
    }

    Hermes<MappingCell, Resource> hermes = new Hermes<>();
    hermes.init(context);
    hermes.compute();

    Set<MappingCell> enhanced_mappings = extractAllObjectInGSHLimit(hermes);
    mappings.addAll(enhanced_mappings);

    hermes.close();
  }
}
