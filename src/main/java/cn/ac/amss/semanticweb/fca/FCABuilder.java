/*
 * FCABuilder.java
 * Copyright (C) 2020 Guowei Chen <icgw@outlook.com>
 *
 * Distributed under terms of the GPL license.
 */

package cn.ac.amss.semanticweb.fca;

import cn.ac.amss.semanticweb.util.AbstractTable;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/**
 * An improved formal concept analaysis builder designed by Guowei Chen.
 *   - AOC-poset algorithm part inspired by Hermes
 *
 * @author Guowei Chen (icgw@outlook.com)
 */
public class FCABuilder <O, A>
{
  private class LookupTable <V> extends AbstractTable<Integer, V> {
    public LookupTable() {
      super();
    }
  }

  private boolean hasInitialized = false;

  private boolean complete = false;

  private int sizeOfConcepts = 0;

  private LookupTable<O> clarifiedObjectIdToObjects       = null;
  private LookupTable<A> clarifiedAttributeIdToAttributes = null;
  private Context<Integer, Integer> clarifiedContext      = null;

  private Context<Set<Integer>, Integer> attributesToAttributeObjectIdClarified = null;
  private Map<Integer, Set<Integer>> attributeToObjectsIdClarified              = null;

  public FCABuilder() {
    clarifiedObjectIdToObjects       = new LookupTable<>();
    clarifiedAttributeIdToAttributes = new LookupTable<>();
    clarifiedContext                 = new Context<>();

    attributeToObjectsIdClarified          = new HashMap<>();
    attributesToAttributeObjectIdClarified = new Context<>();
  }

  public boolean isComplete() {
    return complete;
  }

  public int getSizeOfConcepts() {
    return sizeOfConcepts;
  }

  public void init(Context<O, A> context) {
    getClarifiedContext(context);
    attributeToObjectsIdClarified = new HashMap<>(clarifiedContext.inverse().getMap());

    hasInitialized = true;

    complete = false;

    sizeOfConcepts = 0;
  }

  public void exec() {
    if (!hasInitialized) return;
    computeAttributesToAttributeObject();

    sizeOfConcepts = attributesToAttributeObjectIdClarified.values().size();
  }

  public void clear() {
    clarifiedObjectIdToObjects.clear();
    clarifiedAttributeIdToAttributes.clear();
    clarifiedContext.clear();
    attributesToAttributeObjectIdClarified.clear();
    attributeToObjectsIdClarified.clear();

    hasInitialized = false;

    complete = false;

    sizeOfConcepts = 0;
  }

  public Set<Set<O>> listSimplifiedExtents() {
    return listSimplifiedExtents(0, -1);
  }

  public Set<Set<O>> listSimplifiedExtents(int objectAtLeastSize, int objectAtMostSize) {
    return listSimplifiedExtents(objectAtLeastSize, objectAtMostSize, 0, -1);
  }

  public Set<Set<O>> listSimplifiedExtents(int objectAtLeastSize, int objectAtMostSize,
                                           int attributeAtLeastSize, int attributeAtMostSize) {
    Set<Set<O>> simplifiedExtents = new HashSet<>();
    for (Set<Integer> attributeObjectConcept : attributesToAttributeObjectIdClarified.values()) {
      Set<O> simplifiedExtent = new HashSet<>();
      int sizeOfSimplifiedIntent = 0;
      for (Integer ao : attributeObjectConcept) {
        if (ao < 0) {
          Set<A> attributes = clarifiedAttributeIdToAttributes.get(ao);
          if (null == attributes || attributes.isEmpty()) continue;
          sizeOfSimplifiedIntent += attributes.size();
        } else {
          Set<O> objects = clarifiedObjectIdToObjects.get(ao);
          if (null == objects || objects.isEmpty()) continue;
          simplifiedExtent.addAll(objects);
        }
      }
      if (checkLeastMost(simplifiedExtent.size(), objectAtLeastSize, objectAtMostSize) &&
          checkLeastMost(sizeOfSimplifiedIntent, attributeAtLeastSize, attributeAtMostSize)) {
        simplifiedExtents.add(simplifiedExtent);
      }
    }

    return simplifiedExtents;
  }

  public Set<Concept<O, A>> listSimplifiedConcepts() {
    return listSimplifiedConcepts(0, -1, 0, -1);
  }

  public Set<Concept<O, A>> listSimplifiedConcepts(int objectAtLeastSize, int objectAtMostSize,
                                                   int attributeAtLeastSize, int attributeAtMostSize) {
    Set<Concept<O, A>> simplifiedConcepts = new HashSet<>();
    for (Set<Integer> attributeObjectConcept : attributesToAttributeObjectIdClarified.values()) {
      Set<O> simplifiedExtent = new HashSet<>();
      Set<A> simplifiedIntent = new HashSet<>();
      for (Integer ao : attributeObjectConcept) {
        if (ao < 0) {
          Set<A> attributes = clarifiedAttributeIdToAttributes.get(ao);
          if (null == attributes || attributes.isEmpty()) continue;
          simplifiedIntent.addAll(attributes);
        } else {
          Set<O> objects = clarifiedObjectIdToObjects.get(ao);
          if (null == objects || objects.isEmpty()) continue;
          simplifiedExtent.addAll(objects);
        }
      }
      if (checkLeastMost(simplifiedExtent.size(), objectAtLeastSize, objectAtMostSize) &&
          checkLeastMost(simplifiedIntent.size(), attributeAtLeastSize, attributeAtMostSize)) {
        simplifiedConcepts.add(new Concept<O, A>(simplifiedExtent, simplifiedIntent));
      }
    }

    return simplifiedConcepts;
  }

  public Set<Set<O>> listExtents() {
    return listExtents(0, -1);
  }

  public Set<Set<O>> listExtents(int objectAtLeastSize, int objectAtMostSize) {
    return listExtents(objectAtLeastSize, objectAtMostSize, -1);
  }

  public Set<Set<O>> listExtents(int objectAtLeastSize, int objectAtMostSize, int maximumSizeOfExtents) {
    Set<Set<O>> extents = new HashSet<>();

    Set<Set<Integer>> closureOfAttributes = new HashSet<>();

    complete = closureOfIntersection(closureOfAttributes, attributesToAttributeObjectIdClarified.keySet(), maximumSizeOfExtents);

    for (Set<Integer> s : closureOfAttributes) {
      Set<O> extent = computeExtent(s);
      if (checkLeastMost(extent.size(), objectAtLeastSize, objectAtMostSize)) {
        extents.add(extent);
      }
    }

    return extents;
  }

  public Set<Set<O>> listExtents(int objectAtLeastSize, int objectAtMostSize,
                                 int attributeAtLeastSize, int attributeAtMostSize) {
    return listExtents(objectAtLeastSize, objectAtMostSize, attributeAtLeastSize, attributeAtMostSize, -1);
  }

  public Set<Set<O>> listExtents(int objectAtLeastSize, int objectAtMostSize,
                                 int attributeAtLeastSize, int attributeAtMostSize,
                                 int maximumSizeOfExtents) {
    Set<Set<O>> extents = new HashSet<>();

    Set<Set<Integer>> closureOfAttributes = new HashSet<>();

    complete = closureOfIntersection(closureOfAttributes, attributesToAttributeObjectIdClarified.keySet(),
                                     clarifiedAttributeIdToAttributes, attributeAtLeastSize, attributeAtMostSize,
                                     maximumSizeOfExtents);

    for (Set<Integer> s : closureOfAttributes) {
      Set<O> extent = computeExtent(s);
      int sizeOfIntent = 0;
      for (Integer a : s) {
        Set<A> attributes = clarifiedAttributeIdToAttributes.get(a);
        if (null == attributes || attributes.isEmpty()) continue;
        sizeOfIntent += attributes.size();
      }
      if (checkLeastMost(extent.size(), objectAtLeastSize, objectAtMostSize) &&
          checkLeastMost(sizeOfIntent, attributeAtLeastSize, attributeAtMostSize)) {
        extents.add(extent);
      }
    }

    return extents;
  }

  public Set<Concept<O, A>> listConcepts() {
    return listConcepts(0, -1, 0, -1);
  }

  public Set<Concept<O, A>> listConcepts(int objectAtLeastSize, int objectAtMostSize,
                                         int attributeAtLeastSize, int attributeAtMostSize) {
    return listConcepts(objectAtLeastSize, objectAtMostSize, attributeAtLeastSize, attributeAtMostSize, -1);
  }

  public Set<Concept<O, A>> listConcepts(int objectAtLeastSize, int objectAtMostSize,
                                         int attributeAtLeastSize, int attributeAtMostSize,
                                         int maximumSizeOfConcepts) {
    Set<Concept<O, A>> concepts = new HashSet<>();

    Set<Set<Integer>> closureOfAttributes = new HashSet<>();

    complete = closureOfIntersection(closureOfAttributes, attributesToAttributeObjectIdClarified.keySet(),
                                     clarifiedAttributeIdToAttributes, attributeAtLeastSize, attributeAtMostSize,
                                     maximumSizeOfConcepts);

    for (Set<Integer> s : closureOfAttributes) {
      Set<O> extent = computeExtent(s);
      Set<A> intent = new HashSet<>();
      for (Integer a : s) {
        Set<A> attributes = clarifiedAttributeIdToAttributes.get(a);
        if (null == attributes || attributes.isEmpty()) continue;
        intent.addAll(attributes);
      }
      if (checkLeastMost(extent.size(), objectAtLeastSize, objectAtMostSize) &&
          checkLeastMost(intent.size(), attributeAtLeastSize, attributeAtMostSize)) {
        concepts.add(new Concept<O, A>(extent, intent));
      }
    }

    return concepts;
  }

  /**
   * Check whether the integer number is in an interval [atLeast, atMost], if atLeast is more than atMost, then the
   * interval is [atLeast, positive maximum integer].
   *
   * @param check the number to check
   * @param atLeast the least number
   * @param atMost the most number
   * @return true if the integer is in the interval, else false
   */
  private boolean checkLeastMost(int check, int atLeast, int atMost) {
    if (atLeast > atMost && check >= atLeast) return true;
    return check >= atLeast && check <= atMost;
  }

  private Set<O> computeExtent(Set<Integer> clarifiedAttributeIds) {
    Set<Integer> extent = new HashSet<>();
    if (!clarifiedAttributeIds.iterator().hasNext()) {
      extent.addAll(clarifiedContext.keySet());
    } else {
      Integer a = clarifiedAttributeIds.iterator().next();
      extent.addAll(attributeToObjectsIdClarified.get(a));
    }

    retainAllInOtherSets(extent, clarifiedAttributeIds, attributeToObjectsIdClarified);

    Set<O> newExtent = new HashSet<>();
    for (Integer o : extent) {
      Set<O> s = clarifiedObjectIdToObjects.get(o);
      if (null == s) continue;
      newExtent.addAll(s);
    }

    return newExtent;
  }

  private boolean retainAllInOtherSets(Set<Integer> baseSet, Set<Integer> keyOfSet,
                                       Map<Integer, Set<Integer>> map) {
    if (null == baseSet || null == keyOfSet || null == map) return false;

    boolean modified = false;
    for (Integer k : keyOfSet) {
      Set<Integer> s = map.get(k);
      if (null == s) continue;
      if (baseSet.retainAll(s)) {
        modified = true;
      }
    }

    return modified;
  }

  private void getClarifiedContext(Context<O, A> context) {
    Set<O> objects    = new HashSet<>();
    Set<A> attributes = new HashSet<>();

    Context<A, O> inverseContext = new Context<>();

    for (Entry<O, Set<A>> e : context.entrySet()) {
      O k      = e.getKey();
      Set<A> v = e.getValue();

      if (null == k || null == v) continue;

      objects.add(k);
      attributes.addAll(v);

      for (A a : v) {
        inverseContext.put(a, k);
      }
    }

    Map<O, Integer> objectToId = new HashMap<>();
    Map<Integer, O> idToObject = new HashMap<>();
    Thread t1 = new Thread(
                  new InitBiMap<O>(objectToId, idToObject, objects, 0, 1)
                );

    Map<A, Integer> attributeToId = new HashMap<>();
    Map<Integer, A> idToAttribute = new HashMap<>();
    Thread t2 = new Thread(
                  new InitBiMap<A>(attributeToId, idToAttribute, attributes, -1, -1)
                );

    Map<Set<A>, Integer> attributesToClarifiedObjectId = new HashMap<>();
    Map<Integer, Set<A>> clarifiedObjectIdToAttributes = new HashMap<>();
    Thread t3 = new Thread(
                  new InitBiMap<Set<A>>(attributesToClarifiedObjectId,
                                        clarifiedObjectIdToAttributes,
                                        context.values(), 0, 1)
                );

    Map<Set<O>, Integer> objectsToClarifiedAttributeId = new HashMap<>();
    Map<Integer, Set<O>> clarifiedAttributeIdToObjects = new HashMap<>();
    Thread t4 = new Thread(
                  new InitBiMap<Set<O>>(objectsToClarifiedAttributeId,
                                        clarifiedAttributeIdToObjects,
                                        inverseContext.values(), -1, -1)
                );

    t1.start(); t2.start(); t3.start(); t4.start();

    try {
      t3.join(); t4.join();
    } catch (InterruptedException e) {
      e.printStackTrace();
    }

    Map<O, Integer> objectToClarifiedId    = new HashMap<>();
    Thread t5 = new Thread(
                  new InitClarifiedMap<A, O>(clarifiedObjectIdToObjects,
                                             objectToClarifiedId,
                                             attributesToClarifiedObjectId, context)
                );

    Map<A, Integer> attributeToClarifiedId = new HashMap<>();
    Thread t6 = new Thread(
                  new InitClarifiedMap<O, A>(clarifiedAttributeIdToAttributes,
                                             attributeToClarifiedId,
                                             objectsToClarifiedAttributeId, inverseContext)
                );

    t5.start(); t6.start();

    try {
      t1.join(); t2.join(); t5.join(); t6.join();
    } catch (InterruptedException e) {
      e.printStackTrace();
    }

    for (Entry<O, Set<A>> e : context.entrySet()) {
      Integer k = objectToClarifiedId.get(e.getKey());
      if (null == k || clarifiedContext.containsKey(k)) continue;
      for (A a : e.getValue()) {
        Integer v = attributeToClarifiedId.get(a);
        if (null == v) continue;
        clarifiedContext.put(k, v);
      }
    }
  }

  private void computeAttributesToAttributeObject() {
    Context<Integer, Integer> inverseClarifiedContext = clarifiedContext.inverse();

    Context<Integer, Integer> attributeToItsBroader = new Context<>();

    Thread t1 = new Thread() {
      public void run() {
        for (Entry<Integer, Set<Integer>> e : inverseClarifiedContext.entrySet()) {
          if (!e.getValue().iterator().hasNext()) {
            attributeToItsBroader.put(e.getKey(), inverseClarifiedContext.keySet());
            continue;
          }

          Integer o = e.getValue().iterator().next();
          if (null == o) continue;

          Set<Integer> objects = clarifiedContext.get(o);
          if (null == objects) continue;

          Set<Integer> baseSet = new HashSet<>(objects);
          retainAllInOtherSets(baseSet, e.getValue(), clarifiedContext.getMap());
          attributeToItsBroader.put(e.getKey(), baseSet);
        }
      }
    };

    Thread t2 = new Thread() {
      public void run() {
        for (Entry<Integer, Set<Integer>> e : clarifiedContext.entrySet()) {
          attributesToAttributeObjectIdClarified.put(e.getValue(), e.getKey());
        }
      }
    };

    t1.start(); t2.start();

    try {
      t1.join(); t2.join();
    } catch (InterruptedException e) {
      e.printStackTrace();
    }

    for (Entry<Integer, Set<Integer>> e : attributeToItsBroader.entrySet()) {
      attributesToAttributeObjectIdClarified.put(e.getValue(), e.getKey());
    }
  }

  private boolean closureOfIntersection(Set<Set<Integer>> closure, Set<Set<Integer>> s, int maximumSizeOfSet) {
    if (maximumSizeOfSet >= 0 && s.size() >= maximumSizeOfSet) {
      closure.addAll(s);
      sizeOfConcepts = closure.size();
      return false;
    }

    LookupTable<Set<Integer>> lookup = new LookupTable<>();
    resetLookup(lookup, s);

    if (s.iterator().hasNext()) {
      Set<Integer> base = new HashSet<>(s.iterator().next());
      for (Set<Integer> sub : s) {
        base.retainAll(sub);
      }
      closure.add(base);
    }

    for (Set<Set<Integer>> after = new HashSet<>(s); !after.isEmpty(); ) {
      Set<Set<Integer>> newAfter = new HashSet<>();

      Set<Set<Integer>> s1 = new HashSet<>(), s2 = new HashSet<>();

      Thread t1 = new Thread(new ClosureOfIntersectionHelper(s1, closure, lookup));
      Thread t2 = new Thread(new ClosureOfIntersectionHelper(s2, after,   lookup));

      t1.start(); t2.start();

      try {
        t1.join(); t2.join();
      } catch (InterruptedException e) {
        e.printStackTrace();
      }

      closure.addAll(after);

      s1.removeAll(closure); s2.removeAll(closure);

      newAfter.addAll(s1); newAfter.addAll(s2);

      resetLookup(lookup, newAfter);

      if (maximumSizeOfSet >= 0 && closure.size() + newAfter.size() >= maximumSizeOfSet) {
        closure.addAll(newAfter);
        sizeOfConcepts = closure.size();
        return false;
      }

      after.clear();
      after.addAll(newAfter);
    }

    Set<Integer> all = new HashSet<>();
    for (Set<Integer> sub : s) {
      all.addAll(sub);
    }
    closure.add(all);

    sizeOfConcepts = closure.size();
    return true;
  }

  private boolean checkIsSatisfied(Set<Integer> attributeIds, LookupTable<A> attributesLookup,
                                   int attributeAtLeastSize, int attributeAtMostSize) {
    int sizeOfAttributes = 0;
    for (Integer a : attributeIds) {
      Set<A> attributes = attributesLookup.get(a);
      if (null == attributes) continue;
      sizeOfAttributes += attributes.size();
    }
    return checkLeastMost(sizeOfAttributes, attributeAtLeastSize, attributeAtMostSize);
  }

  private boolean closureOfIntersection(Set<Set<Integer>> closure, Set<Set<Integer>> s, LookupTable<A> attributesLookup,
                                        int attributeAtLeastSize, int attributeAtMostSize, int maximumSizeOfSet) {
    Set<Set<Integer>> newS = new HashSet<>();
    for (Set<Integer> sub : s) {
      if (checkIsSatisfied(sub, attributesLookup, attributeAtLeastSize, attributeAtMostSize)) {
        newS.add(sub);
      }
    }

    if (maximumSizeOfSet >= 0 && newS.size() >= maximumSizeOfSet) {
      closure.addAll(newS);
      sizeOfConcepts = closure.size();
      return false;
    }

    LookupTable<Set<Integer>> lookup = new LookupTable<>();

    resetLookup(lookup, newS);

    if (s.iterator().hasNext()) {
      Set<Integer> base = new HashSet<>(s.iterator().next());
      for (Set<Integer> sub : s) {
        base.retainAll(sub);
      }
      if (checkIsSatisfied(base, attributesLookup, attributeAtLeastSize, attributeAtMostSize)) {
        closure.add(base);
      }
    }

    for (Set<Set<Integer>> after = new HashSet<>(newS); !after.isEmpty(); ) {
      Set<Set<Integer>> newAfter = new HashSet<>();

      Set<Set<Integer>> s1 = new HashSet<>(), s2 = new HashSet<>();

      Thread t1 = new Thread(new ClosureOfIntersectionHelper(s1, closure, lookup));
      Thread t2 = new Thread(new ClosureOfIntersectionHelper(s2, after,   lookup));

      t1.start(); t2.start();

      try {
        t1.join(); t2.join();
      } catch (InterruptedException e) {
        e.printStackTrace();
      }

      closure.addAll(after);

      s1.removeAll(closure); s2.removeAll(closure);

      for (Set<Integer> sub : s1) {
        if (checkIsSatisfied(sub, attributesLookup, attributeAtLeastSize, attributeAtMostSize)) {
          newAfter.add(sub);
        }
      }

      for (Set<Integer> sub : s2) {
        if (checkIsSatisfied(sub, attributesLookup, attributeAtLeastSize, attributeAtMostSize)) {
          newAfter.add(sub);
        }
      }

      resetLookup(lookup, newAfter);

      if (maximumSizeOfSet >= 0 && closure.size() + newAfter.size() >= maximumSizeOfSet) {
        closure.addAll(newAfter);
        sizeOfConcepts = closure.size();
        return false;
      }

      after.clear();
      after.addAll(newAfter);
    }

    Set<Integer> all = new HashSet<>();
    for (Set<Integer> sub : s) {
      all.addAll(sub);
    }

    if (checkIsSatisfied(all, attributesLookup, attributeAtLeastSize, attributeAtMostSize)) {
      closure.add(all);
    }

    sizeOfConcepts = closure.size();
    return true;
  }

  private void resetLookup(LookupTable<Set<Integer>> lookup, Set<Set<Integer>> s) {
    lookup.clear();
    for (Set<Integer> v : s) {
      for (Integer k : v) {
        lookup.put(k, v);
      }
    }
  }

  private Set<Set<Integer>> closureOfIntersectionHelper(Set<Set<Integer>> left, LookupTable<Set<Integer>> rightLookup) {
    Set<Set<Integer>> newIntersectionSets = new HashSet<>();
    if (null == left || left.isEmpty()) return newIntersectionSets;

    for (Set<Integer> s : left) {
      for (Integer i : s) {
        Set<Set<Integer>> otherSets = rightLookup.get(i);
        if (null == otherSets) continue;
        for (Set<Integer> otherSet : otherSets) {
          Set<Integer> newIntersectionSet = new HashSet<>(s);
          newIntersectionSet.retainAll(otherSet);
          if (left.contains(newIntersectionSet)) continue;
          newIntersectionSets.add(newIntersectionSet);
        }
      }
    }

    return newIntersectionSets;
  }

  private class ClosureOfIntersectionHelper implements Runnable {
    Set<Set<Integer>> left                = null;
    LookupTable<Set<Integer>> rightLookup = null;
    Set<Set<Integer>> newIntersectionSets = null;

    public ClosureOfIntersectionHelper(Set<Set<Integer>> newIntersectionSets,
                                       Set<Set<Integer>> left,
                                       LookupTable<Set<Integer>> rightLookup) {
      this.newIntersectionSets = newIntersectionSets;
      this.left                = left;
      this.rightLookup         = rightLookup;
    }

    @Override
    public void run() {
      if (null == left || null == rightLookup || null == newIntersectionSets) return;
      for (Set<Integer> s : left) {
        for (Integer i : s) {
          Set<Set<Integer>> otherSets = rightLookup.get(i);
          if (null == otherSets) continue;
          for (Set<Integer> otherSet : otherSets) {
            Set<Integer> newIntersectionSet = new HashSet<>(s);
            if (newIntersectionSet.retainAll(otherSet) && !left.contains(newIntersectionSet)) {
              // NOTE: when the intersection of `newIntersectionSet' and `otherSet' is changed and
              // `left' do not contain it, then add it into `newIntersectionSets'.
              newIntersectionSets.add(newIntersectionSet);
            }
          }
        }
      }
    }
  }

  private class InitClarifiedMap <K, V> implements Runnable {
    private LookupTable<V> lookup    = null;
    private Map<V, Integer> vToId    = null;
    private Map<Set<K>, Integer> map = null;
    private Context<V, K> context    = null;

    public InitClarifiedMap(LookupTable<V> lookup, Map<V, Integer> vToId, Map<Set<K>, Integer> map, Context<V, K> context) {
      this.lookup  = lookup;
      this.vToId   = vToId;
      this.map     = map;
      this.context = context;
    }

    @Override
    public void run() {
      if (null == lookup || null == map || null == context) return;
      for (Entry<V, Set<K>> e : context.entrySet()) {
        Integer k = map.get(e.getValue());
        if (null == k) continue;
        lookup.put(k, e.getKey());
        vToId.put(e.getKey(), k);
      }
    }

    private InitClarifiedMap() {}
  }

  private class InitBiMap <X> implements Runnable {
    private Map<X, Integer> xToId = null;
    private Map<Integer, X> idToX = null;
    private Collection<X> xs      = null;
    private int startId           = 0;
    private int step              = 0;

    public InitBiMap(Map<X, Integer> xToId, Map<Integer, X> idToX, Collection<X> xs, int startId, int step) {
      this.xToId   = xToId;
      this.idToX   = idToX;
      this.xs      = xs;
      this.startId = startId;
      this.step    = step;
    }

    @Override
    public void run() {
      if (null == xToId || null == idToX || null == xs || 0 == step) return;
      for (X x : xs) {
        if (xToId.containsKey(x)) continue;
        xToId.put(x, startId);
        idToX.put(startId, x);
        startId += step;
      }
    }

    private InitBiMap() {}
  }
}
