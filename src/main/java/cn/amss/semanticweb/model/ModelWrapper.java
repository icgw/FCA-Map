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
// import org.apache.jena.ontology.OntModel;
// import org.apache.jena.ontology.OntModelSpec;
// import org.apache.jena.ontology.Individual;
import org.apache.jena.vocabulary.RDF;
import org.apache.jena.vocabulary.OWL;
import org.apache.jena.vocabulary.SKOS;
import org.apache.jena.util.FileManager;
// import org.apache.jena.util.iterator.ExtendedIterator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;
import java.util.HashSet;
import java.io.InputStream;

import cn.amss.semanticweb.vocabulary.DBkWik;

// for test
// import org.apache.jena.reasoner.rulesys.Rule;
// import org.apache.jena.reasoner.rulesys.GenericRuleReasoner;
// import org.apache.jena.reasoner.Reasoner;
// import java.util.Map;

public class ModelWrapper
{
  final Logger m_logger = LoggerFactory.getLogger(ModelWrapper.class);

  private String m_file_name = null;

  private Model    m_raw_model      = null;
  private InfModel m_inferred_model = null;
  // private OntModel m_ontology_model = null;

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

    // XXX: add rule.
    // String rules = "[r1: (?a ?p ?b) (?p rdfs:label 'type') -> (?a rdf:type ?b)]";
    // Reasoner reasoner = new GenericRuleReasoner(Rule.parseRules(rules));
    // m_inferred_model = ModelFactory.createInfModel(reasoner, m_raw_model);
 
    // XXX: You can change it and add other reasoning rules.
    m_inferred_model = ModelFactory.createRDFSModel(m_raw_model);
    // m_ontology_model = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM_MICRO_RULE_INF, m_raw_model);

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
          // new SimpleSelector(null, RDF.type, (RDFNode) null) {
          new SimpleSelector(null, null, (RDFNode) null) {
            @Override
            public boolean selects(Statement s) {
              Resource subject = s.getSubject();
              return !subject.hasProperty(RDF.type, SKOS.Concept) &&
                     !subject.hasProperty(RDF.type, DBkWik.Image) &&
                     !subject.hasProperty(RDF.type, RDF.Property) &&
                     !subject.hasProperty(RDF.type, OWL.Class) &&
                     // DBkWik.own(subject);
                     DBkWik.ownAsResource(subject);
            }
          }
        );

    while (it.hasNext()) {
      Resource r = it.nextStatement().getSubject();
      m_instances.add(r);
    }

    // ExtendedIterator<Individual> it = m_ontology_model.listIndividuals();
    // while (it.hasNext()) {
    //   Individual inst = it.next();
    //   if (inst.isResource()) {
    //     m_instances.add(inst.asResource());
    //   }
    // }
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

  public Set<Resource> getInstances() {
    // XXX: ..
    return m_instances;
  }

  public Set<Resource> getProperties() {
    // XXX: ..
    return m_properties;
  }

  public Set<Resource> getClasses() {
    // XXX: ..
    return m_classes;
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
