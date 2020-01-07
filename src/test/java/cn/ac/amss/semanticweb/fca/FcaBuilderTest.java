/*
 * FcaBuilderTest.java
 * Copyright (C) 2020 Guowei Chen <icgw@outlook.com>
 *
 * Distributed under terms of the GPL license.
 */

package cn.ac.amss.semanticweb.fca;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import java.util.Set;
import java.util.HashSet;
import java.util.TreeSet;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;

public class FcaBuilderTest
{
  @Test
  public void testListExtents() {
    Context<String, String> context = new Context<>();
    context.put("鹰", "鸟类");
    context.put("鹰", "会飞的");
    context.put("鹰", "食肉的");

    context.put("鹅", "鸟类");
    context.put("鹅", "食草的");

    context.put("马", "哺乳动物");
    context.put("马", "食草的");
    context.put("马", "奇蹄的");

    context.put("牛", "哺乳动物");
    context.put("牛", "食草的");
    context.put("牛", "偶蹄的");

    context.put("豹", "哺乳动物");
    context.put("豹", "食肉的");

    context.put("狮", "哺乳动物");
    context.put("狮", "食肉的");

    Set<Set<String>> expected = new HashSet<>();
    expected.add(new HashSet<>());
    expected.add(new HashSet<>(Arrays.asList("鹰", "鹅", "豹", "狮", "牛", "马")));
    expected.add(new HashSet<>(Arrays.asList("鹰", "鹅")));
    expected.add(new HashSet<>(Arrays.asList("鹰", "豹", "狮")));
    expected.add(new HashSet<>(Arrays.asList("鹅", "牛", "马")));
    expected.add(new HashSet<>(Arrays.asList("豹", "狮", "牛", "马")));
    expected.add(new HashSet<>(Arrays.asList("鹰")));
    expected.add(new HashSet<>(Arrays.asList("鹅")));
    expected.add(new HashSet<>(Arrays.asList("豹", "狮")));
    expected.add(new HashSet<>(Arrays.asList("牛")));
    expected.add(new HashSet<>(Arrays.asList("牛", "马")));
    expected.add(new HashSet<>(Arrays.asList("马")));

    FcaBuilder<String, String> fca = new FcaBuilder<>();
    fca.init(context);
    fca.exec();

    assertEquals( expected, fca.listExtents() );

    fca.clear();
  }
}
