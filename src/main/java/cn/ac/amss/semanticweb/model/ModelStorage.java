/*
 * ModelStorage.java
 * Copyright (C) 2019 Guowei Chen <icgw@outlook.com>
 *
 * Distributed under terms of the GPL license.
 */

package cn.ac.amss.semanticweb.model;

import cn.ac.amss.semanticweb.vocabulary.DBkWik;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResIterator;
import org.apache.jena.vocabulary.RDF;
import org.apache.jena.vocabulary.SKOS;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.ontology.Individual;
import org.apache.jena.ontology.OntProperty;
import org.apache.jena.ontology.DatatypeProperty;
import org.apache.jena.ontology.ObjectProperty;
import org.apache.jena.ontology.OntClass;
import org.apache.jena.ontology.OntModelSpec;
import org.apache.jena.util.FileManager;
import org.apache.jena.util.iterator.ExtendedIterator;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import java.io.InputStream;
import java.util.Set;
import java.util.HashSet;

/**
 * The wrapping class storage of jena model
 *
 * @author Guowei Chen (icgw@outlook.com)
 */
public class ModelStorage {
  private final static Logger logger = LogManager.getLogger(ModelStorage.class.getName());
  private final static String infoFormat =
    "Filename: %s%n" +
    "# Instances: %8d, " +
    "# Categories: %8d, " +
    "# OntProperties: %8d, " +
    "# DataTypeProperties: %8d, " +
    "# ObjectProperties: %8d, " +
    "# OntClasses: %8d.";

  private String filenameOrURI = "";

  private Model model       = null;
  private OntModel ontModel = null;

  private Set<Individual> individuals = null;

  private Set<Resource> categories = null;

  private Set<OntProperty> ontProperties           = null;
  private Set<DatatypeProperty> dataTypeProperties = null;
  private Set<ObjectProperty> objectProperties     = null;

  private Set<OntClass> ontClasses = null;

  private ModelStorage() {
    individuals        = new HashSet<>();
    categories         = new HashSet<>();
    ontProperties      = new HashSet<>();
    dataTypeProperties = new HashSet<>();
    objectProperties   = new HashSet<>();
    ontClasses         = new HashSet<>();
  }

  public ModelStorage(String filenameOrURI) {
    this();
    this.filenameOrURI = filenameOrURI;

    read(filenameOrURI);
  }

  public Model getModel() {
    return model;
  }

  public OntModel getOntModel() {
    return ontModel;
  }

  public Set<Individual> getIndividuals() {
    return individuals;
  }

  public Set<Resource> getCategories() {
    return categories;
  }

  public Set<OntProperty> getOntProperties() {
    return ontProperties;
  }

  public Set<DatatypeProperty> getDataTypeProperties() {
    return dataTypeProperties;
  }

  public Set<ObjectProperty> getObjectProperties() {
    return objectProperties;
  }

  public Set<OntClass> getOntClasses() {
    return ontClasses;
  }

  public void clear() {
    individuals.clear();
    categories.clear();
    ontProperties.clear();
    dataTypeProperties.clear();
    objectProperties.clear();
    ontClasses.clear();

    if (null != model && model.isClosed()) {
      model.close();
    }

    if (null != ontModel && ontModel.isClosed()) {
      ontModel.close();
    }
  }

  private void read(String filenameOrURI) {
    InputStream in = FileManager.get().open(filenameOrURI);

    if (null == in) {
      throw new IllegalArgumentException("File: " + filenameOrURI + " not found.");
    }

    read(in);
  }

  private void read(InputStream in) {
    if (null == in) return;
    model = ModelFactory.createDefaultModel();
    model.read(in, null);
    ontModel = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM, model);

    acquire();

    if (logger.isInfoEnabled()) {
      logger.info(String.format(infoFormat,
            filenameOrURI,
            individuals.size(),
            categories.size(),
            ontProperties.size(), dataTypeProperties.size(), objectProperties.size(),
            ontClasses.size()));
    }
  }

  private void acquireIndividuals() {
    for (ExtendedIterator<Individual> it = ontModel.listIndividuals(); it.hasNext(); ) {
      Individual i = it.next();
      if (isIgnoredIndividual(i)) continue;
      individuals.add(i);
    }
  }

  private void acquireCategories() {
    for (ResIterator it = ontModel.listResourcesWithProperty(RDF.type, SKOS.Concept); it.hasNext(); ) {
      Resource r = it.nextResource();
      if (isIgnoredCategory(r)) continue;
      categories.add(r);
    }
  }

  private void acquireOntProperties() {
    for (ExtendedIterator<OntProperty> it = ontModel.listAllOntProperties(); it.hasNext(); ) {
      OntProperty p = it.next();
      if (isIgnoredOntProperty(p)) continue;
      ontProperties.add(p);
    }
  }

  private void acquireDataTypeProperties() {
    for (ExtendedIterator<DatatypeProperty> it = ontModel.listDatatypeProperties(); it.hasNext(); ) {
      DatatypeProperty p = it.next();
      if (isIgnoredDataTypeProperty(p)) continue;
      dataTypeProperties.add(p);
    }
  }

  private void acquireObjectProperties() {
    for (ExtendedIterator<ObjectProperty> it = ontModel.listObjectProperties(); it.hasNext(); ) {
      ObjectProperty p = it.next();
      if (isIgnoredObjectProperty(p)) continue;
      objectProperties.add(p);
    }
  }

  private void acquireOntClasses() {
    for (ExtendedIterator<OntClass> it = ontModel.listClasses(); it.hasNext(); ) {
      OntClass c = it.next();
      if (isIgnoredOntClass(c)) continue;
      ontClasses.add(c);
    }
  }

  private void acquire() {
    acquireIndividuals();
    acquireCategories();
    acquireOntProperties();
    acquireDataTypeProperties();
    acquireObjectProperties();
    acquireOntClasses();
  }

  private static final boolean isIgnoredIndividual(Individual i) {
    return i.hasProperty(RDF.type, SKOS.Concept) || i.hasProperty(RDF.type, DBkWik.Image);
  }

  private static final boolean isIgnoredCategory(Resource r) {
    // XXX:
    return false;
  }

  private static final boolean isIgnoredOntProperty(OntProperty p) {
    // XXX:
    return false;
  }

  private static final boolean isIgnoredDataTypeProperty(DatatypeProperty p) {
    // XXX:
    return false;
  }

  private static final boolean isIgnoredObjectProperty(ObjectProperty p) {
    // XXX:
    return false;
  }

  private static final boolean isIgnoredOntClass(OntClass c) {
    return c.isAnon();
  }
}
