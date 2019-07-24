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
import org.apache.jena.rdf.model.StmtIterator;
import org.apache.jena.rdf.model.SimpleSelector;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.vocabulary.RDF;
import org.apache.jena.vocabulary.OWL;
import org.apache.jena.vocabulary.SKOS;
import org.apache.jena.util.FileManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;
import java.util.HashSet;
import java.io.InputStream;

import cn.amss.semanticweb.vocabulary.DBkWik;

public class ModelWrapper
{
  final Logger m_logger = LoggerFactory.getLogger(ModelWrapper.class);

  private String m_file_name = null;

  private Model    m_raw_model      = null;
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

  public void setFileName(String fileNameOrURI) {
    m_file_name = fileNameOrURI;
  }

  public void read() {
    if (null == m_file_name || m_file_name == "") {
      // XXX: No file name.
      return;
    }

    InputStream in = FileManager.get().open(m_file_name);
    if (null == in) {
      throw new IllegalArgumentException( "File: " + m_file_name + " not found." );
    }

    m_raw_model.read(in, null);

    // XXX: You can change it and add other reasoning rules.
    m_inferred_model = ModelFactory.createRDFSModel(m_raw_model);

    // NOTE: acquire instances, properties and classes.
    acquireInstances();
    acquireProperties();
    acquireClasses();

    m_logger.info("Source: {}, #Instances: {}, #Properties: {}, #Classes: {}.",
                   m_file_name, m_instances.size(), m_properties.size(), m_classes.size());
  }

  private Set<Resource> acquireResources(ResIterator it) {
    Set<Resource> resources = new HashSet<>();
    while (it.hasNext()) {
      Resource r = it.nextResource();
      if (DBkWik.own(r)) {
        resources.add(r);
      }
    }
    return resources;
  }

  private void acquireInstances() {
    // XXX: remove instances of DBkWik:Image and SKOS:Concept. Under consideration.
    Set<Resource> m_intances = new HashSet<>();

    StmtIterator it = m_inferred_model.listStatements(
    // StmtIterator it = m_raw_model.listStatements(
          new SimpleSelector(null, RDF.type, (RDFNode) null) {
            @Override
            public boolean selects(Statement s) {
              return !s.getObject().equals(SKOS.Concept) &&
                     !s.getObject().equals(DBkWik._Image) &&
                     !s.getObject().equals(RDF.Property) &&
                     !s.getObject().equals(OWL.Class) &&
                     DBkWik.own(s.getSubject());
            }
          }
        );

    while (it.hasNext()) {
      Resource r = it.nextStatement().getSubject();
      m_instances.add(r);
    }
  }

  private void acquireProperties() {
    // ResIterator it = m_raw_model.listResourcesWithProperty(RDF.type, RDF.Property);
    ResIterator it = m_inferred_model.listResourcesWithProperty(RDF.type, RDF.Property);
    m_properties = acquireResources(it);
  }

  private void acquireClasses() {
    // ResIterator it = m_raw_model.listResourcesWithProperty(RDF.type, OWL.Class);
    ResIterator it = m_inferred_model.listResourcesWithProperty(RDF.type, OWL.Class);
    m_classes = acquireResources(it);
  }

  public static void main(String[] args) {
    // XXX:
    String inputFile = "file:/Users/icgw/Desktop/dataset/swtor.xml";
    // String inputFile = "file:/Users/icgw/Desktop/dataset/swg.xml";

    ModelWrapper m = new ModelWrapper();
    m.setFileName(inputFile);
    m.read();
  }
}
