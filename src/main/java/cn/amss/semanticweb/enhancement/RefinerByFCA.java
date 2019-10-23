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
  // XXX: wait to improve.
  ////////////// Deprecated ///////////////////
  protected int m_GSH_limit_object_size    = 1;
  protected int m_GSH_limit_attribute_size = 0;
  /////////////////////////////////////////////

  protected int m_GSH_least_size_of_objects    = 1;
  protected int m_GSH_most_size_of_objects     = 1;
  protected int m_GSH_least_size_of_attributes = 1;
  protected int m_GSH_most_size_of_attributes  = -1;

  ////////////// Deprecated ///////////////////////
  protected int m_Lattice_limit_object_size    = 1;
  protected int m_Lattice_limit_attribute_size = 0;
  /////////////////////////////////////////////////

  protected int m_Lattice_least_size_of_objects    = 1;
  protected int m_Lattice_most_size_of_objects     = 1;
  protected int m_Lattice_least_size_of_attributes = 1;
  protected int m_Lattice_most_size_of_attributes  = -1;

  protected boolean m_refine_from_GSH     = true;
  protected boolean m_refine_from_Lattice = false;

  public void setRefineType(boolean b_GSH, boolean b_Lattice) {
    m_refine_from_GSH     = b_GSH;
    m_refine_from_Lattice = b_Lattice;
  }

  @Deprecated
  public void setGSHLimits(int limit_objects_size, int limit_attributes_size) {
    m_GSH_limit_object_size    = limit_objects_size;
    m_GSH_limit_attribute_size = limit_attributes_size;
  }

  @Deprecated
  public void setLatticeLimits(int limit_objects_size, int limit_attributes_size) {
    m_Lattice_limit_object_size    = limit_objects_size;
    m_Lattice_limit_attribute_size = limit_attributes_size;
  }

  public void setGSHLeastMost(int objects_least, int objects_most, int attributes_least, int attributes_most) {
    m_GSH_least_size_of_objects    = objects_least;
    m_GSH_most_size_of_objects     = objects_most;
    m_GSH_least_size_of_attributes = attributes_least;
    m_GSH_most_size_of_attributes  = attributes_most;
  }

  public void setLatticeLeastMost(int objects_least, int objects_most, int attributes_least, int attributes_most) {
    m_Lattice_least_size_of_objects    = objects_least;
    m_Lattice_most_size_of_objects     = objects_most;
    m_Lattice_least_size_of_attributes = attributes_least;
    m_Lattice_most_size_of_attributes  = attributes_most;
  }

  protected <O, A> Set<O> refineAllObjectInGSHLimit(Hermes<O, A> hermes) {
    Set<O> objects = new HashSet<>();
    for (Set<O> simplified_extent : hermes.listSimplifiedExtentsLeastMost(m_GSH_least_size_of_objects,    m_GSH_most_size_of_objects,
                                                                          m_GSH_least_size_of_attributes, m_GSH_most_size_of_attributes)) {
      objects.addAll(simplified_extent);
    }
    return objects;
  }

  protected <O, A> Set<O> refineAllObjectInLatticeLimit(Hermes<O, A> hermes) {
    Set<O> objects = new HashSet<>();
    for (Set<O> extent : hermes.listExtentsLeastMost(m_Lattice_least_size_of_objects,    m_Lattice_most_size_of_objects,
                                                     m_Lattice_least_size_of_attributes, m_Lattice_most_size_of_attributes)) {
      objects.addAll(extent);
    }
    return objects;
  }

  // TODO:
}
