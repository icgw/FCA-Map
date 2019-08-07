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

  protected int m_Lattice_limit_object_size    = 2;
  protected int m_Lattice_limit_attribute_size = 0;

  protected boolean m_extract_from_GSH     = true;
  protected boolean m_extract_from_Lattice = false;

  public void setRefineType(boolean b_GSH, boolean b_Lattice) {
    m_extract_from_GSH     = b_GSH;
    m_extract_from_Lattice = b_Lattice;
  }

  public void setGSHLimits(int limit_objects_size, int limit_attributes_size) {
    m_GSH_limit_object_size    = limit_objects_size;
    m_GSH_limit_attribute_size = limit_attributes_size;
  }

  public void setLatticeLimits(int limit_objects_size, int limit_attributes_size) {
    m_Lattice_limit_object_size    = limit_objects_size;
    m_Lattice_limit_attribute_size = limit_attributes_size;
  }

  protected <O, A> Set<O> extractAllObjectInGSHLimit(Hermes<O, A> hermes) {
    Set<O> objects = new HashSet<>();
    for (Set<O> simplified_extent : hermes.listSimplifiedExtentsLimit(m_GSH_limit_object_size, m_GSH_limit_attribute_size)) {
      objects.addAll(simplified_extent);
    }
    return objects;
  }

  protected <O, A> Set<O> extractAllObjectInLatticeLimit(Hermes<O, A> hermes) {
    Set<O> objects = new HashSet<>();
    for (Set<O> extent : hermes.listExtentsLimit(m_Lattice_limit_object_size, m_Lattice_limit_attribute_size)) {
      objects.addAll(extent);
    }
    return objects;
  }

  // TODO:
}
