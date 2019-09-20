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

import cn.amss.semanticweb.model.ModelWrapper;
import cn.amss.semanticweb.model.OntModelWrapper;
import cn.amss.semanticweb.model.ResourceWrapper;

public abstract class MatcherBase
{
  protected int m_source_id = 0;
  protected int m_target_id = 1;

  protected int m_number_of_ontologies = 2;

  protected ModelWrapper m_source = null;
  protected ModelWrapper m_target = null;

  protected Map<OntModelWrapper, Integer> ontology2Id = null;
  protected Map<Integer, OntModelWrapper> id2Ontology = null;

  public MatcherBase() {
    m_source_id = 0;
    m_target_id = 1;

    m_number_of_ontologies = 2;

    ontology2Id = new HashMap<>();
    id2Ontology = new HashMap<>();
  }

  public void setSourceOntModelWrapper(OntModelWrapper source) {
    ontology2Id.put(source, m_source_id);
    id2Ontology.put(m_source_id, source);
  }

  public void setTargetOntModelWrapper(OntModelWrapper target) {
    ontology2Id.put(target, m_target_id);
    id2Ontology.put(m_target_id, target);
  }

  public void addIntermediateOntModelWrapper(OntModelWrapper intermediate) {
    if (!ontology2Id.containsKey(intermediate)) {
      ontology2Id.put(intermediate, m_number_of_ontologies);
      id2Ontology.put(m_number_of_ontologies, intermediate);
      ++m_number_of_ontologies;
    }
  }

  protected final boolean isFromSource(ResourceWrapper<?> rw) {
    return rw.getFromId() == m_source_id;
  }

  protected final boolean isFromTarget(ResourceWrapper<?> rw) {
    return rw.getFromId() == m_target_id;
  }

  @Deprecated
  public void init(InputStream source, InputStream target) {
    m_source = new ModelWrapper(source);
    m_target = new ModelWrapper(target);
  }

  @Deprecated
  public void init(String source, String target) {
    m_source = new ModelWrapper(source);
    m_target = new ModelWrapper(target);
  }

  @Deprecated
  public void setSourceId(int source_id) {
    m_source_id = source_id;
  }

  @Deprecated
  public void setTargetId(int target_id) {
    m_target_id = target_id;
  }

  public void close() {
    if (ontology2Id != null || !ontology2Id.isEmpty()) {
      for (OntModelWrapper ont : ontology2Id.keySet()) {
        ont.close();
      }
    }
  }
}
