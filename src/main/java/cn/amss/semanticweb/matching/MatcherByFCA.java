/*
 * MatcherByFCA.java
 * Copyright (C) 2019 Guowei Chen <icgw@outlook.com>
 *
 * Distributed under terms of the GPL license.
 */

package cn.amss.semanticweb.matching;

import java.util.Set;

import cn.amss.semanticweb.fca.Hermes;
import cn.amss.semanticweb.matching.impl.MatcherBase;

public abstract class MatcherByFCA extends MatcherBase
{
  ////////////// Deprecated ///////////////
  protected int m_GSH_objects_limit    = 0;
  protected int m_GSH_attributes_limit = 0;
  /////////////////////////////////////////

  protected int m_GSH_least_size_of_objects    = 1;
  protected int m_GSH_most_size_of_objects     = -1;
  protected int m_GSH_least_size_of_attributes = 0;
  protected int m_GSH_most_size_of_attributes  = -1;

  ////////////// Deprecated ///////////////////
  protected int m_lattice_objects_limit    = 2;
  protected int m_lattice_attributes_limit = 0;
  /////////////////////////////////////////////

  protected int m_Lattice_least_size_of_objects    = 2;
  protected int m_Lattice_most_size_of_objects     = 2;
  protected int m_Lattice_least_size_of_attributes = 0;
  protected int m_Lattice_most_size_of_attributes  = -1;

  protected boolean extract_from_GSH     = true;
  protected boolean extract_from_Lattice = true;

  public void setExtractType(boolean b_GSH, boolean b_Lattice) {
    extract_from_GSH     = b_GSH;
    extract_from_Lattice = b_Lattice;
  }

  @Deprecated
  public void setGSHLimit(int objects_limit, int attributes_limit) {
    m_GSH_objects_limit    = objects_limit;
    m_GSH_attributes_limit = attributes_limit;
  }

  @Deprecated
  public void setLatticeLimit(int objects_limit, int attributes_limit) {
    m_lattice_objects_limit    = objects_limit;
    m_lattice_attributes_limit = attributes_limit;
  }

  @Deprecated
  public void setGSHObjectsLimit(int limit) {
    setGSHLimit(limit, 0);
  }

  @Deprecated
  public void setLatticeObjectsLimit(int limit) {
    setLatticeLimit(limit, 0);
  }

  @Deprecated
  public void setGSHAttributesLimit(int limit) {
    setGSHLimit(0, limit);
  }

  @Deprecated
  public void setLatticeAttributesLimit(int limit) {
    setLatticeLimit(0, limit);
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

  protected <O, A> Set<Set<O>> extractExtentsFromGSH(Hermes<O, A> hermes) {
    return hermes.listSimplifiedExtentsLeastMost(m_GSH_least_size_of_objects,    m_GSH_most_size_of_objects,
                                                 m_GSH_least_size_of_attributes, m_GSH_most_size_of_attributes);
  }

  protected <O, A> Set<Set<O>> extractExtentsFromLattice(Hermes<O, A> hermes) {
    return hermes.listExtentsLeastMost(m_Lattice_least_size_of_objects,    m_Lattice_most_size_of_objects,
                                       m_Lattice_least_size_of_attributes, m_Lattice_most_size_of_attributes);
  }
}
