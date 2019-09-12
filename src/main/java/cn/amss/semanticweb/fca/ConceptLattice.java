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
import java.util.Queue;

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

  Map<Concept<Integer, Integer>, Set<Concept<Integer, Integer>>> topDown  = null;
  Map<Concept<Integer, Integer>, Set<Concept<Integer, Integer>>> bottomUp = null;

  public ConceptLattice() {
    object2Id        = new HashMap<>();
    attribute2Id     = new HashMap<>();
    topObjects       = new HashSet<>();
    bottomAttributes = new HashSet<>();
    topDown          = new HashMap<>();
    bottomUp         = new HashMap<>();
  }

  private boolean isUpDown(Concept<O, A> up, Concept<O, A> down) {
    return up.getExtent().containsAll(down.getExtent());
  }

  private boolean isDownUp(Concept<O, A> down, Concept<O, A> up) {
    return down.getIntent().containsAll(up.getIntent());
  }

  public void buildTopDown(Set<Concept<O, A>> concepts, Concept<O, A> top) {
    // TODO: topDown
  }

  public void buildBottomUp(Set<Concept<O, A>> concepts, Concept<O, A> bottom) {
    // TODO: bottomUp
  }
}
