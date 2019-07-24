/*
 * MatcherFactory.java
 * Copyright (C) 2019 Guowei Chen <icgw@outlook.com>
 *
 * Distributed under terms of the GPL license.
 */

package cn.amss.semanticweb.matching;

import cn.amss.semanticweb.matching.Matcher;
import cn.amss.semanticweb.matching.impl.LexicalMatcherImpl;

public class MatcherFactory
{
  private MatcherFactory() {
  }

  public static Matcher createLexicalMatcher() {
    return new LexicalMatcherImpl();
  }

  // TODO: StructuralMatcher..
}
