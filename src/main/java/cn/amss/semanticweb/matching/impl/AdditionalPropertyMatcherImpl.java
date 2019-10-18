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
import org.apache.jena.vocabulary.RDF;

import java.util.Set;
import java.util.HashSet;
import java.util.Map;
import java.util.HashMap;
import java.util.Arrays;

import cn.amss.semanticweb.fca.Hermes;
import cn.amss.semanticweb.model.OntModelWrapper;
import cn.amss.semanticweb.model.ResourceWrapper;
import cn.amss.semanticweb.util.Pair;
import cn.amss.semanticweb.alignment.Mapping;
import cn.amss.semanticweb.alignment.MappingCell;
import cn.amss.semanticweb.matching.MatcherByFCA;
import cn.amss.semanticweb.matching.AdditionalPropertyMatcher;

public class AdditionalPropertyMatcherImpl extends MatcherByFCA implements AdditionalPropertyMatcher
{
  private Mapping m_instance_anchors = null;
  private Mapping m_property_anchors = null;
  private Mapping m_class_anchors    = null;

  private Map<Resource, Set<MappingCell>> m_resource_to_map_cell = null;

  private static boolean b_source_duplicate_free = true;
  private static boolean b_target_duplicate_free = true;

  private OntModelWrapper m_source = null;
  private OntModelWrapper m_target = null;

  private static class SubjectObject extends Pair<MappingCell, MappingCell> {
    public SubjectObject(MappingCell subject_align, MappingCell object_align) {
      super(subject_align, object_align);
    }
  }

  public AdditionalPropertyMatcherImpl() {
    m_instance_anchors     = new Mapping();
    m_property_anchors     = new Mapping();
    m_class_anchors        = new Mapping();
    m_resource_to_map_cell = new HashMap<>();
  }

  private static final <O, A> void add(Map<O, Set<A>> context, O k, A v) {
    Set<A> s = context.get(k);
    if (s != null) {
      s.add(v);
    } else {
      context.put(k, new HashSet<A>(Arrays.asList(v)));
    }
  }

  private void addHashAnchors(Mapping anchors) {
    if (anchors == null || anchors.isEmpty()) return;

    for (MappingCell c : anchors) {
      Resource r1 = c.getResource1();
      Resource r2 = c.getResource2();

      add(m_resource_to_map_cell, r1, c);
      add(m_resource_to_map_cell, r2, c);
    }
  }


  private static final void deriveClassMappingsFromResource(Resource r, Map<Resource, Set<MappingCell>> m, Set<MappingCell> s) {
    for (StmtIterator it = r.listProperties(RDF.type); it.hasNext(); ) {
      Statement stmt = it.nextStatement();
      if (stmt.getObject().isResource()) {
        Set<MappingCell> v = m.get(stmt.getObject());
        if (v != null) {
          s.addAll(s);
        }
      }
    }
  }

  private static final <T extends Resource> void addContextFromSO(Map<ResourceWrapper<T>, Set<SubjectObject>> context,
                                                                  ResourceWrapper<T> p,
                                                                  Set<MappingCell> subject_mappings,
                                                                  Set<MappingCell> object_mappings) {
    if (subject_mappings == null || object_mappings == null) return;
    for (MappingCell s : subject_mappings) {
      for (MappingCell o : object_mappings) {
        add(context, p, new SubjectObject(s, o));
      }
    }
  }

  private static final <T extends Resource> void addContextFromSO(Map<ResourceWrapper<T>, Set<SubjectObject>> context,
                                                                  ResourceWrapper<T> p,
                                                                  Map<Resource, Set<MappingCell>> m,
                                                                  Resource subject,
                                                                  Resource object) {
    Set<MappingCell> subject_instance_mappings = m.get(subject);
    Set<MappingCell> object_instance_mappings = m.get(object);
    addContextFromSO(context, p, subject_instance_mappings, object_instance_mappings);

    Set<MappingCell> subject_class_mappings = new HashSet<MappingCell>();
    deriveClassMappingsFromResource(subject, m, subject_class_mappings);

    Set<MappingCell> object_class_mappings = new HashSet<MappingCell>();
    deriveClassMappingsFromResource(object, m, object_class_mappings);
    addContextFromSO(context, p, subject_class_mappings, object_class_mappings);
  }

  private <T extends Resource> void addContext(Set<T> properties,
                                               int from_id,
                                               Map<Resource, Set<MappingCell>> m,
                                               Map<ResourceWrapper<T>, Set<SubjectObject>> context) {
    for (final T p : properties) {

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

        ResourceWrapper<T> rw = new ResourceWrapper<T>(p, from_id);
        addContextFromSO(context, rw, m, subject, object);
      }
    }
  }

  private <T extends Resource> Map<ResourceWrapper<T>, Set<SubjectObject>> constructContext(Set<T> sources,
                                                                                            Set<T> targets,
                                                                                            Mapping property_anchors) {
    Map<ResourceWrapper<T>, Set<SubjectObject>> context = new HashMap<>();

    addContext(sources, m_source_id, m_resource_to_map_cell, context);
    addContext(targets, m_target_id, m_resource_to_map_cell, context);

    if (property_anchors == null) return context;

    for (MappingCell mc : property_anchors) {
      Set<SubjectObject> v1 = context.get(new ResourceWrapper<Resource>(mc.getResource1(), m_source_id));
      Set<SubjectObject> v2 = context.get(new ResourceWrapper<Resource>(mc.getResource2(), m_target_id));
      if (v1 != null && v2 != null) {
        v1.addAll(v2);
        v2.addAll(v1);
      }
    }

    return context;
  }

  private <T extends Resource> void splitResource(Set<ResourceWrapper<T>> rws, Set<T> source, Set<T> target) {
    if (rws == null || source == null || target == null) return;

    for (ResourceWrapper<T> rw : rws) {
      if (isFromSource(rw)) {
        source.add(rw.getResource());
      }
      else if (isFromTarget(rw)) {
        target.add(rw.getResource());
      }
    }
  }

  private <T extends Resource> void extractMapping(Set<Set<ResourceWrapper<T>>> cluster, Mapping mappings) {
    Set<T> source = new HashSet<>();
    Set<T> target = new HashSet<>();
    for (Set<ResourceWrapper<T>> rws : cluster) {
      source.clear();
      target.clear();

      splitResource(rws, source, target);

      for (T s : source) {
        for (T t : target) {
          mappings.add(s, t);
        }
      }
    }
  }

  private void clear() {
    if(null != m_instance_anchors) {
      m_instance_anchors.clear();
    }

    if (null != m_property_anchors) {
      m_property_anchors.clear();
    }

    if (null != m_class_anchors) {
      m_class_anchors.clear();
    }

    if (null != m_resource_to_map_cell) {
      m_resource_to_map_cell.clear();
    }
  }

  @Override
  public void setSourceTargetOntModelWrapper(OntModelWrapper source, OntModelWrapper target) {
    m_source = source;
    m_target = target;
  }

  @Override
  public void setDuplicateFree(boolean b_duplicate_free1, boolean b_duplicate_free2) {
    b_source_duplicate_free = b_duplicate_free1;
    b_target_duplicate_free = b_duplicate_free2;
  }

  @Override
  public <T extends Resource> void matchProperties(Set<T> sources, Set<T> targets, Mapping mappings) {
    Map<ResourceWrapper<T>, Set<SubjectObject>> context = constructContext(sources, targets, m_property_anchors);

    Hermes<ResourceWrapper<T>, SubjectObject> hermes = new Hermes<>();
    hermes.init(context);
    hermes.compute();

    Set<Set<ResourceWrapper<T>>> simplified_extents = null, extents = null;
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

  private <T extends Resource> void mapAdditionalProperties(Set<T> sources, Set<T> targets, Mapping mappings) {
    Map<ResourceWrapper<T>, Set<SubjectObject>> context = constructContext(sources, targets, m_property_anchors);

    Hermes<ResourceWrapper<T>, SubjectObject> hermes = new Hermes<>();
    hermes.init(context);
    hermes.compute();

    Set<Set<ResourceWrapper<T>>> simplified_extents = null, extents = null;
    if (extract_from_GSH) {
      simplified_extents = extractExtentsFromGSH(hermes);
    }

    if (extract_from_Lattice) {
      extents = extractExtentsFromLattice(hermes);
    }

    Mapping candidates = new Mapping();
    if (simplified_extents != null) {
      extractMapping(simplified_extents, candidates);
    }

    if (extents != null) {
      extractMapping(extents, candidates);
    }

    if (m_property_anchors != null && !m_property_anchors.isEmpty() &&
        (b_source_duplicate_free || b_target_duplicate_free)) {
      Set<String> source_uris = new HashSet<>();
      Set<String> target_uris = new HashSet<>();

      for (MappingCell mc : m_property_anchors) {
        if (b_source_duplicate_free) {
          source_uris.add(mc.getEntity1());
        }

        if (b_target_duplicate_free) {
          target_uris.add(mc.getEntity2());
        }
      }

      for (MappingCell mc : candidates) {
        String entity1 = mc.getEntity1();
        String entity2 = mc.getEntity2();
        if ((b_source_duplicate_free && source_uris.contains(entity1)) ||
            (b_target_duplicate_free && target_uris.contains(entity2))) continue;
        mappings.add(mc);
      }
    } else {
      mappings.addAll(candidates);
    }

    hermes.close();
  }

  @Override
  public void mapOntProperties(Mapping mappings) {
    mapAdditionalProperties(m_source.getOntProperties(), m_target.getOntProperties(), mappings);
  }

  @Override
  public void mapDatatypeProperties(Mapping mappings) {
    mapAdditionalProperties(m_source.getDatatypeProperties(), m_target.getDatatypeProperties(), mappings);
  }

  @Override
  public void mapObjectProperties(Mapping mappings) {
    mapAdditionalProperties(m_source.getObjectProperties(), m_target.getObjectProperties(), mappings);
  }

  @Override
  public boolean addInstanceAnchors(Mapping instance_anchors) {
    boolean b = m_instance_anchors.addAll(instance_anchors);
    if (b) {
      addHashAnchors(instance_anchors);
    }
    return b;
  }

  @Override
  public boolean addPropertyAnchors(Mapping property_anchors) {
    return m_property_anchors.addAll(property_anchors);
  }

  @Override
  public boolean addClassAnchors(Mapping class_anchors) {
    boolean b = m_class_anchors.addAll(class_anchors);
    if (b) {
      addHashAnchors(class_anchors);
    }
    return b;
  }
}
