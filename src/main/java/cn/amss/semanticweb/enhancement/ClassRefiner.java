/*
 * ClassRefiner.java
 * Copyright (C) 2019 Guowei Chen <icgw@outlook.com>
 *
 * Distributed under terms of the GPL license.
 */

package cn.amss.semanticweb.enhancement;

import cn.amss.semanticweb.alignment.Mapping;

public interface ClassRefiner
{
  public void setRefineType(boolean b_GSH, boolean b_Lattice);

  public void setGSHLimits(int limit_objects_size, int limit_attributes_size);

  public void setLatticeLimits(int limit_objects_size, int limit_attributes_size);

  public void addInstanceAnchors(Mapping instance_anchors);

  public void validateClassAnchors(Mapping class_anchors, Mapping enhanced_class_mappings);
}
