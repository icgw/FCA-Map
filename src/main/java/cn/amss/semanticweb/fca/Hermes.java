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

  /**
   * Initiate the map of attribute's id to objects' id and object's id to attributes' id.
   *
   * @param relations add binary relation of formal context
   * @param relations no side effect
   */
  private void init(Set<Pair<Integer, Integer>> relations) {
    for (Pair<Integer, Integer> pair : relations) {
      int object_id = pair.getKey(), attribute_id = pair.getValue();
      object2Attributes.putIfAbsent(object_id, new HashSet<Integer>());
      object2Attributes.get(object_id).add(attribute_id);

      attribute2Objects.putIfAbsent(attribute_id, new HashSet<Integer>());
      attribute2Objects.get(attribute_id).add(object_id);
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
      invert_m.putIfAbsent(r.getValue(), new HashSet<Integer>());
      invert_m.get(r.getValue()).add(r.getKey());
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
      simplification.putIfAbsent(r.getValue(), new HashSet<Integer>());
      simplification.get(r.getValue()).addAll(r.getKey());
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

  private Set<Integer> computeParentAttributes(Set<Integer> s, Set<Integer> attributes) {
    Pair<Set<Integer>, Set<Integer>> simplified_concept = simplifiedConceptIdFrom(s);

    Set<Integer> parent_attributes = new HashSet<>(attributes);
    parent_attributes.removeAll(simplified_concept.getValue());

    return parent_attributes;
  }

  /**
   * Extract the simplified concepts within particular limits.
   *
   * @param limit_objects_size limit the size of objects of simplified concept
   * @param limit_attributes_size  limit the size of attributes of simplified concept
   * @return the simplified concept, where its size of objects and attributes both are less than limit
   */
  public Set<Pair<Set<O>, Set<A>>> listSimplifiedConceptsLimit(int limit_objects_size, int limit_attributes_size) {
    Set<Pair<Set<O>, Set<A>>> simplified_concepts_limit = new HashSet<>();
    if (simplification != null) {
      for (Map.Entry<Set<Integer>, Set<Integer>> e : simplification.entrySet()) {
        Pair<Set<O>, Set<A>> simplified_concept = simplifiedConceptFrom(e.getValue());
        if ((limit_objects_size <= 0 || simplified_concept.getKey().size() < limit_objects_size) &&
            (limit_attributes_size <= 0 || simplified_concept.getValue().size() < limit_attributes_size) &&
            simplified_concept != null) {
          simplified_concepts_limit.add(simplified_concept);
        }
      }
    }
    return simplified_concepts_limit;
  }

  public Set<Pair<Set<O>, Set<A>>> listAllSimplifiedConcepts() {
    return listSimplifiedConceptsLimit(0, 0);
  }

  public Set<Set<O>> listSimplifiedExtentsLimit(int limit_objects_size, int limit_attributes_size) {
    Set<Set<O>> simplified_extents_limit = new HashSet<>();
    if (simplification != null) {
      for (Map.Entry<Set<Integer>, Set<Integer>> e : simplification.entrySet()) {
        Pair<Set<O>, Set<A>> simplified_concept = simplifiedConceptFrom(e.getValue());
        if ((limit_objects_size <= 0 || simplified_concept.getKey().size() < limit_objects_size) &&
            (limit_attributes_size <= 0 || simplified_concept.getValue().size() < limit_attributes_size) &&
            simplified_concept != null) {
          simplified_extents_limit.add(simplified_concept.getKey());
        }
      }
    }
    return simplified_extents_limit;
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
        Set<Integer> s          = r.getValue();

        if (limit_attributes_size <= 0 || attributes.size() < limit_attributes_size) {
          set_of_attributes.add(attributes);
        }

        Set<Integer> parent_attributes = computeParentAttributes(s, attributes);
        if (limit_attributes_size <= 0 || parent_attributes.size() < limit_attributes_size) {
          set_of_attributes.add(parent_attributes);
        }
      }
    }

    if (attribute2Objects != null) {
      Set<Integer> total_attributes = attribute2Objects.keySet();
      if (limit_attributes_size <= 0 || total_attributes.size() < limit_attributes_size) {
        set_of_attributes.add(total_attributes);
      }
    }

    for (Set<Integer> attributes : set_of_attributes) {
      Concept<Integer, Integer> concept_id = computeConceptId(attributes, limit_objects_size, limit_attributes_size);
      Concept<O, A> concept = retransform(concept_id);
      if (concept != null && (!concept_id.getKey().isEmpty() || !concept_id.getValue().isEmpty())) {
        concepts_limit.add(concept);
      }
    }

    return concepts_limit;
  }

  public Set<Set<O>> listExtentsLimit(int limit_objects_size, int limit_attributes_size) {
    Set<Set<O>> extents_limit = new HashSet<>();

    Set<Set<Integer>> set_of_attributes = new HashSet<>();
    if (simplification != null) {
      for (Map.Entry<Set<Integer>, Set<Integer>> r : simplification.entrySet()) {
        Set<Integer> attributes = r.getKey();
        Set<Integer> s          = r.getValue();

        if (limit_attributes_size <= 0 || attributes.size() < limit_attributes_size) {
          set_of_attributes.add(attributes);
        }

        Set<Integer> parent_attributes = computeParentAttributes(s, attributes);
        if (limit_attributes_size <= 0 || parent_attributes.size() < limit_attributes_size) {
          set_of_attributes.add(parent_attributes);
        }
      }
    }

    if (attribute2Objects != null) {
      Set<Integer> total_attributes = attribute2Objects.keySet();
      if (limit_attributes_size <= 0 || total_attributes.size() < limit_attributes_size) {
        set_of_attributes.add(total_attributes);
      }
    }

    for (Set<Integer> attributes : set_of_attributes) {
      Pair<Set<Integer>, Set<Integer>> concept_id = computeConceptId(attributes,
                                                                     limit_objects_size,
                                                                     limit_attributes_size);
      Set<O> extent = retransform(concept_id.getKey(), object2O);
      if (extent != null && (!concept_id.getKey().isEmpty() || !concept_id.getValue().isEmpty())) {
        extents_limit.add(extent);
      }
    }

    return extents_limit;
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
