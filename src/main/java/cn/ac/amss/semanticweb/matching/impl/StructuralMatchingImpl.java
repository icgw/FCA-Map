/*
 * StructuralMatchingImpl.java
 * Copyright (C) 2019 Guowei Chen <icgw@outlook.com>
 *
 * Distributed under terms of the GPL license.
 */

package cn.ac.amss.semanticweb.matching.impl;

import cn.ac.amss.semanticweb.util.Pair;

public class StructuralMatchingImpl
{
  public StructuralMatchingImpl() {
  }

  private class AnchorIdPair extends Pair<Integer, Integer> {
    public AnchorIdPair(int key, int value) {
      super(key, value);
    }
  }
}
