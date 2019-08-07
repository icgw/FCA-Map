/*
 * ClassRefinerImpl.java
 * Copyright (C) 2019 Guowei Chen <icgw@outlook.com>
 *
 * Distributed under terms of the GPL license.
 */

package cn.amss.semanticweb.enhancement.impl;

import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import java.util.HashSet;
import java.util.Arrays;

import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.StmtIterator;
import org.apache.jena.vocabulary.RDF;

import cn.amss.semanticweb.enhancement.ClassRefiner;
import cn.amss.semanticweb.enhancement.RefinerByFCA;
import cn.amss.semanticweb.alignment.Mapping;
import cn.amss.semanticweb.alignment.MappingCell;
import cn.amss.semanticweb.fca.Hermes;

public class ClassRefinerImpl extends RefinerByFCA implements ClassRefiner
{
  public ClassRefinerImpl() {
    super();
  }

  private static void acquireInstancesFromClass(Resource clazz, Set<Resource> instances) {
    for (StmtIterator stmt = clazz.getModel().listStatements(null, RDF.type, clazz); stmt.hasNext(); ) {
      Resource s = stmt.nextStatement().getSubject();
      instances.add(s);
    }
  }

  private static void acquireInstancesFromMappingCell(MappingCell mc, Set<Resource> instances) {
    acquireInstancesFromClass(mc.getResource1(), instances);
    acquireInstancesFromClass(mc.getResource2(), instances);
  }

  @Override
  protected void acquireAttributesFromMappingCell(MappingCell mc, Set<Resource> attributes) {
    acquireInstancesFromMappingCell(mc, attributes);
  }

  @Override
  protected Set<MappingCell> extractRefinement(Hermes<MappingCell, Resource> hermes) {
    return extractAllObjectInGSHLimit(hermes);
  }

  @Override
  public void addInstanceAnchors(Mapping instance_anchors) {
    for (MappingCell mc : instance_anchors) {
      add(m_hash_anchors, mc.getResource1(), mc.getResource2());
      add(m_hash_anchors, mc.getResource2(), mc.getResource1());
    }
  }

  @Override
  public void validateClassAnchors(Mapping class_anchors, Mapping mappings) {
    validateAnchors(class_anchors, mappings);
  }
}
