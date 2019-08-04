/*
 * Concept.java
 * Copyright (C) 2019 Guowei Chen <icgw@outlook.com>
 *
 * Distributed under terms of the GPL license.
 */

package cn.amss.semanticweb.fca;

import java.util.Set;

import cn.amss.semanticweb.util.Pair;

public class Concept <O, A> extends Pair<Set<O>, Set<A>> {
  public Concept(Set<O> extent, Set<A> intent)  {
    super(extent, intent);
  }

  public Set<O> getExtent() {
    return this.getKey();
  }

  public Set<A> getIntent() {
    return this.getValue();
  }
}
