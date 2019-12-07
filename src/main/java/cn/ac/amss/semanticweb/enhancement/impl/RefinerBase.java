/*
 * RefinerBase.java
 * Copyright (C) 2019 Guowei Chen <icgw@outlook.com>
 *
 * Distributed under terms of the GPL license.
 */

package cn.ac.amss.semanticweb.enhancement.impl;


import org.apache.jena.rdf.model.Resource;

import java.util.Set;
import java.util.HashSet;
import java.util.Map;
import java.util.HashMap;
import java.util.Arrays;

import cn.ac.amss.semanticweb.fca.Hermes;
import cn.ac.amss.semanticweb.alignment.Mapping;
import cn.ac.amss.semanticweb.alignment.MappingCell;

public abstract class RefinerBase
{
  protected Map<Resource, Set<Resource>> m_hash_anchors = null;

  public RefinerBase() {
    m_hash_anchors = new HashMap<>();
  }

  abstract protected void acquireAttributesFromMappingCell(MappingCell mc, Set<Resource> attributes);

  abstract protected Set<MappingCell> extractRefinement(Hermes<MappingCell, Resource> hermes);

  protected final static void add(Map<Resource, Set<Resource>> m, Resource key, Resource value) {
    Set<Resource> v = m.get(key);
    if (v != null) {
      v.add(value);
    } else {
      m.put(key, new HashSet<Resource>(Arrays.asList(value)));
    }
  }

  protected void validateAnchors(Mapping anchors, Mapping refined_mappings) {
    Map<MappingCell, Set<Resource>> context = new HashMap<>();

    for (MappingCell mc : anchors) {
      Set<Resource> attributes = new HashSet<>();
      acquireAttributesFromMappingCell(mc, attributes);
      Set<Resource> inferred_attributes = new HashSet<>(attributes);

      for (Resource a : attributes) {
        Set<Resource> v = m_hash_anchors.get(a);
        if (v != null) {
          inferred_attributes.addAll(v);
        }
      }

      context.put(mc, inferred_attributes);
    }

    Hermes<MappingCell, Resource> hermes = new Hermes<>();
    hermes.init(context);
    hermes.compute();

    Set<MappingCell> enhanced_mappings = extractRefinement(hermes);
    refined_mappings.addAll(enhanced_mappings);

    hermes.close();
  }
}
