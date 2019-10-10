/*
 * ModelWrapper.java
 * Copyright (C) 2019 Guowei Chen <icgw@outlook.com>
 *
 * Distributed under terms of the GPL license.
 */

package cn.amss.semanticweb.model;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.InfModel;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResIterator;
import org.apache.jena.vocabulary.RDF;
import org.apache.jena.vocabulary.OWL;
import org.apache.jena.vocabulary.SKOS;
import org.apache.jena.util.FileManager;
import org.apache.log4j.Logger;

import java.util.Set;
import java.util.HashSet;
import java.io.InputStream;

import cn.amss.semanticweb.vocabulary.DBkWik;

@Deprecated
public class ModelWrapper
{
  final Logger m_logger = Logger.getLogger(ModelWrapper.class);

  private Model    m_raw_model      = null;

  private boolean  m_inferred       = false;
  private InfModel m_inferred_model = null;

  private Set<Resource> m_instances  = null;
  private Set<Resource> m_properties = null;
  private Set<Resource> m_classes    = null;

  public ModelWrapper() {
    m_raw_model = ModelFactory.createDefaultModel();

    m_instances  = new HashSet<>();
    m_properties = new HashSet<>();
    m_classes    = new HashSet<>();
  }

  public ModelWrapper(String file_path) {
    this();
    read(file_path);
  }

  public ModelWrapper(InputStream in) {
    this();
    read(in);
  }

  private void read(String file_path) {
    InputStream in = FileManager.get().open(file_path);

    if (null == in) {
      throw new IllegalArgumentException( "File: " + file_path + " not found." );
    }

    read(in);
  }

  private void read(InputStream in) {
    if (null == in) return;

    m_instances.clear();
    m_properties.clear();
    m_classes.clear();

    m_raw_model.read(in, null);

    // NOTE: You can change it and add other reasoning rules.
    if (m_inferred) {
      m_inferred_model = ModelFactory.createRDFSModel(m_raw_model);
    }

    // NOTE: acquire instances, properties and classes.
    acquireInstances();
    acquireProperties();
    acquireClasses();

    m_logger.info(String.format("#Instances: %8d, #Properties: %8d, #Classes: %8d.",
                   m_instances.size(), m_properties.size(), m_classes.size()));
  }

  public void close() {
    if (m_raw_model != null && !m_raw_model.isClosed()) {
      m_raw_model.close();
    }

    if (m_inferred_model != null && !m_inferred_model.isClosed()) {
      m_inferred_model.close();
    }

    m_instances.clear();
    m_properties.clear();
    m_classes.clear();
  }

  private Set<Resource> acquireResources(ResIterator it) {
    Set<Resource> resources = new HashSet<>();
    while (it.hasNext()) {
      Resource r = it.nextResource();
      resources.add(r);
    }
    return resources;
  }

  private void acquireInstances() {
    // XXX: remove instances of DBkWik:Image and SKOS:Concept, Template. Under consideration.
    ResIterator it = null;

    if (m_inferred) {
      it = m_inferred_model.listSubjects();
    } else {
      it = m_raw_model.listSubjects();
    }

    while (it.hasNext()) {
      Resource r = it.nextResource();
      if (DBkWik.ownAsResource(r) && !DBkWik.ownAsTemplate(r) &&
          !r.hasProperty(RDF.type, SKOS.Concept) &&
          !r.hasProperty(RDF.type, DBkWik.Image)) {
        m_instances.add(r);
      }
    }
  }

  private void acquireProperties() {
    ResIterator it = null;

    if (m_inferred) {
      it = m_inferred_model.listResourcesWithProperty(RDF.type, RDF.Property);
    } else {
      it = m_raw_model.listResourcesWithProperty(RDF.type, RDF.Property);
    }

    m_properties = acquireResources(it);
  }

  private void acquireClasses() {
    ResIterator it = null;

    if (m_inferred) {
      it = m_inferred_model.listResourcesWithProperty(RDF.type, OWL.Class);
    } else {
      it = m_raw_model.listResourcesWithProperty(RDF.type, OWL.Class);
    }

    m_classes = acquireResources(it);
  }

  public Set<Resource> getInstances() {
    return m_instances;
  }

  public Set<Resource> getProperties() {
    return m_properties;
  }

  public Set<Resource> getClasses() {
    return m_classes;
  }
}
