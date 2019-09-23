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
import java.util.Arrays;
import java.util.StringTokenizer;

import cn.amss.semanticweb.matching.LexicalMatcher;
import cn.amss.semanticweb.matching.MatcherByFCA;
import cn.amss.semanticweb.alignment.Mapping;
import cn.amss.semanticweb.model.ResourceWrapper;
import cn.amss.semanticweb.lexicon.stemming.PorterStemmer;
import cn.amss.semanticweb.text.Normalize;
import cn.amss.semanticweb.fca.Hermes;
import cn.amss.semanticweb.vocabulary.DBkWik;

/**
 * Lexical Matcher based on formal concept analysis.
 *
 * @author Guowei Chen (icgw@outlook.com)
 */
public class LexicalMatcherImpl extends MatcherByFCA implements LexicalMatcher
{
  private static final String delimiter_characters = " \t-({[)}]_!@#%&*\\:;\"',.?/~+=|<>$`^";
  private static final boolean return_delimiter    = false;
  private static final boolean use_porter_stemmer  = true;
  private static final boolean to_lower_case       = true;

  private static final boolean use_normalize_case_style = true;
  private static final boolean use_remove_S             = true;

  private static boolean use_strip_diacritics = true;

  public LexicalMatcherImpl() {
  }

  private static Set<String> acquireAllTokens(String norm_str, boolean use_stemmer) {
    StringTokenizer tokenizer = new StringTokenizer(norm_str, delimiter_characters, return_delimiter);
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
      if (name != null && !name.equals("")) {
        labelOrName.add(name);
      }
    }

    if (labelOrName.isEmpty()) {
      String name = DBkWik.getName(resource.getURI());
      if (name != null && !name.isEmpty()) {
        labelOrName.add(name);
      }
    }

    if (labelOrName.isEmpty()) {
      // TODO: case that still empty.
    }

    return labelOrName;
  }

  private <T extends Resource> void constructLabelOrName2ResourcesTable(Set<T> resources,
                                                                        Map<String, Set<ResourceWrapper<T>>> m,
                                                                        int from_id,
                                                                        boolean b_lowercase) {
    if (resources == null || m == null) return;

    for (T r : resources) {
      Set<String> labelOrNames = acquireLabelOrName(r, b_lowercase);
      for (String ln : labelOrNames) {
        m.putIfAbsent(ln, new HashSet<ResourceWrapper<T>>());
        m.get(ln).add(new ResourceWrapper<T>(r, from_id));
      }
    }
  }

  private <T extends Resource> void constructLabelOrName2ResourcesTable(Set<T> sources,
                                                                        Set<T> targets,
                                                                        Map<String, Set<ResourceWrapper<T>>> m,
                                                                        boolean b_lowercase) {
    constructLabelOrName2ResourcesTable(sources, m, m_source_id, b_lowercase);
    constructLabelOrName2ResourcesTable(targets, m, m_target_id, b_lowercase);
  }

  private Map<String, Set<String>> constructContextLexicalForm(Set<String> labelOrNames) {
    Map<String, Set<String>> context = new HashMap<>();
    if (labelOrNames == null) return context;

    for (String ln : labelOrNames) {
      String norm_ln = ln;

      if (use_normalize_case_style) {
        norm_ln = Normalize.normalizeCaseStyle(norm_ln);
      }

      if (use_strip_diacritics) {
        norm_ln = Normalize.stripDiacritics(norm_ln);
      }

      if (use_remove_S) {
        norm_ln = Normalize.removeS(norm_ln);
      }

      context.put(ln, acquireAllTokens(norm_ln, use_porter_stemmer));
    }

    return context;
  }

  private <T extends Resource> void splitResourceWrapper(Set<String> lns,
                                                         Map<String, Set<ResourceWrapper<T>>> m,
                                                         Set<T> source,
                                                         Set<T> target) {
    if (lns == null || m == null || source == null || target == null) return;
    for (String ln : lns) {
      Set<ResourceWrapper<T>> rws = m.get(ln);
      if (rws != null && !rws.isEmpty()) {
        for (ResourceWrapper<T> rw : rws) {
          if (isFromSource(rw)) {
            source.add(rw.getResource());
          } else if (isFromTarget(rw)) {
            target.add(rw.getResource());
          }
        }
      }
    }
  }

  private <T extends Resource> void splitResourceWrapper(Set<String> lns,
                                                         Map<String, Set<ResourceWrapper<T>>> m,
                                                         Set<T> source,
                                                         Set<T> target,
                                                         Set<T> intermediate) {
    if (lns == null || m == null || source == null || target == null) return;
    for (String ln : lns) {
      Set<ResourceWrapper<T>> rws = m.get(ln);
      if (rws != null && !rws.isEmpty()) {
        for (ResourceWrapper<T> rw : rws) {
          if (isFromSource(rw)) {
            source.add(rw.getResource());
          } else if (isFromTarget(rw)) {
            target.add(rw.getResource());
          } else {
            intermediate.add(rw.getResource());
          }
        }
      }
    }
  }

  private <T extends Resource> void extractMapping(Set<Set<String>> cluster,
                                                   Map<String, Set<ResourceWrapper<T>>> m,
                                                   Mapping mappings) {
    Set<T> source = new HashSet<>();
    Set<T> target = new HashSet<>();
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

  private static <K, V> void add(Map<K, Set<V>> m, K k, V v) {
    Set<V> vs = m.get(k);
    if (vs == null) {
      m.put(k, new HashSet<>(Arrays.asList(v)));
    } else {
      vs.add(v);
    }
  }

  private <T extends Resource> void extractIntermediate2SourceTarget(Set<Set<String>> cluster,
                                                                     Map<String, Set<ResourceWrapper<T>>> m,
                                                                     Map<T, Set<T>> intermediate2Source,
                                                                     Map<T, Set<T>> intermediate2Target) {
    Set<T> source = new HashSet<>();
    Set<T> target = new HashSet<>();
    Set<T> intermediate = new HashSet<>();

    for (Set<String> c : cluster) {
      source.clear();
      target.clear();
      intermediate.clear();

      splitResourceWrapper(c, m, source, target, intermediate);

      if ( !intermediate.isEmpty() && (!source.isEmpty() || !target.isEmpty()) ) {
        for (T i : intermediate) {
          for (T s : source) {
            add(intermediate2Source, i, s);
          }

          for (T t : target) {
            add(intermediate2Target, i, t);
          }
        }
      }
    }
  }

  private <T extends Resource> void extractMapping4MultiSources(Set<Set<String>> cluster,
                                                                Map<String, Set<ResourceWrapper<T>>> m,
                                                                Mapping mappings) {
    Map<T, Set<T>> intermediate2Source = new HashMap<>();
    Map<T, Set<T>> intermediate2Target = new HashMap<>();

    extractIntermediate2SourceTarget(cluster, m, intermediate2Source, intermediate2Target);

    Set<T> intermediate = new HashSet<>();

    intermediate.addAll(intermediate2Source.keySet());
    intermediate.retainAll(intermediate2Target.keySet());

    for (T i : intermediate) {
      Set<T> source = intermediate2Source.get(i);
      Set<T> target = intermediate2Target.get(i);

      for (T s : source) {
        for (T t : target) {
          mappings.add(s, t);
        }
      }
    }
  }

  @Override
  protected <T extends Resource> void matchResources(Set<T> sources, Set<T> targets, Mapping mappings) {
    if (sources == null || targets == null || mappings == null) return;

    Map<String, Set<ResourceWrapper<T>>> labelOrName2Resources = new HashMap<>();
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
  protected <T extends Resource> void matchResources(Map<Integer, Set<T>> id2Resources, Mapping mappings) {
    if (id2Resources == null || mappings == null) return;

    Map<String, Set<ResourceWrapper<T>>> labelOrName2Resources = new HashMap<>();
    for (Map.Entry<Integer, Set<T>> e : id2Resources.entrySet()) {
      constructLabelOrName2ResourcesTable(e.getValue(), labelOrName2Resources, e.getKey(), to_lower_case);
    }

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
      if (m_number_of_ontologies > 2) {
        extractMapping4MultiSources(extents, labelOrName2Resources, mappings);
      }
    }

    hermes.close();
  }

  @Override
  public void mapInstances(Mapping mappings) {
    matchResources(id2Instances, mappings);
  }

  @Override
  public void mapOntClasses(Mapping mappings) {
    matchResources(id2OntClasses, mappings);
  }

  @Override
  public void mapOntProperties(Mapping mappings) {
    matchResources(id2OntProperties, mappings);
  }

  @Override
  public void mapDatatypeProperties(Mapping mappings) {
    matchResources(id2DatatypeProperties, mappings);
  }

  @Override
  public void mapObjectProperties(Mapping mappings) {
    matchResources(id2ObjectProperties, mappings);
  }

  @Override
  public <T extends Resource> void matchInstances(Set<T> sources, Set<T> targets, Mapping mappings) {
    matchResources(sources, targets, mappings);
  }

  @Override
  public <T extends Resource> void matchProperties(Set<T> sources, Set<T> targets, Mapping mappings) {
    matchResources(sources, targets, mappings);
  }

  @Override
  public <T extends Resource> void matchClasses(Set<T> sources, Set<T> targets, Mapping mappings) {
    matchResources(sources, targets, mappings);
  }

  @Override
  public void setUseStripDiacritics(boolean b) {
    use_strip_diacritics = b;
  }
}
