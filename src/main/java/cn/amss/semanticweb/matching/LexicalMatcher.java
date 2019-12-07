/*
 * LexicalMatcher.java
 * Copyright (C) 2019 Guowei Chen <icgw@outlook.com>
 *
 * Distributed under terms of the GPL license.
 */

package cn.amss.semanticweb.matching;

public interface LexicalMatcher extends Matcher, MatcherSetting
{
  public void defaultConfig();
}
