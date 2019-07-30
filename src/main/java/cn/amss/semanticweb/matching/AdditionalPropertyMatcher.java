/*
 * AdditionalPropertyMatcher.java
 * Copyright (C) 2019 Guowei Chen <icgw@outlook.com>
 *
 * Distributed under terms of the GPL license.
 */

package cn.amss.semanticweb.matching;

import cn.amss.semanticweb.alignment.Mapping;

public interface AdditionalPropertyMatcher extends PropertyMatcher
{
  // TODO: s, t, inst.., outmapping.
  public boolean addInstanceAnchors(Mapping instance_anchors);

  // TODO: clear instance anchors.
  public void clear();
}
