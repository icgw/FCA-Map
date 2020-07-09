/*
 * StringMetricFactory.java
 * Copyright (C) 2020 Guowei Chen <icgw@outlook.com>
 *
 * Distributed under terms of the GPL license.
 */
package cn.ac.amss.stringsimilarity;

import cn.ac.amss.stringsimilarity.impl.LevenshteinDistanceImpl;
import cn.ac.amss.stringsimilarity.impl.LevenshteinSimilarityImpl;

/**
 *
 *
 * @author Guowei Chen (icgw@outlook.com)
 */
public class StringMetricFactory
{
  private StringMetricFactory() {}

  public static StringDistance createLevenshteinDistance() {
    return new LevenshteinDistanceImpl();
  }

  public static StringSimilarity createLevenshteinSimilarity() {
    return new LevenshteinSimilarityImpl();
  }
}

