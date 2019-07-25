/*
 * MatcherByFCA.java
 * Copyright (C) 2019 Guowei Chen <icgw@outlook.com>
 *
 * Distributed under terms of the GPL license.
 */

package cn.amss.semanticweb.matching;

import java.util.Set;
import java.util.Map;

import cn.amss.semanticweb.fca.Hermes;
import cn.amss.semanticweb.alignment.Mapping;
import cn.amss.semanticweb.model.ResourceWrapper;

public abstract class MatcherByFCA implements Matcher
{
  protected int m_source_id = 0;
  protected int m_target_id = 1;

  public void setSourceId(int source_id) {
    m_source_id = source_id;
  }

  public void setTargetId(int target_id) {
    m_target_id = target_id;
  }

  protected <T> Mapping extractMappingFromGSH(Hermes<ResourceWrapper, T> hermes, int extent_limit, int intent_limit) {
    // TODO:
    return null;
  }

  protected <T> Mapping extractMappingFromLattice(Hermes<ResourceWrapper, T> hermes, int extent_limit, int intent_limit) {
    // TODO:
    return null;
  }
}
