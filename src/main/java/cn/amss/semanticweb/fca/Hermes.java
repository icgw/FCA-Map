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

import cn.amss.semanticweb.util.Pair;

public class Hermes <O, A>
{
  private Map<Integer, Set<Integer>> object2Attributes = null;
  private Map<Integer, Set<Integer>> attribute2Objects = null;

  private Map<Integer, O> object2O = null;
  private Map<O, Integer> O2Object = null;

  private Map<Integer, A> attribute2A = null;
  private Map<A, Integer> A2Attribute = null;

  private int dividing = 0;

  // Rc: Clarified Relation.
  private Map<Set<Integer>, Set<Integer>> clarified = null;
  // Dom: Domination Relation.
  private Map<Set<Integer>, Set<Integer>> domination = null;
  // Rces: the simplification of Rce, which is the juxtaposition of Rc with Dom.
  private Map<Set<Integer>, Set<Integer>> simplification = null;

  private Set<Pair<Set<O>, Set<A>>> simplifiedConcepts = null;

  public Hermes() {
    object2Attributes = new HashMap<Integer, Set<Integer>>();
    attribute2Objects = new HashMap<Integer, Set<Integer>>();

    object2O = new HashMap<>();
    O2Object = new HashMap<>();

    attribute2A = new HashMap<>();
    A2Attribute = new HashMap<>();
  }

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

  private Pair<Set<Integer>, Set<Integer>> simplifiedConceptIdFrom(Set<Integer> s) {
    Set<Integer> objects    = new HashSet<>();
    Set<Integer> attributes = new HashSet<>();

    if (s == null || s.isEmpty()) return new Pair<>(objects, attributes);

    for (int i : s) {
      if (i < dividing) {
        objects.add(i);
      } else {
        attributes.add(i);
      }
    }

    return new Pair<>(objects, attributes);
  }

  private Pair<Set<O>, Set<A>> retransform(Pair<Set<Integer>, Set<Integer>> pair) {
    Set<O> objects    = new HashSet<>();
    Set<A> attributes = new HashSet<>();

    if (pair == null) return new Pair<>(objects, attributes);

    if (pair.getKey() != null) {
      for (int i : pair.getKey()) {
        if (object2O.containsKey(i)) objects.add(object2O.get(i));
      }
    }

    if (pair.getValue() != null) {
      for (int i : pair.getValue()) {
        if (attribute2A.containsKey(i)) attributes.add(attribute2A.get(i));
      }
    }

    return new Pair<>(objects, attributes);
  }

  private Pair<Set<O>, Set<A>> simplifiedConceptFrom(Set<Integer> s) {
    return retransform(simplifiedConceptIdFrom(s));
  }

  public Set<Pair<Set<O>, Set<A>>> listAllSimplifiedConcepts() {
    if (simplifiedConcepts == null || simplifiedConcepts.isEmpty()) {
      simplifiedConcepts = new HashSet<>();
      for (Map.Entry<Set<Integer>, Set<Integer>> e : simplification.entrySet()) {
        Pair<Set<O>, Set<A>> simplified_concept = simplifiedConceptFrom(e.getValue());
        simplifiedConcepts.add(simplified_concept);
      }
    }
    return simplifiedConcepts;
  }
}
