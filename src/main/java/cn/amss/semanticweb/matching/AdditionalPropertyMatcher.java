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

public interface AdditionalPropertyMatcher extends PropertyMatcher, MatcherSetting
{
  public boolean addInstanceAnchors(Mapping instance_anchors);

  public boolean addPropertyAnchors(Mapping property_anchors);

  public boolean addClassAnchors(Mapping class_anchors);
}
