/*
 * NormalzeTest.java
 * Copyright (C) 2019 Guowei Chen <icgw@outlook.com>
 *
 * Distributed under terms of the GPL license.
 */

package cn.amss.semanticweb.text;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class NormalizeTest
{
  @Test
  public void testNormalizeCaseStyle() {
    String s1 = "ISomeCamelCasedString";
    String s2 = "UNDERSCORED_STRING";
    String s3 = "camelCased_and_UNDERSCORED";

    assertEquals( Normalize.normalizeCaseStyle(s1), "I Some Camel Cased String" );
    assertEquals( Normalize.normalizeCaseStyle(s2), "UNDERSCORED STRING" );
    assertEquals( Normalize.normalizeCaseStyle(s3), "camel Cased and UNDERSCORED" );
  }
}
