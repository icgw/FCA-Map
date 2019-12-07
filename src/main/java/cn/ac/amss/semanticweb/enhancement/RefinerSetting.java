/*
 * RefinerSetting.java
 * Copyright (C) 2019 Guowei Chen <icgw@outlook.com>
 *
 * Distributed under terms of the GPL license.
 */

package cn.ac.amss.semanticweb.enhancement;

public interface RefinerSetting
{
  public void setRefineType(boolean b_GSH, boolean b_Lattice);

  public void setGSHLimits(int limit_objects_size, int limit_attributes_size);

  public void setLatticeLimits(int limit_objects_size, int limit_attributes_size);
}
