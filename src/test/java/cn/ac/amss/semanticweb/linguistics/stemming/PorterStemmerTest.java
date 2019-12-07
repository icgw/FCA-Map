/*
 * PorterStemmerTest.java
 * Copyright (C) 2019 Guowei Chen <icgw@outlook.com>
 *
 * Distributed under terms of the GPL license.
 */

package cn.ac.amss.semanticweb.linguistics.stemming;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class PorterStemmerTest
{
  @Test
  public void testMutate() throws IOException {
    PorterStemmer stemmer = new PorterStemmer();

    InputStream sample = this.getClass().getResourceAsStream("/porter/voc-50l.txt");
    BufferedReader br1 = new BufferedReader(new InputStreamReader(sample));

    InputStream output = this.getClass().getResourceAsStream("/porter/output-50l.txt");
    BufferedReader br2 = new BufferedReader(new InputStreamReader(output));

    String s = null, o = null;
    while ( (s = br1.readLine()) != null && (o = br2.readLine()) != null ) {
      assertEquals ( o, stemmer.mutate(s) );
    }
  }
}
