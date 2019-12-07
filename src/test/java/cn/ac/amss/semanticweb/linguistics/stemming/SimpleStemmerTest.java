/*
 * SimpleStemmerTest.java
 * Copyright (C) 2019 Guowei Chen <icgw@outlook.com>
 *
 * Distributed under terms of the GPL license.
 */

package cn.ac.amss.semanticweb.linguistics.stemming;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class SimpleStemmerTest
{
  @Test
  public void testMutate() {
    SimpleStemmer stemmer = new SimpleStemmer();

    assertEquals( "achieve", stemmer.mutate( "achieving" ) );
    assertEquals( "achieve", stemmer.mutate( "achieved" ) );
    assertEquals( "achieve", stemmer.mutate( "achieves" ) );
    assertEquals( "achievement", stemmer.mutate( "achievements" ) );
    assertEquals( "achiever", stemmer.mutate( "achievers" ) );
  }
}
