/*
 * LexicalMatcherImpl.java
 * Copyright (C) 2019 Guowei Chen <icgw@outlook.com>
 *
 * Distributed under terms of the GPL license.
 */

package cn.amss.semanticweb.matching.impl;

import org.apache.jena.util.Tokenizer;
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
import cn.amss.semanticweb.lexicon.stemming.PorterStemmer;
import cn.amss.semanticweb.text.Normalize;
import cn.amss.semanticweb.fca.Hermes;

public class LexicalMatcherImpl extends MatcherByFCA implements LexicalMatcher
{
  private static final String delimiter4uri = "/((resource)|(property)|(class))/";

  private static final String delimiter_characters = " :,.";
  private static final String delimiter_literal    = "";
  private static final boolean return_delimiter    = false;
  private static final boolean use_porter_stemmer  = true;
  private static final boolean to_lower_case       = true;

  public LexicalMatcherImpl() {
  }

  private static Set<String> acquireAllTokens(String norm_str, boolean use_stemmer) {
    Tokenizer tokenizer = new Tokenizer(norm_str, delimiter_characters, delimiter_literal, return_delimiter);
    Set<String> tokens  = new HashSet<>();
    while (tokenizer.hasMoreTokens()) {
      String token = tokenizer.nextToken();
      if (use_stemmer) {
        PorterStemmer stm = new PorterStemmer();
        token = stm.mutate(token);
      }
      tokens.add(token);
    }
    return tokens;
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
      String[] parts = resource.getURI().split(delimiter4uri);
      if (parts.length > 1) {
        labelOrName.add(parts[1]);
      }
    }

    if (labelOrName.isEmpty()) {
      // TODO: case that still empty.
    }

    return labelOrName;
  }

  private void constructLabelOrName2ResourcesTable(Set<Resource> resources, Map<String, Set<ResourceWrapper>> m, int from_id, boolean b_lowercase) {
    if (resources == null || m == null) return;

    for (Resource r : resources) {
      Set<String> labelOrNames = acquireLabelOrName(r, b_lowercase);
      for (String ln : labelOrNames) {
        m.putIfAbsent(ln, new HashSet<ResourceWrapper>());
        m.get(ln).add(new ResourceWrapper(r, from_id));
      }
    }
  }

  private void constructLabelOrName2ResourcesTable(Set<Resource> sources, Set<Resource> targets, Map<String, Set<ResourceWrapper>> m, boolean b_lowercase) {
    constructLabelOrName2ResourcesTable(sources, m, m_source_id, b_lowercase);
    constructLabelOrName2ResourcesTable(targets, m, m_target_id, b_lowercase);
  }

  private Map<String, Set<String>> constructContextLexicalForm(Set<String> labelOrNames) {
    Map<String, Set<String>> context = new HashMap<>();
    if (labelOrNames == null) return context;

    for (String ln : labelOrNames) {
      String norm_ln = Normalize.normalizeCaseStyle(ln);
      context.put(ln, acquireAllTokens(norm_ln, use_porter_stemmer));
    }

    return context;
  }

  private void splitResourceWrapper(Set<String> lns, Map<String, Set<ResourceWrapper>> m, Set<Resource> source, Set<Resource> target) {
    if (lns == null || m == null || source == null || target == null) return;
    for (String ln : lns) {
      Set<ResourceWrapper> rws = m.get(ln);
      if (rws != null && !rws.isEmpty()) {
        for (ResourceWrapper rw : rws) {
          if (isFromSource(rw)) {
            source.add(rw.getResource());
          } else if (isFromTarget(rw)) {
            target.add(rw.getResource());
          }
        }
      }
    }
  }

  private void extractMapping(Set<Set<String>> cluster, Map<String, Set<ResourceWrapper>> m, Mapping mappings) {
    Set<Resource> source = new HashSet<>();
    Set<Resource> target = new HashSet<>();
    for (Set<String> c : cluster) {
      source.clear();
      target.clear();

      splitResourceWrapper(c, m, source, target);

      for (Resource s : source) {
        for (Resource t : target) {
          mappings.add(s, t);
        }
      }
    }
  }

  @Override
  public void matchResources(Set<Resource> sources, Set<Resource> targets, Mapping mappings) {
    if (sources == null || targets == null || mappings == null) return;

    Map<String, Set<ResourceWrapper>> labelOrName2Resources = new HashMap<>();
    constructLabelOrName2ResourcesTable(sources, targets, labelOrName2Resources, to_lower_case);

    Set<String> labelOrNames         = labelOrName2Resources.keySet();
    Map<String, Set<String>> context = constructContextLexicalForm(labelOrNames);
    Hermes<String, String> hermes    = new Hermes<>();
    hermes.init(context);
    hermes.compute();

    Set<Set<String>> simplified_extents = null, extents = null;
    if (extract_from_GSH) {
      simplified_extents = extractExtentsFromGSH(hermes);
    }

    if (extract_from_Lattice) {
      extents = extractExtentsFromLattice(hermes);
    }

    if (simplified_extents != null) {
      extractMapping(simplified_extents, labelOrName2Resources, mappings);
    }

    if (extents != null) {
      extractMapping(extents, labelOrName2Resources, mappings);
    }

    hermes.close();
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
}
