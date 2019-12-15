/*
 * MappingTest.java
 * Copyright (C) 2019 Guowei Chen <icgw@outlook.com>
 *
 * Distributed under terms of the GPL license.
 */

package cn.ac.amss.semanticweb.alignment;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class MappingTest
{
  @Test
  public void testToClosure() {
    Mapping m = new Mapping();
    m.add("http://example.org/a", "http://example.org/1");
    m.add("http://example.org/a", "http://example.org/2");
    m.add("http://example.org/b", "http://example.org/1");
    m.add("http://example.org/c", "http://example.org/3");

    Mapping mClosure = new Mapping();
    mClosure.add("http://example.org/a", "http://example.org/1");
    mClosure.add("http://example.org/a", "http://example.org/2");
    mClosure.add("http://example.org/b", "http://example.org/1");
    mClosure.add("http://example.org/c", "http://example.org/3");
    mClosure.add("http://example.org/b", "http://example.org/2");

    assertEquals( mClosure, m.toClosure() );
  }
}
