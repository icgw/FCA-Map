/*
 * NormalizeTest.java
 * Copyright (C) 2019 Guowei Chen <icgw@outlook.com>
 *
 * Distributed under terms of the GPL license.
 */

package cn.ac.amss.semanticweb.text;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class NormalizeTest
{
  @Test
  public void testNormCamelSnakeDelimiter() {
    String s1 = "ISomeCamelCasedString";
    String s2 = "UNDERSCORED_STRING";
    String s3 = "camelCased_and_UNDERSCORED";

    assertEquals( "I Some Camel Cased String",   Normalize.normCamelSnakeDelimiter(s1) );
    assertEquals( "UNDERSCORED STRING",          Normalize.normCamelSnakeDelimiter(s2) );
    assertEquals( "camel Cased and UNDERSCORED", Normalize.normCamelSnakeDelimiter(s3) );
  }

  @Test
  public void testStripDiacritics() {
    String s1 = "Björn";
    assertEquals( "Bjorn", Normalize.stripDiacritics(s1) );

    String s2 = "vídeo";
    assertEquals( "video", Normalize.stripDiacritics(s2) );
  }

  @Test
  public void removeContraction() {
    String s1 = "let's I'm they're He's I've He'd I'll o'clock";
    assertEquals( "let I they He I He I clock", Normalize.removeContraction(s1) );
  }

  @Test
  public void testNormAcronym() {
    String s1 = "U.S.";
    assertEquals( "US", Normalize.normAcronym(s1) );

    String s2 = "Ph.D.";
    assertEquals( "PhD", Normalize.normAcronym(s2) );
  }

  @Test
  public void testNormDigit() {
    String s1 = "Volume 1 10 2";
    assertEquals( "Volume 1-10-2", Normalize.normDigit(s1) );
  }
}
