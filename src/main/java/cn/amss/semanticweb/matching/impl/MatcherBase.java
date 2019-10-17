/*
 * MatcherBase.java
 * Copyright (C) 2019 Guowei Chen <icgw@outlook.com>
 *
 * Distributed under terms of the GPL license.
 */

package cn.amss.semanticweb.matching.impl;

import java.io.InputStream;
import java.util.Map;
import java.util.HashMap;
import java.util.Set;

import cn.amss.semanticweb.alignment.Mapping;
import cn.amss.semanticweb.model.ModelWrapper;
import cn.amss.semanticweb.model.OntModelWrapper;
import cn.amss.semanticweb.model.ResourceWrapper;

import org.apache.jena.rdf.model.Resource;
import org.apache.jena.ontology.Individual;
import org.apache.jena.ontology.OntClass;
import org.apache.jena.ontology.OntProperty;
import org.apache.jena.ontology.DatatypeProperty;
import org.apache.jena.ontology.ObjectProperty;

public abstract class MatcherBase
{
  protected int m_source_id = 0;
  protected int m_target_id = 1;

  protected int m_number_of_ontologies = 2;

  protected Map<OntModelWrapper, Integer> ontology2Id = null;
  protected Map<Integer, OntModelWrapper> id2Ontology = null;

  protected Map<Integer, Set<OntClass>> id2OntClasses                 = null;
  protected Map<Integer, Set<Individual>> id2Instances                = null;
  protected Map<Integer, Set<OntProperty>> id2OntProperties           = null;
  protected Map<Integer, Set<DatatypeProperty>> id2DatatypeProperties = null;
  protected Map<Integer, Set<ObjectProperty>> id2ObjectProperties     = null;

  public MatcherBase() {
    m_source_id = 0;
    m_target_id = 1;

    m_number_of_ontologies = 2;

    ontology2Id = new HashMap<>();
    id2Ontology = new HashMap<>();

    id2OntClasses         = new HashMap<>();
    id2Instances          = new HashMap<>();
    id2OntProperties      = new HashMap<>();
    id2DatatypeProperties = new HashMap<>();
    id2ObjectProperties   = new HashMap<>();
  }

  private void initId2Resources(int id, OntModelWrapper omw) {
    if (omw == null) return;

    id2OntClasses.put(id, omw.getOntClasses());
    id2Instances.put(id, omw.getInstances());
    id2OntProperties.put(id, omw.getOntProperties());
    id2DatatypeProperties.put(id, omw.getDatatypeProperties());
    id2ObjectProperties.put(id, omw.getObjectProperties());
  }

  @Deprecated
  public void setSourceOntModelWrapper(OntModelWrapper source) {
    if (source == null) return;

    ontology2Id.put(source, m_source_id);
    id2Ontology.put(m_source_id, source);

    initId2Resources(m_source_id, source);
  }

  @Deprecated
  public void setTargetOntModelWrapper(OntModelWrapper target) {
    if (target == null) return;

    ontology2Id.put(target, m_target_id);
    id2Ontology.put(m_target_id, target);

    initId2Resources(m_target_id, target);
  }

  private void setOntModelWrapper(OntModelWrapper mw, int id) {
    if (mw == null) return;

    ontology2Id.put(mw, id);
    id2Ontology.put(id, mw);

    initId2Resources(id, mw);
  }

  public void setSourceTargetOntModelWrapper(OntModelWrapper source, OntModelWrapper target) {
    setOntModelWrapper(source, m_source_id);
    setOntModelWrapper(target, m_target_id);
  }

  public void addIntermediateOntModelWrapper(OntModelWrapper intermediate) {
    if (intermediate != null && !ontology2Id.containsKey(intermediate)) {
      ontology2Id.put(intermediate, m_number_of_ontologies);
      id2Ontology.put(m_number_of_ontologies, intermediate);

      initId2Resources(m_number_of_ontologies, intermediate);

      ++m_number_of_ontologies;
    }
  }

  protected final boolean isFromSource(ResourceWrapper<?> rw) {
    return rw.getFromId() == m_source_id;
  }

  protected final boolean isFromTarget(ResourceWrapper<?> rw) {
    return rw.getFromId() == m_target_id;
  }

  protected <T extends Resource> void matchResources(Set<T> sources, Set<T> targets, Mapping mappings) {
    // TODO:
  }

  protected <T extends Resource> void matchResources(Map<Integer, Set<T>> id2Resources, Mapping mappings) {
    // TODO:
  }

  public void close() {
    if (ontology2Id != null || !ontology2Id.isEmpty()) {
      for (OntModelWrapper ont : ontology2Id.keySet()) {
        ont.close();
      }
    }
  }
}
