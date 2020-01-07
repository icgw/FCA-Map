/*
 * Context.java
 * Copyright (C) 2019 Guowei Chen <icgw@outlook.com>
 *
 * Distributed under terms of the GPL license.
 */

package cn.ac.amss.semanticweb.fca;

import cn.ac.amss.semanticweb.util.AbstractTable;

import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Set;

public class Context <O, A> extends AbstractTable<O, A> {
  public Context() {
    super();
  }

  public Context(Context<O, A> context) {
    this.map = new HashMap<>(context.map);
  }

  public Context<A, O> inverse() {
    Context<A, O> inverseContext = new Context<>();
    for (Entry<O, Set<A>> e : this.map.entrySet()) {
      O v = e.getKey();
      for (A k : e.getValue()) {
        inverseContext.put(k, v);
      }
    }
    return inverseContext;
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    for (Entry<O, Set<A>> e : map.entrySet()) {
      sb.append(String.format("%n<<<<<<<%nObject: %s%nAttribute: %s%n>>>>>>>%n",
            e.getKey().toString(), e.getValue().toString()));
    }
    return sb.toString();
  }
}
