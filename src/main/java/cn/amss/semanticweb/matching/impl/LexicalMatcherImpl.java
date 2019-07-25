/*
 * LexicalMatcherImpl.java
 * Copyright (C) 2019 Guowei Chen <icgw@outlook.com>
 *
 * Distributed under terms of the GPL license.
 */

package cn.amss.semanticweb.matching.impl;

import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;
import org.apache.jena.vocabulary.RDFS;
import org.apache.jena.vocabulary.SKOS;

import java.util.Set;
import java.util.HashSet;
import java.util.Map;
import java.util.HashMap;

import cn.amss.semanticweb.matching.LexicalMatcher;
import cn.amss.semanticweb.matching.MatcherByFCA;
import cn.amss.semanticweb.alignment.Mapping;
import cn.amss.semanticweb.model.ResourceWrapper;

// for test
import cn.amss.semanticweb.model.ModelWrapper;
import cn.amss.semanticweb.vocabulary.DBkWik;

public class LexicalMatcherImpl extends MatcherByFCA implements LexicalMatcher
{
  private static final String delimiter = "(/resource/)|(/property/)|(/class/)";
  private Map<String, Set<ResourceWrapper>> labelOrName2Instances  = null;
  private Map<String, Set<ResourceWrapper>> labelOrName2Properties = null;
  private Map<String, Set<ResourceWrapper>> labelOrName2Classes    = null;

  public LexicalMatcherImpl() {
    // TODO:
  }

  private static Set<String> acquireAllLiteralsLexicalFormsWith(Resource resource, Property property, boolean b_lowercase) {
    Set<String> s = new HashSet<>();
    for (StmtIterator it = resource.listProperties(property); it.hasNext(); ) {
      Statement stmt = it.nextStatement();
      RDFNode object = stmt.getObject();
      if (object.isLiteral()) {
        String lb = object.asLiteral().getString();
        if (lb != null && !lb.equals("")) {
          if (b_lowercase) {
            s.add(lb.toLowerCase());
          } else {
            s.add(lb);
          }
        }
      }
    }
    return s;
  }

  private static Set<String> acquireLabelOrName(Resource resource, boolean b_lowercase) {
    Set<String> labelOrName = new HashSet<>();

    labelOrName.addAll(acquireAllLiteralsLexicalFormsWith(resource, RDFS.label, b_lowercase));

    labelOrName.addAll(acquireAllLiteralsLexicalFormsWith(resource, SKOS.prefLabel, b_lowercase));

    labelOrName.addAll(acquireAllLiteralsLexicalFormsWith(resource, SKOS.altLabel, b_lowercase));

    labelOrName.addAll(acquireAllLiteralsLexicalFormsWith(resource, SKOS.hiddenLabel, b_lowercase));

    if (labelOrName.isEmpty()) {
      String name = resource.getLocalName();
      if (!name.equals("")) {
        labelOrName.add(name);
      }
    }

    if (labelOrName.isEmpty()) {
      String[] parts = resource.getURI().split(delimiter);
      if (parts.length > 1) {
        labelOrName.add(parts[1]);
      }
    }

    if (labelOrName.isEmpty()) {
      // TODO: case that empty.
    }

    return labelOrName;
  }

  private void constructLabelOrName2ResourcesTable(Set<Resource> resources, Map<String, Set<ResourceWrapper>> m, int from_id, boolean b_lowercase) {
    // TODO:
  }

  private void constructLabelOrName2ResourcesTable(Set<Resource> sources, Set<Resource> targets, Map<String, Set<ResourceWrapper>> m, boolean b_lowercase) {
    constructLabelOrName2ResourcesTable(sources, m, m_source_id, b_lowercase);
    constructLabelOrName2ResourcesTable(sources, m, m_target_id, b_lowercase);
  }

  @Override
  public void matchResources(Set<Resource> sources, Set<Resource> targets, Mapping mappings) {
    // TODO:
  }

  @Override
  public void matchInstances(Set<Resource> sources, Set<Resource> targets, Mapping mappings) {
    matchResources(sources, targets, mappings);
  }

  @Override
  public void matchProperties(Set<Resource> sources, Set<Resource> targets, Mapping mappings) {
    matchResources(sources, targets, mappings);
  }

  @Override
  public void matchClasses(Set<Resource> sources, Set<Resource> targets, Mapping mappings) {
    matchResources(sources, targets, mappings);
  }

  public static void main(String[] args) {
    // String inputFile = "file:/Users/icgw/Desktop/dataset/swtor.xml";
    String inputFile = "file:/Users/icgw/Desktop/dataset/swg.xml";

    ModelWrapper m = new ModelWrapper();
    m.setFileName(inputFile);
    m.read();

    Set<Resource> inst = m.getInstances();

    for (Resource r : inst) {
      Set<String> ln = acquireLabelOrName(r, true);
      if (ln.isEmpty() || (ln.size() > 1 && r.getLocalName().isEmpty())) {
      // if (ln.isEmpty()) {
        System.out.println(r.getURI() + " => " + ln + ", local_name: " + r.getLocalName());
      }
    }
  }

}
