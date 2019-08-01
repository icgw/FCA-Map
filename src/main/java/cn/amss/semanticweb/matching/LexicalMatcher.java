/*
 * LexicalMatcher.java
 * Copyright (C) 2019 Guowei Chen <icgw@outlook.com>
 *
 * Distributed under terms of the GPL license.
 */

package cn.amss.semanticweb.matching;

import java.util.Set;
import java.util.Map;

import cn.amss.semanticweb.alignment.Mapping;
import cn.amss.semanticweb.model.ResourceWrapper;

public interface LexicalMatcher extends Matcher
{
  public void setExtractType(boolean b_GSH, boolean b_Lattice);

  public void setGSHLimit(int objects_limit, int attributes_limit);

  public void setLatticeLimit(int objects_limit, int attributes_limit);
}
