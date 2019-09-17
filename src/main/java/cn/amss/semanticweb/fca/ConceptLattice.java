/*
 * ConceptLattice.java
 * Copyright (C) 2019 Guowei Chen <icgw@outlook.com>
 *
 * Distributed under terms of the GPL license.
 */

package cn.amss.semanticweb.fca;

import cn.amss.semanticweb.fca.Concept;

import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Arrays;

/**
 * A concept lattice.
 *
 * @author Guowei Chen (icgw@outlook.com)
 */
public class ConceptLattice <O, A>
{
  Map<O, Integer> object2Id    = null;
  Map<A, Integer> attribute2Id = null;

  Set<O> topObjects       = null;
  Set<A> bottomAttributes = null;

  // Map<Concept<Integer, Integer>, Set<Concept<Integer, Integer>>> topDown  = null;
  // Map<Concept<Integer, Integer>, Set<Concept<Integer, Integer>>> bottomUp = null;
  Map<Integer, Concept<O, A>> id2Concept = null;
  Map<Concept<O, A>, Integer> concept2Id = null;

  int topId    = -1;
  int bottomId = -1;

  Map<Integer, Set<Integer>> topDown  = null;
  Map<Integer, Set<Integer>> bottomUp = null;

  public ConceptLattice() {
    object2Id        = new HashMap<>();
    attribute2Id     = new HashMap<>();
    topObjects       = new HashSet<>();
    bottomAttributes = new HashSet<>();
    id2Concept       = new HashMap<>();
    concept2Id       = new HashMap<>();
    topDown          = new HashMap<>();
    bottomUp         = new HashMap<>();
  }

  public ConceptLattice(Set<Concept<O, A>> concepts, Concept<O, A> top, Concept<O, A> bottom) {
    this();
    init(concepts, top, bottom);
  }

  public void init(Set<Concept<O, A>> concepts, Concept<O, A> top, Concept<O, A> bottom) {
    int idx = 0;
    for (Concept<O, A> c : concepts) {
      id2Concept.put(idx, c);
      concept2Id.put(c, idx);
      ++idx;
    }

    // XXX: check if top or bottom not exist in concepts.
    topId    = concept2Id.getOrDefault(top,    -1);
    bottomId = concept2Id.getOrDefault(bottom, -2);
  }

  private boolean isUpDown(Concept<O, A> up, Concept<O, A> down) {
    return up.getExtent().containsAll(down.getExtent());
  }
  private boolean isUpDown(int upId, int downId) {
    return isUpDown(id2Concept.get(upId), id2Concept.get(downId));
  }

  private boolean isDownUp(Concept<O, A> down, Concept<O, A> up) {
    return down.getIntent().containsAll(up.getIntent());
  }

  private boolean isDownUp(int downId, int upId) {
    return isDownUp(id2Concept.get(downId), id2Concept.get(upId));
  }

  public void buildTopDown(Set<Concept<O, A>> concepts, Concept<O, A> top) {
    Queue<Integer> parentIdQueue = new LinkedList<Integer>();
    int topId                    = concept2Id.get(top);
    for (Concept<O, A> c : concepts) {
      parentIdQueue.offer(topId);
      int cId = concept2Id.get(c);
      while ( !parentIdQueue.isEmpty() ) {
        int pId                 = parentIdQueue.poll();
        Set<Integer> childrenId = topDown.get(pId);
        if (childrenId == null || childrenId.isEmpty()) {
          topDown.put(pId, new HashSet<Integer>(Arrays.asList(cId)));
        } else {
          Set<Integer> childrenIdClone = new HashSet<>(childrenId);
          for (int childId : childrenIdClone) {
            if (isUpDown(cId, childId)) {
              childrenId.remove(childId);
              childrenId.add(cId);
              topDown.get(cId).add(childId);
            }
            else if (isDownUp(cId, childId)) {
              parentIdQueue.offer(childId);
            }
          }
        }
      }
    }
    // TODO: need test topDown
  }

  public void buildBottomUp(Set<Concept<O, A>> concepts, Concept<O, A> bottom) {
    // TODO: bottomUp
  }
}
