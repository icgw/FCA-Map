/*
 * Hermes.java
 * Copyright (C) 2019 Guowei Chen <icgw@outlook.com>
 *
 * Distributed under terms of the GPL license.
 */

package cn.amss.semanticweb.fca;

import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Arrays;

import cn.amss.semanticweb.util.Pair;

/**
 * Hermes: a simple and efficient algorithm for building the AOC-poset of a binary relation
 *   Anne Berry, Alain Gutierrez, Marianne Huchard, Amedeo Napoli, Alain Sigayret
 *
 * @author Guowei Chen (icgw@outlook.com)
 */
public class Hermes <O, A>
{
  private Map<Integer, Set<Integer>> object2Attributes = null;
  private Map<Integer, Set<Integer>> attribute2Objects = null;

  private Map<Integer, O> object2O = null;
  private Map<O, Integer> O2Object = null;

  private Map<Integer, A> attribute2A = null;
  private Map<A, Integer> A2Attribute = null;

  private int dividing = 0;

  /**
   * Rc: Clarified Relation.
   */
  private Map<Set<Integer>, Set<Integer>> clarified = null;

  /**
   * Dom: Domination Relation.
   */
  private Map<Set<Integer>, Set<Integer>> domination = null;

  /**
   * Rces: The simplification of Rce, which is the juxtaposition of Rc with Dom.
   */
  private Map<Set<Integer>, Set<Integer>> simplification = null;


  /**
   * Create new Hermes.
   */
  public Hermes() {
    object2Attributes = new HashMap<Integer, Set<Integer>>();
    attribute2Objects = new HashMap<Integer, Set<Integer>>();

    object2O = new HashMap<>();
    O2Object = new HashMap<>();

    attribute2A = new HashMap<>();
    A2Attribute = new HashMap<>();
  }

  private final static <K, V> void add(Map<K, Set<V>> m, K k, V v) {
    Set<V> s = m.get(k);
    if (null != s) {
      s.add(v);
    } else {
      m.put(k, new HashSet<V>(Arrays.asList(v)));
    }
  }

  private final static <K, V> void addAll(Map<K, Set<V>> m, K k, Set<V> v) {
    Set<V> s = m.get(k);
    if (null != s) {
      s.addAll(v);
    } else {
      m.put(k, v);
    }
  }

  /**
   * Initiate the map of attribute's id to objects' id and object's id to attributes' id.
   *
   * @param relations add binary relation of formal context
   * @param relations no side effect
   */
  private void init(Set<Pair<Integer, Integer>> relations) {
    for (Pair<Integer, Integer> pair : relations) {
      int object_id = pair.getKey(), attribute_id = pair.getValue();

      add(object2Attributes, object_id, attribute_id);
      add(attribute2Objects, attribute_id, object_id);
    }
  }

  public void init(Map<O, Set<A>> context) {
    int i = 0;
    for (O o : context.keySet()) {
      object2O.put(i, o);
      O2Object.put(o, i);
      ++i;
    }

    dividing = i;

    Set<A> attributes = new HashSet<>();
    for (Set<A> sa : context.values()) {
      attributes.addAll(sa);
    }

    for (A a : attributes) {
      attribute2A.put(i, a);
      A2Attribute.put(a, i);
      ++i;
    }

    Set<Pair<Integer, Integer>> relations = new HashSet<>();
    for (Map.Entry<O, Set<A>> r : context.entrySet()) {
      for (A a : r.getValue()) {
        relations.add(new Pair<>(O2Object.get(r.getKey()), A2Attribute.get(a)));
      }
    }

    init(relations);
  }

  private static Map<Set<Integer>, Set<Integer>> invert(Map<Integer, Set<Integer>> m) {
    Map<Set<Integer>, Set<Integer>> invert_m = new HashMap<>();
    for (Map.Entry<Integer, Set<Integer>> r : m.entrySet()) {
      add(invert_m, r.getValue(), r.getKey());
    }
    return invert_m;
  }

  private static class ClarifiedThread extends Thread {
    private Map<Integer, Set<Integer>> object_to_attributes;
    private Map<Set<Integer>, Set<Integer>> clarified_relations;

    ClarifiedThread(Map<Integer, Set<Integer>> m) {
      object_to_attributes = m;
      clarified_relations  = new HashMap<>();
    }

    @Override
    public void run() {
      clarified_relations = invert(object_to_attributes);
    }

    private Map<Set<Integer>, Set<Integer>> getClarified() {
      return clarified_relations;
    }
  }

  private static class DominationThread extends Thread {
    private Map<Integer, Set<Integer>> attribute_to_objects;
    private Map<Set<Integer>, Set<Integer>> domination_relations;

    DominationThread(Map<Integer, Set<Integer>> m) {
      attribute_to_objects = m;
      domination_relations = new HashMap<>();
    }

    @Override
    public void run() {
      Map<Set<Integer>, Set<Integer>> objects_to_attributes = invert(attribute_to_objects);

      Set<Set<Integer>> key_set = objects_to_attributes.keySet();
      for (Map.Entry<Set<Integer>, Set<Integer>> r : objects_to_attributes.entrySet()) {
        Set<Integer> tmp = r.getKey();

        domination_relations.putIfAbsent(r.getValue(), new HashSet<Integer>());
        Set<Integer> current = domination_relations.get(r.getValue());

        for (Set<Integer> k : key_set) {
          if (k.containsAll(tmp)) {
            current.addAll(objects_to_attributes.get(k));
          }
        }
      }
    }

    private Map<Set<Integer>, Set<Integer>> getDomination() {
      return domination_relations;
    }
  }

  public void compute() {
    if (object2Attributes == null || object2Attributes.isEmpty() ||
        attribute2Objects == null || attribute2Objects.isEmpty()) return;

    ClarifiedThread rc   = new ClarifiedThread(object2Attributes);
    DominationThread dom = new DominationThread(attribute2Objects);

    rc.start();
    dom.start();

    try {
      rc.join();
      dom.join();
    } catch (InterruptedException e) {
      e.printStackTrace();
    }

    clarified = rc.getClarified();
    domination = dom.getDomination();

    simplification = new HashMap<>(clarified);
    for (Map.Entry<Set<Integer>, Set<Integer>> r : domination.entrySet()) {
      addAll(simplification, r.getValue(), r.getKey());
    }
  }

  private Concept<Integer, Integer> simplifiedConceptIdFrom(Set<Integer> s) {
    Set<Integer> objects    = new HashSet<>();
    Set<Integer> attributes = new HashSet<>();

    if (s == null || s.isEmpty()) return new Concept<Integer, Integer>(objects, attributes);

    for (int i : s) {
      if (i < dividing) {
        objects.add(i);
      } else {
        attributes.add(i);
      }
    }

    return new Concept<Integer, Integer>(objects, attributes);
  }

  private <T> Set<T> retransform(Set<Integer> sid, Map<Integer, T> m) {
    Set<T> origin = new HashSet<>();
    if (sid == null || sid.isEmpty()) {
      return origin;
    }

    for (int i : sid) {
      if (m.containsKey(i)) {
        origin.add(m.get(i));
      }
    }

    return origin;
  }

  private Concept<O, A> retransform(Concept<Integer, Integer> concept_id) {
    if (concept_id == null) {
      return null;
    }

    Set<O> objects = retransform(concept_id.getExtent(), object2O);

    Set<A> attributes = retransform(concept_id.getIntent(), attribute2A);

    return new Concept<O, A>(objects, attributes);
  }

  private Concept<O, A> simplifiedConceptFrom(Set<Integer> s) {
    return retransform(simplifiedConceptIdFrom(s));
  }

  // NOTE: s from simplification.
  private Set<O> simplifiedExtentFrom(Set<Integer> s) {
    return retransform(s, object2O);
  }

  private Set<Integer> relative(Set<Integer> s, Map<Integer, Set<Integer>> m) {
    Set<Integer> relative_s = new HashSet<>();
    if (s == null || m == null) return relative_s;

    Iterator<Integer> it = s.iterator();
    if (it.hasNext()) {
      int i = it.next();
      if (m.get(i) != null) {
        relative_s.addAll(m.get(i));
      }
    }

    while (it.hasNext()) {
      int i = it.next();
      if (m.get(i) != null) {
        relative_s.retainAll(m.get(i));
      }
    }

    return relative_s;
  }

  private Set<Integer> relativeObjects(Set<Integer> attributes) {
    if (attributes == null || object2Attributes == null) return new HashSet<Integer>();
    if (attributes.isEmpty()) {
      return object2Attributes.keySet();
    }
    return relative(attributes, attribute2Objects);
  }

  private Set<Integer> relativeAttributes(Set<Integer> objects) {
    if (objects == null || attribute2Objects == null) return new HashSet<Integer>();
    if (objects.isEmpty()) {
      return attribute2Objects.keySet();
    }
    return relative(objects, object2Attributes);
  }

  private Concept<Integer, Integer> computeConceptId(Set<Integer> attributes,
                                                     int limit_objects_size,
                                                     int limit_attributes_size) {
    Set<Integer> extent_id = new HashSet<>();
    Set<Integer> intent_id = new HashSet<>();

    if (attributes == null) return new Concept<Integer, Integer>(extent_id, intent_id);

    intent_id.addAll(attributes);

    if (attributes.isEmpty() && object2Attributes != null) {
      Set<Integer> all_objects = object2Attributes.keySet();
      if (limit_objects_size > 0 && all_objects.size() > limit_objects_size) {
        return new Concept<Integer, Integer>(new HashSet<Integer>(), new HashSet<Integer>());
      }
      return new Concept<Integer, Integer>(all_objects, intent_id);
    }

    if (limit_attributes_size > 0 && attributes.size() > limit_attributes_size) {
      return new Concept<Integer, Integer>(new HashSet<Integer>(), new HashSet<Integer>());
    }

    extent_id = relativeObjects(attributes);

    if (limit_objects_size > 0 && extent_id.size() > limit_objects_size) {
      return new Concept<Integer, Integer>(new HashSet<Integer>(), new HashSet<Integer>());
    }

    Set<Integer> relative_intent = relativeAttributes(extent_id);

    if (!intent_id.equals(relative_intent)) {
      return new Concept<Integer, Integer>(new HashSet<Integer>(), new HashSet<Integer>());
    }

    return new Concept<Integer, Integer>(extent_id, intent_id);
  }

  private Concept<Integer, Integer> computeConceptId(Set<Integer> attributes,
                                                     int least_objects_size,    int most_objects_size,
                                                     int least_attributes_size, int most_attributes_size) {
    Set<Integer> extent_id = new HashSet<>();
    Set<Integer> intent_id = new HashSet<>();

    if (attributes == null) return new Concept<Integer, Integer>(extent_id, intent_id);

    intent_id.addAll(attributes);

    if (attributes.isEmpty() && object2Attributes != null) {
      Set<Integer> all_objects = object2Attributes.keySet();
      if (most_objects_size >= 0 && all_objects.size() > most_objects_size && all_objects.size() < least_objects_size) {
        return new Concept<Integer, Integer>(new HashSet<Integer>(), new HashSet<Integer>());
      }
      return new Concept<Integer, Integer>(all_objects, intent_id);
    }

    if (most_attributes_size >= 0 && attributes.size() > most_attributes_size && attributes.size() < least_attributes_size) {
      return new Concept<Integer, Integer>(new HashSet<Integer>(), new HashSet<Integer>());
    }

    extent_id = relativeObjects(attributes);

    if (most_objects_size >= 0 && extent_id.size() > most_objects_size && extent_id.size() < least_attributes_size) {
      return new Concept<Integer, Integer>(new HashSet<Integer>(), new HashSet<Integer>());
    }

    Set<Integer> relative_intent = relativeAttributes(extent_id);

    if (!intent_id.equals(relative_intent)) {
      return new Concept<Integer, Integer>(new HashSet<Integer>(), new HashSet<Integer>());
    }

    return new Concept<Integer, Integer>(extent_id, intent_id);
  }

  private static void attributeIdClosure(Set<Set<Integer>> set_of_attributes) {
    // XXX: wait to improve
    Map<Integer, Set<Set<Integer>>> m = new HashMap<>();
    for (Set<Integer> attributes : set_of_attributes) {
      for (int i : attributes) {
        add(m, i, attributes);
      }
    }

    boolean bIncrease;
    do {
      bIncrease = false;
      Set<Set<Integer>> copy = new HashSet<>(set_of_attributes);

      for (Set<Integer> s1 : copy) {
        Set<Set<Integer>> related_set_of_attributes = new HashSet<>();
        for (int i : s1) {
          related_set_of_attributes.addAll(m.get(i));
        }

        for (Set<Integer> s2 : related_set_of_attributes) {
          Set<Integer> s3 = new HashSet<>(s2);
          s3.retainAll(s1);
          if (!set_of_attributes.contains(s3)) {
            set_of_attributes.add(s3);
            for (int k : s3) {
              add(m, k, s3);
            }
            bIncrease = true;
          }
        }
      }
    } while (bIncrease);
  }

  /**
   * Extract the simplified concepts within particular limits.
   *
   * @param limit_objects_size limit the size of objects of simplified concept
   * @param limit_attributes_size  limit the size of attributes of simplified concept
   * @return the simplified concept, where its size of objects and attributes both are less than and equals limit
   */
  public Set<Pair<Set<O>, Set<A>>> listSimplifiedConceptsLimit(int limit_objects_size, int limit_attributes_size) {
    Set<Pair<Set<O>, Set<A>>> simplified_concepts_limit = new HashSet<>();
    if (simplification != null) {
      for (Map.Entry<Set<Integer>, Set<Integer>> e : simplification.entrySet()) {
        Pair<Set<O>, Set<A>> simplified_concept = simplifiedConceptFrom(e.getValue());
        if ((limit_objects_size <= 0 || simplified_concept.getKey().size() <= limit_objects_size) &&
            (limit_attributes_size <= 0 || simplified_concept.getValue().size() <= limit_attributes_size) &&
            simplified_concept != null) {
          simplified_concepts_limit.add(simplified_concept);
        }
      }
    }
    return simplified_concepts_limit;
  }

  /**
   * Extract the simplified concept within a interval limitation.
   *
   * @param least_objects_size the least size of objects
   * @param most_objects_size the most size of objects, when the most size less than 0 indicates no most limit
   * @param least_attributes_size the least size of attributes
   * @param most_attributes_size the most size of attributes, when the most size less than 0 indicates no most limit
   * @return the simplified concept which satisfied above condition
   */
  public Set<Pair<Set<O>, Set<A>>> listSimplifiedConceptsLeastMost(int least_objects_size,    int most_objects_size,
                                                                   int least_attributes_size, int most_attributes_size) {
    Set<Pair<Set<O>, Set<A>>> simplified_concepts_least_most = new HashSet<>();
    if (simplification != null) {
      for (Map.Entry<Set<Integer>, Set<Integer>> e : simplification.entrySet()) {
        Pair<Set<O>, Set<A>> simplified_concept = simplifiedConceptFrom(e.getValue());
        if (simplified_concept == null) continue;
        int k_sz = simplified_concept.getKey().size(), v_sz = simplified_concept.getValue().size();
        if ( k_sz >= least_objects_size    && (k_sz <= most_objects_size    || most_objects_size < 0) &&
             v_sz >= least_attributes_size && (v_sz <= most_attributes_size || most_attributes_size < 0)) {
          simplified_concepts_least_most.add(simplified_concept);
        }
      }
    }
    return simplified_concepts_least_most;
  }

  public Set<Pair<Set<O>, Set<A>>> listAllSimplifiedConcepts() {
    return listSimplifiedConceptsLimit(0, 0);
  }

  public Set<Set<O>> listSimplifiedExtentsLimit(int limit_objects_size, int limit_attributes_size) {
    Set<Set<O>> simplified_extents_limit = new HashSet<>();
    if (simplification != null) {
      for (Map.Entry<Set<Integer>, Set<Integer>> e : simplification.entrySet()) {
        Pair<Set<O>, Set<A>> simplified_concept = simplifiedConceptFrom(e.getValue());
        if ((limit_objects_size <= 0 || simplified_concept.getKey().size() <= limit_objects_size) &&
            (limit_attributes_size <= 0 || simplified_concept.getValue().size() <= limit_attributes_size) &&
            simplified_concept != null) {
          simplified_extents_limit.add(simplified_concept.getKey());
        }
      }
    }
    return simplified_extents_limit;
  }

  /**
   * Extract the simplified extents within [least, most]
   *
   * @param least_objects_size the least size of objects
   * @param most_objects_size the most size of objects, when the most size less than 0 indicates no most limit
   * @param least_attributes_size the least size of attributes
   * @param most_attributes_size the most size of attributes, when the most size less than 0 indicates no most limit
   * @return the simplified extents satisfied above conditions
   */
  public Set<Set<O>> listSimplifiedExtentsLeastMost(int least_objects_size,    int most_objects_size,
                                                    int least_attributes_size, int most_attributes_size) {
    Set<Set<O>> simplified_extents_least_most = new HashSet<>();
    if (simplification != null) {
      for (Map.Entry<Set<Integer>, Set<Integer>> e : simplification.entrySet()) {
        Pair<Set<O>, Set<A>> simplified_concept = simplifiedConceptFrom(e.getValue());
        if (simplified_concept == null) continue;
        int k_sz = simplified_concept.getKey().size(), v_sz = simplified_concept.getValue().size();
        if ( k_sz >= least_objects_size    && (k_sz <= most_objects_size    || most_objects_size < 0) &&
             v_sz >= least_attributes_size && (v_sz <= most_attributes_size || most_attributes_size < 0)) {
          simplified_extents_least_most.add(simplified_concept.getKey());
        }
      }
    }
    return simplified_extents_least_most;
  }

  private void addTopBottomAttributes(Set<Set<Integer>> set_of_attributes, int limit_attributes_size) {
    if (attribute2Objects != null) {
      Set<Integer> total_attributes = attribute2Objects.keySet();

      Set<Integer> top_attributes =  new HashSet<>(total_attributes);
      for (Set<Integer> attributes : set_of_attributes) {
        top_attributes.retainAll(attributes);
      }

      if (limit_attributes_size <= 0 || total_attributes.size() <= limit_attributes_size) {
        set_of_attributes.add(total_attributes);
      }

      if (limit_attributes_size <= 0 || total_attributes.size() <= limit_attributes_size) {
        set_of_attributes.add(top_attributes);
      }
    }
  }

  private void addTopBottomAttributes(Set<Set<Integer>> set_of_attributes, int least_attributes_size, int most_attributes_size) {
    if (attribute2Objects != null) {
      Set<Integer> total_attributes = attribute2Objects.keySet();

      Set<Integer> top_attributes =  new HashSet<>(total_attributes);
      for (Set<Integer> attributes : set_of_attributes) {
        top_attributes.retainAll(attributes);
      }

      int sz = total_attributes.size();

      if (sz >= least_attributes_size && (sz <= most_attributes_size || most_attributes_size < 0)) {
        set_of_attributes.add(total_attributes);
      }

      if (sz >= least_attributes_size && (sz <= most_attributes_size || most_attributes_size < 0)) {
        set_of_attributes.add(top_attributes);
      }
    }
  }

  /**
   * Extract the concepts within particular limits.
   *
   * @param limit_objects_size limit the size of objects of concept
   * @param limit_attributes_size limit the size of attributes of concept
   * @return the concept which has limited size of objects and attributes
   */
  public Set<Concept<O, A>> listConceptsLimit(int limit_objects_size, int limit_attributes_size) {
    Set<Concept<O, A>> concepts_limit = new HashSet<>();

    Set<Set<Integer>> set_of_attributes = new HashSet<>();
    if (simplification != null) {
      for (Map.Entry<Set<Integer>, Set<Integer>> r : simplification.entrySet()) {
        Set<Integer> attributes = r.getKey();

        if (limit_attributes_size <= 0 || attributes.size() <= limit_attributes_size) {
          set_of_attributes.add(attributes);
        }
      }
    }

    attributeIdClosure(set_of_attributes);

    addTopBottomAttributes(set_of_attributes, limit_attributes_size);

    for (Set<Integer> attributes : set_of_attributes) {
      Concept<Integer, Integer> concept_id = computeConceptId(attributes, limit_objects_size, limit_attributes_size);
      Concept<O, A> concept = retransform(concept_id);
      if (concept != null && (!concept_id.getExtent().isEmpty() || !concept_id.getIntent().isEmpty())) {
        concepts_limit.add(concept);
      }
    }

    return concepts_limit;
  }

  public Set<Concept<O, A>> listConceptsLeastMost(int least_objects_size,    int most_objects_size,
                                                  int least_attributes_size, int most_attributes_size) {
    Set<Concept<O, A>> concepts_least_most = new HashSet<>();

    Set<Set<Integer>> set_of_attributes = new HashSet<>();
    if (simplification != null) {
      for (Map.Entry<Set<Integer>, Set<Integer>> r : simplification.entrySet()) {
        Set<Integer> attributes = r.getKey();

        if (attributes.size() >= least_objects_size && attributes.size() <= most_attributes_size) {
          set_of_attributes.add(attributes);
        }
      }
    }

    attributeIdClosure(set_of_attributes);

    addTopBottomAttributes(set_of_attributes, least_attributes_size, most_attributes_size);

    for (Set<Integer> attributes : set_of_attributes) {
      Concept<Integer, Integer> concept_id = computeConceptId(attributes, least_objects_size, most_objects_size,
                                                                          least_attributes_size, most_attributes_size);
      Concept<O, A> concept = retransform(concept_id);
      if (concept != null && (!concept_id.getExtent().isEmpty() || !concept_id.getIntent().isEmpty())) {
        concepts_least_most.add(concept);
      }
    }

    return concepts_least_most;
  }

  public Set<Set<O>> listExtentsLimit(int limit_objects_size, int limit_attributes_size) {
    Set<Set<O>> extents_limit = new HashSet<>();

    Set<Set<Integer>> set_of_attributes = new HashSet<>();
    if (simplification != null) {
      for (Map.Entry<Set<Integer>, Set<Integer>> r : simplification.entrySet()) {
        Set<Integer> attributes = r.getKey();

        if (limit_attributes_size <= 0 || attributes.size() <= limit_attributes_size) {
          set_of_attributes.add(attributes);
        }
      }
    }

    attributeIdClosure(set_of_attributes);

    addTopBottomAttributes(set_of_attributes, limit_attributes_size);

    for (Set<Integer> attributes : set_of_attributes) {
      Concept<Integer, Integer> concept_id = computeConceptId(attributes,
                                                              limit_objects_size,
                                                              limit_attributes_size);
      Set<O> extent = retransform(concept_id.getExtent(), object2O);
      if (extent != null && (!concept_id.getExtent().isEmpty() || !concept_id.getIntent().isEmpty())) {
        extents_limit.add(extent);
      }
    }

    return extents_limit;
  }

  public Set<Set<O>> listExtentsLeastMost(int least_objects_size,    int most_objects_size,
                                          int least_attributes_size, int most_attributes_size) {
    Set<Set<O>> extents_least_most = new HashSet<>();

    Set<Set<Integer>> set_of_attributes = new HashSet<>();
    if (simplification != null) {
      for (Map.Entry<Set<Integer>, Set<Integer>> r : simplification.entrySet()) {
        Set<Integer> attributes = r.getKey();

        if ( attributes.size() >= least_attributes_size &&
            (attributes.size() <= most_attributes_size  || most_attributes_size < 0) ) {
          set_of_attributes.add(attributes);
        }
      }
    }

    attributeIdClosure(set_of_attributes);

    addTopBottomAttributes(set_of_attributes, least_attributes_size, most_attributes_size);

    for (Set<Integer> attributes : set_of_attributes) {
      Concept<Integer, Integer> concept_id = computeConceptId(attributes, least_objects_size, most_objects_size,
                                                                          least_attributes_size, most_attributes_size);
      Set<O> extent = retransform(concept_id.getExtent(), object2O);
      if (extent != null && (!concept_id.getExtent().isEmpty() || !concept_id.getIntent().isEmpty())) {
        extents_least_most.add(extent);
      }
    }

    return extents_least_most;
  }

  public Set<Concept<O, A>> listAllConcepts() {
    return listConceptsLimit(0, 0);
  }

  public void close() {
    if (object2Attributes != null) {
      object2Attributes.clear();
    }

    if (attribute2Objects != null) {
      attribute2Objects.clear();
    }

    if (object2O != null) {
      object2O.clear();
    }

    if (O2Object != null) {
      O2Object.clear();
    }

    if (attribute2A != null) {
      attribute2A.clear();
    }

    if (A2Attribute != null) {
      A2Attribute.clear();
    }

    if (clarified != null) {
      clarified.clear();
    }

    if (domination != null) {
      domination.clear();
    }

    if (simplification != null) {
      simplification.clear();
    }
  }
}
