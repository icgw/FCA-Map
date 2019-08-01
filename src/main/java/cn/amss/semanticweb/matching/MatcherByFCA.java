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
  protected int m_GSH_objects_limit    = 0;
  protected int m_GSH_attributes_limit = 0;

  protected int m_lattice_objects_limit    = 3;
  protected int m_lattice_attributes_limit = 7;

  protected boolean extract_from_GSH     = true;
  protected boolean extract_from_Lattice = true;

  public void setExtractType(boolean b_GSH, boolean b_Lattice) {
    extract_from_GSH     = b_GSH;
    extract_from_Lattice = b_Lattice;
  }

  public void setGSHLimit(int objects_limit, int attributes_limit) {
    m_GSH_objects_limit    = objects_limit;
    m_GSH_attributes_limit = attributes_limit;
  }

  public void setLatticeLimit(int objects_limit, int attributes_limit) {
    m_lattice_objects_limit    = objects_limit;
    m_lattice_attributes_limit = attributes_limit;
  }

  public void setGSHObjectsLimit(int limit) {
    setGSHLimit(limit, 0);
  }

  public void setLatticeObjectsLimit(int limit) {
    setLatticeLimit(limit, 0);
  }

  public void setGSHAttributesLimit(int limit) {
    setGSHLimit(0, limit);
  }

  public void setLatticeAttributesLimit(int limit) {
    setLatticeLimit(0, limit);
  }

  protected <O, A> Set<Set<O>> extractExtentsFromGSH(Hermes<O, A> hermes) {
    return hermes.listSimplifiedExtentsLimit(m_GSH_objects_limit, m_GSH_attributes_limit);
  }

  protected <O, A> Set<Set<O>> extractExtentsFromLattice(Hermes<O, A> hermes) {
    return hermes.listExtentsLimit(m_lattice_objects_limit, m_lattice_attributes_limit);
  }
}
