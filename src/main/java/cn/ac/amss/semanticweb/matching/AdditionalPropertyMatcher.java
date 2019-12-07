/*
 * AdditionalPropertyMatcher.java
 * Copyright (C) 2019 Guowei Chen <icgw@outlook.com>
 *
 * Distributed under terms of the GPL license.
 */

package cn.ac.amss.semanticweb.matching;

import cn.ac.amss.semanticweb.alignment.Mapping;

public interface AdditionalPropertyMatcher extends PropertyMatcher, MatcherSetting
{
  public boolean addInstanceAnchors(Mapping instance_anchors);

  public boolean addPropertyAnchors(Mapping property_anchors);

  public boolean addClassAnchors(Mapping class_anchors);

  public void setDuplicateFree(boolean b_source_duplicate_free, boolean b_target_duplicate_free);
}
