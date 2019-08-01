/*
 * AdditionalPropertyMatcherImpl.java
 * Copyright (C) 2019 Guowei Chen <icgw@outlook.com>
 *
 * Distributed under terms of the GPL license.
 */

package cn.amss.semanticweb.matching.impl;

import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;
import org.apache.jena.rdf.model.SimpleSelector;

import java.util.Set;
import java.util.HashSet;
import java.util.Map;
import java.util.HashMap;
import java.util.Iterator;

import cn.amss.semanticweb.fca.Hermes;
import cn.amss.semanticweb.model.ResourceWrapper;
import cn.amss.semanticweb.util.Pair;
import cn.amss.semanticweb.alignment.Mapping;
import cn.amss.semanticweb.alignment.MappingCell;
import cn.amss.semanticweb.matching.MatcherByFCA;
import cn.amss.semanticweb.matching.AdditionalPropertyMatcher;

public class AdditionalPropertyMatcherImpl extends MatcherByFCA implements AdditionalPropertyMatcher
{
  private Mapping m_instance_anchors = null;

  private Map<Resource, Set<MappingCell>> m_resource_to_map_cell = null;

  private class SubjectObject extends Pair<MappingCell, MappingCell> {
    public SubjectObject(MappingCell subject_align, MappingCell object_align) {
      super(subject_align, object_align);
    }
  }

  public AdditionalPropertyMatcherImpl() {
    m_instance_anchors     = new Mapping();
    m_resource_to_map_cell = new HashMap<>();
  }

  private void initHashAnchors(Mapping anchors) {
    if (anchors == null || anchors.isEmpty()) return;

    for (Iterator<MappingCell> it = anchors.iterator(); it.hasNext(); ) {
      MappingCell c = it.next();

      Resource r1 = c.getResource1();
      Resource r2 = c.getResource2();

      m_resource_to_map_cell.putIfAbsent(r1, new HashSet<MappingCell>());
      m_resource_to_map_cell.putIfAbsent(r2, new HashSet<MappingCell>());

      m_resource_to_map_cell.get(r1).add(c);
      m_resource_to_map_cell.get(r2).add(c);
    }
  }

  private void addContext(Set<Resource> properties,
                          int from_id,
                          Map<Resource, Set<MappingCell>> m,
                          Map<ResourceWrapper, Set<SubjectObject>> context) {
    for (Resource p : properties) {

      StmtIterator it = p.getModel().listStatements(
            new SimpleSelector(null, null, (RDFNode) null) {
              @Override
              public boolean selects(Statement s) {
                return s.getPredicate().getURI().equals(p.getURI()) && s.getObject().isResource();
              }
            }
          );

      while (it.hasNext()) {
        Statement stmt = it.nextStatement();

        Resource subject = stmt.getSubject();
        Resource object  = stmt.getObject().asResource();

        Set<MappingCell> subject_mappings = m.get(subject);
        Set<MappingCell> object_mappings  = m.get(object);

        if (subject_mappings != null && object_mappings != null) {
          for (MappingCell s : subject_mappings) {
            for (MappingCell o : object_mappings) {
              ResourceWrapper rw = new ResourceWrapper(p, from_id);
              context.putIfAbsent(rw, new HashSet<SubjectObject>());
              context.get(rw).add(new SubjectObject(s, o));
            }
          }
        }
      }
    }
  }

  private Map<ResourceWrapper, Set<SubjectObject>> constructContext(Set<Resource> sources, Set<Resource> targets) {
    Map<ResourceWrapper, Set<SubjectObject>> context = new HashMap<>();

    addContext(sources, m_source_id, m_resource_to_map_cell, context);
    addContext(targets, m_target_id, m_resource_to_map_cell, context);

    return context;
  }

  private void splitResource(Set<ResourceWrapper> rws, Set<Resource> source, Set<Resource> target) {
    if (rws == null || source == null || target == null) return;

    for (ResourceWrapper rw : rws) {
      if (isFromSource(rw)) {
        source.add(rw.getResource());
      }
      else if (isFromTarget(rw)) {
        target.add(rw.getResource());
      }
    }
  }

  private void extractMapping(Set<Set<ResourceWrapper>> cluster, Mapping mappings) {
    Set<Resource> source = new HashSet<>();
    Set<Resource> target = new HashSet<>();
    for (Set<ResourceWrapper> rws : cluster) {
      source.clear();
      target.clear();

      splitResource(rws, source, target);

      for (Resource s : source) {
        for (Resource t : target) {
          mappings.add(s, t);
        }
      }
    }
  }

  @Override
  public void matchProperties(Set<Resource> sources, Set<Resource> targets, Mapping mappings) {
    initHashAnchors(m_instance_anchors);
    Map<ResourceWrapper, Set<SubjectObject>> context = constructContext(sources, targets);

    Hermes<ResourceWrapper, SubjectObject> hermes = new Hermes<>();
    hermes.init(context);
    hermes.compute();

    Set<Set<ResourceWrapper>> simplified_extents = null, extents = null;
    if (extract_from_GSH) {
      simplified_extents = extractExtentsFromGSH(hermes);
    }

    if (extract_from_Lattice) {
      extents = extractExtentsFromLattice(hermes);
    }

    if (simplified_extents != null) {
      extractMapping(simplified_extents, mappings);
    }

    if (extents != null) {
      extractMapping(extents, mappings);
    }

    hermes.close();
  }

  @Override
  public boolean addInstanceAnchors(Mapping instance_anchors) {
    boolean b = m_instance_anchors.addAll(instance_anchors);
    if (true == b) {
      initHashAnchors(instance_anchors);
    }
    return b;
  }

  @Override
  public void clear() {
    m_instance_anchors.clear();
    m_resource_to_map_cell.clear();
  }

  public void matchProperties(Set<Resource> sources, Set<Resource> targets, Mapping instance_anchors, Mapping mappings) {
    clear();
    addInstanceAnchors(instance_anchors);
    matchProperties(sources, targets, mappings);
  }
}
