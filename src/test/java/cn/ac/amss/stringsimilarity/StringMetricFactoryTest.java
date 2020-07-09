/*
 * StringMetricFactoryTest.java
 * Copyright (C) 2020 Guowei Chen <icgw@outlook.com>
 *
 * Distributed under terms of the GPL license.
 */

package cn.ac.amss.stringsimilarity;

import org.junit.Test;
import static org.junit.Assert.assertEquals;

public class StringMetricFactoryTest
{
  @Test
  public void testLevenshteinDistance() {
    StringDistance ld = StringMetricFactory.createLevenshteinDistance();
    assertEquals( 3.0, ld.distance( "horse", "ros" ), 1e-7);
  }

  @Test
  public void testLevenshteinSimilarity() {
    StringSimilarity ls = StringMetricFactory.createLevenshteinSimilarity();
    assertEquals( (1.0 - 3.0 / 8.0), ls.similarity( "horse", "ros" ), 1e-7);
  }
}
