/*
 * FcaBuilder.java
 * Copyright (C) 2020 Guowei Chen <icgw@outlook.com>
 *
 * Distributed under terms of the GPL license.
 */

package cn.ac.amss.semanticweb.fca;

import cn.ac.amss.semanticweb.util.Table;

import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.List;
import java.util.ArrayList;
import java.util.SortedSet;
import java.util.HashSet;
import java.util.HashMap;
import java.util.TreeSet;
import java.util.Comparator;
import java.util.Iterator;

/**
 * An improved formal concept analaysis builder designed by Guowei Chen.
 *   - AOC-poset algorithm part inspired by Hermes
 *
 * @author Guowei Chen (icgw@outlook.com)
 */
public class FcaBuilder <O, A>
{
  private class LookupTable <V> extends Table<Integer, V> {
    public LookupTable() {
      super();
    }
  }

  private boolean hasInitialized = false;

  private LookupTable<O> clarifiedObjectIdToObjects       = null;
  private LookupTable<A> clarifiedAttributeIdToAttributes = null;
  private Context<Integer, Integer> clarifiedContext      = null;

  private Context<Set<Integer>, Integer> attributesToAttributeObjectOnTheirClarifiedId = null;
  private Map<Integer, Set<Integer>> attributeToObjectsIdClarified                     = null;

  public FcaBuilder() {
    clarifiedObjectIdToObjects       = new LookupTable<>();
    clarifiedAttributeIdToAttributes = new LookupTable<>();
    clarifiedContext                 = new Context<>();

    attributeToObjectsIdClarified                 = new HashMap<>();
    attributesToAttributeObjectOnTheirClarifiedId = new Context<>();
  }

  public void init(Context<O, A> context) {
    getClarifiedContext(context);
    attributeToObjectsIdClarified = new HashMap<>(clarifiedContext.inverse().getMap());
    hasInitialized = true;
  }

  public void exec() {
    if (!hasInitialized) return;
    computeAttributesToAttributeObject();
  }

  public void clear() {
    clarifiedObjectIdToObjects.clear();
    clarifiedAttributeIdToAttributes.clear();
    clarifiedContext.clear();
    attributesToAttributeObjectOnTheirClarifiedId.clear();
    attributeToObjectsIdClarified.clear();
    hasInitialized = false;
  }

  public Set<Set<O>> listSimplifiedExtents() {
    return listSimplifiedExtents(0, -1);
  }

  public Set<Set<O>> listExtents() {
    return listExtents(0, -1, -1);
  }

  public Set<Set<O>> listSimplifiedExtents(int objectAtLeastSize, int objectAtMostSize) {
    Set<Set<O>> simplifiedExtents = new HashSet<>();
    for (Set<Integer> attributeObjectConcept : attributesToAttributeObjectOnTheirClarifiedId.values()) {
      Set<O> simplifiedExtent = new HashSet<>();
      for (Integer ao : attributeObjectConcept) {
        if (ao < 0) continue;
        Set<O> objects = clarifiedObjectIdToObjects.get(ao);
        if (null == objects || objects.isEmpty()) continue;
        simplifiedExtent.addAll(objects);
      }
      if (checkLeastMost(simplifiedExtent.size(), objectAtLeastSize, objectAtMostSize)) {
        simplifiedExtents.add(simplifiedExtent);
      }
    }

    return simplifiedExtents;
  }

  private boolean checkLeastMost(int check, int atLeast, int atMost) {
    if (atLeast > atMost) return true;
    return check >= atLeast && check <= atMost;
  }

  public Set<Set<O>> listExtents(int objectAtLeastSize, int objectAtMostSize, int maximumSizeOfExtents) {
    Set<Set<O>> extents = new HashSet<>();

    Set<Set<Integer>> attributesSet = new HashSet<>(attributesToAttributeObjectOnTheirClarifiedId.keySet());

    Set<Set<Integer>> closureOfAttributes
      = closureOfIntersection(attributesToAttributeObjectOnTheirClarifiedId.keySet(), maximumSizeOfExtents);

    Set<Integer> all = new HashSet<>(clarifiedAttributeIdToAttributes.keySet());
    closureOfAttributes.add(all);

    Set<Integer> base = new HashSet<>(all);
    for (Set<Integer> s : clarifiedContext.values()) {
      base.retainAll(s);
    }
    closureOfAttributes.add(base);

    for (Set<Integer> s : closureOfAttributes) {
      Set<O> extent = computeExtent(s);
      if (checkLeastMost(extent.size(), objectAtLeastSize, objectAtMostSize)) {
        extents.add(extent);
      }
    }

    return extents;
  }

  private Set<O> computeExtent(Set<Integer> clarifiedAttributeIds) {
    Set<Integer> extent = new HashSet<>(clarifiedContext.keySet());

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
    boolean modified = false;
    if (null == baseSet || null == keyOfSet || null == map) return modified;

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

      objects.add(k);
      attributes.addAll(v);

      for (A a : v) {
        inverseContext.put(a, k);
      }
    }

    Map<O, Integer> objectToId = new HashMap<>();
    Map<Integer, O> idToObject = new HashMap<>();
    Thread t1 = new Thread(new InitBiMap<O>(objectToId, idToObject, objects, 0, 1));

    Map<A, Integer> attributeToId = new HashMap<>();
    Map<Integer, A> idToAttribute = new HashMap<>();
    Thread t2 = new Thread(new InitBiMap<A>(attributeToId, idToAttribute, attributes, -1, -1));

    Map<Set<A>, Integer> attributesToClarifiedObjectId = new HashMap<>();
    Map<Integer, Set<A>> clarifiedObjectIdToAttributes = new HashMap<>();
    Thread t3 = new Thread(new InitBiMap<Set<A>>(attributesToClarifiedObjectId,
                                                 clarifiedObjectIdToAttributes,
                                                 context.values(), 0, 1));

    Map<Set<O>, Integer> objectsToClarifiedAttributeId = new HashMap<>();
    Map<Integer, Set<O>> clarifiedAttributeIdToObjects = new HashMap<>();
    Thread t4 = new Thread(new InitBiMap<Set<O>>(objectsToClarifiedAttributeId,
                                                 clarifiedAttributeIdToObjects,
                                                 inverseContext.values(), -1, -1));

    t1.start(); t2.start(); t3.start(); t4.start();

    try {
      t3.join(); t4.join();
    } catch (InterruptedException e) {
      e.printStackTrace();
    }

    Map<O, Integer> objectToClarifiedId    = new HashMap<>();
    Thread t5 = new Thread(new InitClarifiedMap<A, O>(clarifiedObjectIdToObjects,
                                                      objectToClarifiedId,
                                                      attributesToClarifiedObjectId, context));

    Map<A, Integer> attributeToClarifiedId = new HashMap<>();
    Thread t6 = new Thread(new InitClarifiedMap<O, A>(clarifiedAttributeIdToAttributes,
                                                      attributeToClarifiedId,
                                                      objectsToClarifiedAttributeId, inverseContext));
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

    SortedSet<Entry<Integer, Set<Integer>>> sortedInverseContext = new TreeSet<>(
      new Comparator<Entry<Integer, Set<Integer>>>() {
        @Override
        public int compare(Entry<Integer, Set<Integer>> e1, Entry<Integer, Set<Integer>> e2) {
          Set<Integer> s1 = e1.getValue(), s2 = e2.getValue();
          if (s1.size() == s2.size()) {
            Set<Integer> sort1 = new TreeSet<>(s1), sort2 = new TreeSet<>(s2);
            for (Iterator<Integer> it1 = sort1.iterator(), it2 = sort2.iterator(); it1.hasNext(); ) {
              Integer i1 = it1.next(), i2 = it2.next();
              if (i1 == i2) continue;
              return i1 - i2;
            }
            return 0;
          }
          return e1.getValue().size() - e2.getValue().size();
        }
      }
    );

    List<Entry<Integer, Set<Integer>>> sortedInverseContextList = new ArrayList<>();
    for (Entry<Integer, Set<Integer>> e : inverseClarifiedContext.entrySet()) {
      sortedInverseContext.add(e);
    }

    for (Entry<Integer, Set<Integer>> e : sortedInverseContext) {
      sortedInverseContextList.add(e);
    }

    Context<Integer, Integer> attributeToItsBroader = new Context<>();

    int n = sortedInverseContextList.size();
    for (int i = 0; i < n; ++i) {
      Integer k = sortedInverseContextList.get(i).getKey();
      SortedSet<Integer> objects = new TreeSet<>(sortedInverseContextList.get(i).getValue());
      attributeToItsBroader.put(k, k);
      for (int j = i + 1; j < n; ++j) {
        SortedSet<Integer> otherObjects = new TreeSet<>(sortedInverseContextList.get(j).getValue());
        if (objects.size() == otherObjects.size() ||
            objects.first() > otherObjects.last() ||
            objects.last() < otherObjects.first() ||
            !otherObjects.containsAll(objects)) continue;
        attributeToItsBroader.put(k, sortedInverseContextList.get(j).getKey());
      }
    }

    for (Entry<Integer, Set<Integer>> e : attributeToItsBroader.entrySet()) {
      attributesToAttributeObjectOnTheirClarifiedId.put(e.getValue(), e.getKey());
    }

    for (Entry<Integer, Set<Integer>> e : clarifiedContext.entrySet()) {
      attributesToAttributeObjectOnTheirClarifiedId.put(e.getValue(), e.getKey());
    }
  }

  private Set<Set<Integer>> closureOfIntersection(Set<Set<Integer>> s, int maximumSizeOfSet) {
    if (maximumSizeOfSet >= 0 && s.size() >= maximumSizeOfSet) {
      return s;
    }

    LookupTable<Set<Integer>> lookup = new LookupTable<>();
    resetLookup(lookup, s);

    Set<Set<Integer>> closure = new HashSet<>();
    for (Set<Set<Integer>> after = new HashSet<>(s); !after.isEmpty(); ) {
      Set<Set<Integer>> newAfter = new HashSet<>();

      newAfter.addAll(closureOfIntersectionHelper(closure, lookup));
      newAfter.addAll(closureOfIntersectionHelper(after, lookup));
      newAfter.removeAll(closure);

      resetLookup(lookup, newAfter);

      closure.addAll(after);

      if (maximumSizeOfSet >= 0 && closure.size() >= maximumSizeOfSet) return closure;

      after.clear();
      after.addAll(newAfter);
    }

    return closure;
  }

  private void resetLookup(LookupTable<Set<Integer>> lookup, Set<Set<Integer>> s) {
    lookup.clear();
    for (Set<Integer> v : s) {
      for (Integer k : v) {
        lookup.put(k, v);
      }
    }
  }

  private Set<Set<Integer>> closureOfIntersectionHelper(Set<Set<Integer>> left,
                                                        LookupTable<Set<Integer>> rightLookup) {
    Set<Set<Integer>> newIntersectionSets = new HashSet<>();
    if (null == left || left.isEmpty()) return newIntersectionSets;

    for (Set<Integer> s : left) {
      for (Integer i : s) {
        Set<Set<Integer>> otherSets = rightLookup.get(i);
        if (null == otherSets) continue;
        for (Set<Integer> otherSet : otherSets) {
          Set<Integer> newIntersectionSet = new HashSet<>(s);
          newIntersectionSet.retainAll(otherSet);
          newIntersectionSets.add(newIntersectionSet);
        }
      }
    }

    newIntersectionSets.removeAll(left);
    return newIntersectionSets;
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
