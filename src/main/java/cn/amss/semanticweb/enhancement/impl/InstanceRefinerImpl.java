/*
 * InstanceRefinerImpl.java
 * Copyright (C) 2019 Guowei Chen <icgw@outlook.com>
 *
 * Distributed under terms of the GPL license.
 */

package cn.amss.semanticweb.enhancement.impl;

import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import java.util.HashSet;

import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;

import cn.amss.semanticweb.alignment.Mapping;
import cn.amss.semanticweb.alignment.MappingCell;
import cn.amss.semanticweb.enhancement.InstanceRefiner;
import cn.amss.semanticweb.enhancement.RefinerByFCA;
import cn.amss.semanticweb.fca.Hermes;

public class InstanceRefinerImpl extends RefinerByFCA implements InstanceRefiner
{
  public InstanceRefinerImpl() {
    super();
  }

  private static void acquireResourcesFromInstance(Resource instance, Set<Resource> resources) {
    for (StmtIterator it = instance.listProperties(); it.hasNext(); ) {
      Statement stmt     = it.nextStatement();
      Property predicate = stmt.getPredicate();
      RDFNode object     = stmt.getObject();

      if (predicate.isResource() && object.isResource()) {
        resources.add(predicate.asResource());
        resources.add(object.asResource());
      }
    }

    for (StmtIterator it = instance.getModel().listStatements(null, null, instance); it.hasNext(); ) {
      Statement stmt = it.nextStatement();
      resources.add(stmt.getSubject().asResource());
      resources.add(stmt.getPredicate().asResource());
    }
  }

  private static void acquireResourcesFromMappingCell(MappingCell mc, Set<Resource> resources) {
    acquireResourcesFromInstance(mc.getResource1(), resources);
    acquireResourcesFromInstance(mc.getResource2(), resources);
  }

  @Override
  protected void acquireAttributesFromMappingCell(MappingCell mc, Set<Resource> attributes) {
    acquireResourcesFromMappingCell(mc, attributes);
  }

  @Override
  protected Set<MappingCell> extractRefinement(Hermes<MappingCell, Resource> hermes) {
    Set<MappingCell> mappings = new HashSet<>();

    if (m_extract_from_GSH) {
      mappings.addAll(extractAllObjectInGSHLimit(hermes));
    }

    if (m_extract_from_Lattice) {
      mappings.addAll(extractAllObjectInLatticeLimit(hermes));
    }

    return mappings;
  }


  @Override
  public void addPropertyAnchors(Mapping property_anchors) {
    for (MappingCell mc : property_anchors) {
      add(m_hash_anchors, mc.getResource1(), mc.getResource2());
      add(m_hash_anchors, mc.getResource2(), mc.getResource1());
    }
  }

  @Override
  public void addClassAnchors(Mapping class_anchors) {
    for (MappingCell mc : class_anchors) {
      add(m_hash_anchors, mc.getResource1(), mc.getResource2());
      add(m_hash_anchors, mc.getResource2(), mc.getResource1());
    }
  }

  @Override
  public void addInstanceAnchors(Mapping instance_anchors) {
    for (MappingCell mc : instance_anchors) {
      add(m_hash_anchors, mc.getResource1(), mc.getResource2());
      add(m_hash_anchors, mc.getResource2(), mc.getResource1());
    }
  }

  @Override
  public void addAllAnchors(Mapping anchors) {
    for (MappingCell mc : anchors) {
      add(m_hash_anchors, mc.getResource1(), mc.getResource2());
      add(m_hash_anchors, mc.getResource2(), mc.getResource1());
    }
  }

  @Override
  public void validateInstanceAnchors(Mapping instance_anchors, Mapping mappings) {
    validateAnchors(instance_anchors, mappings);
  }
}
