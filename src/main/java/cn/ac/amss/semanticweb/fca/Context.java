/*
 * Context.java
 * Copyright (C) 2019 Guowei Chen <icgw@outlook.com>
 *
 * Distributed under terms of the GPL license.
 */

package cn.ac.amss.semanticweb.fca;

import cn.ac.amss.semanticweb.util.Table;

import java.util.Map.Entry;
import java.util.Set;

public class Context <O, A> extends Table<O, A> {
  public Context() {
    super();
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
