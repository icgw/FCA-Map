/*
 * PreprocessingTest.java
 * Copyright (C) 2019 Guowei Chen <icgw@outlook.com>
 *
 * Distributed under terms of the GPL license.
 */

package cn.ac.amss.semanticweb.text;

import java.util.Set;
import java.util.HashSet;
import java.util.Arrays;

import org.junit.Test;
import static org.junit.Assert.assertEquals;

public class PreprocessingTest
{
  @Test
  public void testRemoveStopWords() throws Exception {
    Preprocessing.setDefault();

    Set<String> words = new HashSet<>();
    words.add("the");
    words.add("world");
    words.add("of");
    words.add("hello");
    words.add("is");
    words.add("a");
    words.add("and");

    Set<String> expectWords = new HashSet<>();
    expectWords.add("hello");
    expectWords.add("world");

    Preprocessing.removeStopWords(words);

    assertEquals( expectWords, words );
  }

  @Test
  public void testStringTokenize() {
    String s1 = "Hello World! You're a genius.";
    Set<String> tokens = new HashSet<>();
    tokens.add("Hello");
    tokens.add("World");
    tokens.add("You're");
    tokens.add("a");
    tokens.add("genius");

    assertEquals( tokens, new HashSet<String>(Arrays.asList(Preprocessing.stringTokenize(s1))) );
  }
}
