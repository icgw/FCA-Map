/*
 * AdditionalPropertyMatcher.java
 * Copyright (C) 2019 Guowei Chen <icgw@outlook.com>
 *
 * Distributed under terms of the GPL license.
 */

package cn.amss.semanticweb.matching;

import java.util.Set;
import org.apache.jena.rdf.model.Resource;

import cn.amss.semanticweb.alignment.Mapping;

public interface AdditionalPropertyMatcher extends PropertyMatcher
{
  public void setExtractType(boolean b_GSH, boolean b_Lattice);

  public void setGSHLimit(int objects_limit, int attributes_limit);

  public void setLatticeLimit(int objects_limit, int attributes_limit);

  public boolean addInstanceAnchors(Mapping instance_anchors);

  public void matchProperties(Set<Resource> sources, Set<Resource> targets, Mapping instance_anchors, Mapping mappings);

  public void clear();
}
