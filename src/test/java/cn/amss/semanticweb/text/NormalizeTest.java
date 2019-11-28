/*
 * NormalizeTest.java
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

    assertEquals( "I Some Camel Cased String",   Normalize.normalizeCaseStyle(s1) );
    assertEquals( "UNDERSCORED STRING",          Normalize.normalizeCaseStyle(s2) );
    assertEquals( "camel Cased and UNDERSCORED", Normalize.normalizeCaseStyle(s3) );
  }

  @Test
  public void testStripDiacritics() {
    String s1 = "Björn";
    assertEquals( "Bjorn", Normalize.stripDiacritics(s1) );

    String s2 = "vídeo";
    assertEquals( "video", Normalize.stripDiacritics(s2) );
  }

  @Test
  public void testRemoveS() {
    String s1 = "Person's";
    assertEquals( "Person", Normalize.removeS(s1) );
  }

  @Test
  public void testRemoveDisambiguation() {
    String s1 = "Mercury (element)";
    assertEquals( "Mercury", Normalize.removeDisambiguation(s1) );
  }

  @Test
  public void testRemoveSubPages() {
    String s1 = "Kashyyyk/Legends";
    assertEquals( "Kashyyyk", Normalize.removeSubPages(s1) );

    String s2 = "/";
    assertEquals( "/", Normalize.removeSubPages(s2) );
  }

  @Test
  public void testRemoveAppositive() {
    String s1 = "Clone Wars Painting: General Grievous";
    assertEquals( "General Grievous", Normalize.removeAppositive(s1) );
  }
}
