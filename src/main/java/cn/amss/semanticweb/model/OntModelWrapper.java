/*
 * OntModelWrapper.java
 * Copyright (C) 2019 Guowei Chen <icgw@outlook.com>
 *
 * Distributed under terms of the GPL license.
 */

package cn.amss.semanticweb.model;

import java.util.Set;
import java.util.HashSet;
import java.io.InputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.jena.vocabulary.RDF;
import org.apache.jena.vocabulary.SKOS;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.ontology.OntClass;
import org.apache.jena.ontology.Individual;
import org.apache.jena.ontology.OntProperty;
import org.apache.jena.ontology.ObjectProperty;
import org.apache.jena.ontology.DatatypeProperty;
import org.apache.jena.ontology.OntModelSpec;
import org.apache.jena.util.FileManager;
import org.apache.jena.util.iterator.ExtendedIterator;

import cn.amss.semanticweb.vocabulary.DBkWik;

/**
 * The wrapper of ontology model, which store the instances, properties and classes.
 *
 * @author Guowei Chen (icgw@outlook.com)
 */
public class OntModelWrapper
{
  final static Logger m_logger = LoggerFactory.getLogger(OntModelWrapper.class);

  private Model m_raw_model   = null;
  private OntModel m_ontology = null;

  private Set<Individual> m_instances   = null;

  private Set<OntProperty> m_properties               = null;
  private Set<DatatypeProperty> m_datatype_properties = null;
  private Set<ObjectProperty> m_object_properties     = null;

  private Set<OntClass> m_classes       = null;

  /**
   * Initial member variables
   */
  public OntModelWrapper() {
    m_raw_model = ModelFactory.createDefaultModel();

    m_instances           = new HashSet<>();
    m_properties          = new HashSet<>();
    m_datatype_properties = new HashSet<>();
    m_object_properties   = new HashSet<>();
    m_classes             = new HashSet<>();
  }

  /**
   * Initial ontology model from input stream
   *
   * @param in the input stream of the ontology
   */
  public OntModelWrapper(InputStream in) {
    this();
    read(in);
  }

  /**
   * Initial ontology model from an ontology file
   *
   * @param file the file path or url of the ontology
   */
  public OntModelWrapper(String file) {
    this();
    InputStream in = FileManager.get().open(file);

    if (null == in) {
      throw new IllegalArgumentException( "File: " + file + " not found.");
    }
    read(in);
  }

  /**
   * Read model from input stream and acquired the specified instances, properties and classes
   *
   * @param in input stream of model
   */
  private void read(InputStream in) {
    if (null == in) return;

    clear();

    m_raw_model.read(in, null);
    m_ontology = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM, m_raw_model);

    acquireInstances();
    acquireProperties();

    // XXX: wait to improve
    acquireObjectProperties();
    acquireDatatypeProperties();
    ////////////////////////////

    acquireClasses();

    if (m_logger.isInfoEnabled()) {
      m_logger.info("#Instances: {}, #Properties: {}, #DatatypeProperties: {}, #ObjectProperties: {}, #Classes: {}.",
                    m_instances.size(), m_properties.size(), m_datatype_properties.size(), m_object_properties.size(), m_classes.size());
    }
  }

  /**
   * Check whether the individual should be skipped
   *
   * @param i individual
   * @return true means the individual should be ignored
   */
  private static final boolean isSkipInstance(Individual i) {
    return i.hasProperty(RDF.type, SKOS.Concept) ||
           i.hasProperty(RDF.type, DBkWik.Image);
  }

  /**
   * Check whether the property should be skipped
   *
   * @param p property
   * @return true means the property should be ignored
   */
  private static final boolean isSkipProperty(OntProperty p) {
    return false;
  }

  /**
   * Check whether the class should be skipped
   *
   * @param c class
   * @return true means the class should be ignored
   */
  private static final boolean isSkipClass(OntClass c) {
    return c.isAnon();
  }

  /**
   * Acquire the instances and skip the ignored one
   */
  private void acquireInstances() {
    for (ExtendedIterator<Individual> it = m_ontology.listIndividuals(); it.hasNext(); ) {
      Individual i = it.next();
      if (isSkipInstance(i)) continue;
      m_instances.add(i);
    }
  }

  /**
   * Acquire all ontology properties and skip the ignored one
   */
  private void acquireProperties() {
    for (ExtendedIterator<OntProperty> it = m_ontology.listAllOntProperties(); it.hasNext(); ) {
      OntProperty p = it.next();
      if (isSkipProperty(p)) continue;
      m_properties.add(p);
    }
  }

  /**
   * Acquire datatype properties and skip the ignored one
   */
  private void acquireDatatypeProperties() {
    for (ExtendedIterator<DatatypeProperty> it = m_ontology.listDatatypeProperties(); it.hasNext(); ) {
      DatatypeProperty p = it.next();
      if (isSkipProperty(p)) continue;
      m_datatype_properties.add(p);
    }
  }

  /**
   * Acquire object properties and skip the ignored one
   */
  private void acquireObjectProperties() {
    for (ExtendedIterator<ObjectProperty> it = m_ontology.listObjectProperties(); it.hasNext(); ) {
      ObjectProperty p = it.next();
      if (isSkipProperty(p)) continue;
      m_object_properties.add(p);
    }
  }

  /**
   * Acquire the classes and skip the ignored one
   */
  private void acquireClasses() {
    for (ExtendedIterator<OntClass> it = m_ontology.listClasses(); it.hasNext(); ) {
      OntClass c = it.next();
      if (isSkipClass(c)) continue;
      m_classes.add(c);
    }
  }

  /**
   * Clear the instances, properties and classes in the model wrapper
   */
  private final void clear() {
    m_instances.clear();
    m_properties.clear();
    m_datatype_properties.clear();
    m_object_properties.clear();
    m_classes.clear();
  }

  /**
   * Close the model wrapper
   */
  public final void close() {
    if (m_raw_model != null && !m_raw_model.isClosed()) {
      m_raw_model.close();
    }

    if (m_ontology != null && !m_ontology.isClosed()) {
      m_ontology.close();
    }

    clear();
  }

  /**
   * Get the hash set of instances
   *
   * @return m_instances
   */
  public Set<Individual> getInstances() {
    return m_instances;
  }

  /**
   * Get the hash set of properties
   *
   * @return m_properties
   */
  public Set<OntProperty> getProperties() {
    return m_properties;
  }

  /**
   * Get the hash set of classes
   *
   * @return m_classes
   */
  public Set<OntClass> getClasses() {
    return m_classes;
  }
}
