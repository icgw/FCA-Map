/*
 * StructuralMatchingImpl.java
 * Copyright (C) 2019 Guowei Chen <icgw@outlook.com>
 *
 * Distributed under terms of the GPL license.
 */

package cn.ac.amss.semanticweb.matching.impl;

import cn.ac.amss.semanticweb.util.Pair;
import cn.ac.amss.semanticweb.constant.MatchingSpec.Owner;
import cn.ac.amss.semanticweb.model.PlainRDFNode;

import org.apache.jena.rdf.model.Resource;

public class StructuralMatchingImpl
{
  public StructuralMatchingImpl() {
  }

  private class AnchorIdPair extends Pair<Integer, Integer> {
    public AnchorIdPair(int key, int value) {
      super(key, value);
    }
  }

  private <T extends Resource> PlainRDFNode getPlainRDFNode(T r, Owner o) {
    return new PlainRDFNode(r.getURI(), o);
  }
}
