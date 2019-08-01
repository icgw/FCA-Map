/*
 * MatcherFactory.java
 * Copyright (C) 2019 Guowei Chen <icgw@outlook.com>
 *
 * Distributed under terms of the GPL license.
 */

package cn.amss.semanticweb.matching;

import cn.amss.semanticweb.matching.impl.LexicalMatcherImpl;
import cn.amss.semanticweb.matching.impl.AdditionalPropertyMatcherImpl;

public class MatcherFactory
{
  private MatcherFactory() {
  }

  public static LexicalMatcher createLexicalMatcher() {
    return new LexicalMatcherImpl();
  }

  public static AdditionalPropertyMatcher createAdditionalPropertyMatcher() {
    return new AdditionalPropertyMatcherImpl();
  }

  // TODO: StructuralMatcher..
}
