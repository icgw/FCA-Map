/*
 * AdditionalPropertyMatcherImpl.java
 * Copyright (C) 2019 Guowei Chen <icgw@outlook.com>
 *
 * Distributed under terms of the GPL license.
 */

package cn.amss.semanticweb.matching.impl;

import org.apache.jena.rdf.model.Resource;
import java.util.Set;

import cn.amss.semanticweb.alignment.Mapping;
import cn.amss.semanticweb.matching.MatcherByFCA;
import cn.amss.semanticweb.matching.AdditionalPropertyMatcher;

public class AdditionalPropertyMatcherImpl extends MatcherByFCA implements AdditionalPropertyMatcher
{
  public AdditionalPropertyMatcherImpl() {
  }

  @Override
  public void matchProperties(Set<Resource> sources, Set<Resource> targets, Mapping mappings) {
    // TODO:
  }

  @Override
  public boolean addInstanceAnchors(Mapping instance_anchors) {
    // TODO:
    return true;
  }
}
