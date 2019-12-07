/*
 * Concept.java
 * Copyright (C) 2019 Guowei Chen <icgw@outlook.com>
 *
 * Distributed under terms of the GPL license.
 */

package cn.ac.amss.semanticweb.fca;

import java.util.Set;

import cn.ac.amss.semanticweb.util.Pair;


/**
 * A (formal) concept is a pair (A, B), where A is the extent and B is the intent.
 *
 * @author Guowei Chen (icgw@outlook.com)
 */
public class Concept <O, A> extends Pair<Set<O>, Set<A>> {

  /**
   * A concept of a complete lattice.
   *
   * @param extent add extent to this concept
   * @param intent add intent to this concept
   */
  public Concept(Set<O> extent, Set<A> intent)  {
    super(extent, intent);
  }

  /**
   * @return the extent of this concept
   */
  public Set<O> getExtent() {
    return this.getKey();
  }

  /**
   * @return the intent of this concept
   */
  public Set<A> getIntent() {
    return this.getValue();
  }
}
