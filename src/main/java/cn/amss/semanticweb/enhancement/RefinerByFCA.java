/*
 * RefinerByFCA.java
 * Copyright (C) 2019 Guowei Chen <icgw@outlook.com>
 *
 * Distributed under terms of the GPL license.
 */

package cn.amss.semanticweb.enhancement;

import java.util.Set;
import java.util.HashSet;

import cn.amss.semanticweb.fca.Hermes;
import cn.amss.semanticweb.enhancement.impl.RefinerBase;

public abstract class RefinerByFCA extends RefinerBase
{
  protected int m_GSH_limit_object_size    = 2;
  protected int m_GSH_limit_attribute_size = 0;

  public void setGSHLimits(int limit_objects_size, int limit_attributes_size) {
    m_GSH_limit_object_size    = limit_objects_size;
    m_GSH_limit_attribute_size = limit_attributes_size;
  }

  protected <O, A> Set<O> extractAllObjectInGSHLimit(Hermes<O, A> hermes) {
    Set<O> objects = new HashSet<>();
    for (Set<O> extent : hermes.listSimplifiedExtentsLimit(m_GSH_limit_object_size, m_GSH_limit_attribute_size)) {
      for (O o : extent) {
        objects.add(o);
      }
    }
    return objects;
  }

  // TODO:
}
