/*
 * MatcherByFCA.java
 * Copyright (C) 2019 Guowei Chen <icgw@outlook.com>
 *
 * Distributed under terms of the GPL license.
 */

package cn.amss.semanticweb.matching;

import java.util.Set;
import java.util.Map;

import cn.amss.semanticweb.util.Pair;
import cn.amss.semanticweb.fca.Hermes;
import cn.amss.semanticweb.alignment.Mapping;
import cn.amss.semanticweb.model.ResourceWrapper;
import cn.amss.semanticweb.matching.impl.MatcherBase;

public abstract class MatcherByFCA extends MatcherBase implements Matcher
{
  protected <O, A> Set<Pair<O, O>> extractMappingFromGSH(Hermes<O, A> hermes, int extent_limit, int intent_limit) {
    // TODO:
    return null;
  }

  protected <O, A> Set<Pair<O, O>> extractMappingFromLattice(Hermes<O, A> hermes, int extent_limit, int intent_limit) {
    // TODO:
    return null;
  }
}
