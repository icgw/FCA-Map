/*
 * HermesTest.java
 * Copyright (C) 2019 Guowei Chen <icgw@outlook.com>
 *
 * Distributed under terms of the GPL license.
 */

package cn.amss.semanticweb.fca;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import java.util.HashSet;
import java.util.Arrays;

import cn.amss.semanticweb.util.Pair;

public class HermesTest
{
  @Test
  public void testListAllSimplifiedConcepts() {
    Map<Integer, Set<Character>> context = new HashMap<>();
    context.putIfAbsent(1, new HashSet<Character>());
    context.get(1).add('a');
    context.get(1).add('c');
    context.get(1).add('d');
    context.get(1).add('e');
    context.get(1).add('g');

    context.putIfAbsent(2, new HashSet<Character>());
    context.get(2).add('a');
    context.get(2).add('e');
    context.get(2).add('f');
    context.get(2).add('g');

    context.putIfAbsent(3, new HashSet<Character>());
    context.get(3).add('a');
    context.get(3).add('b');
    context.get(3).add('c');
    context.get(3).add('d');
    context.get(3).add('f');
    context.get(3).add('g');

    context.putIfAbsent(4, new HashSet<Character>());
    context.get(4).add('c');
    context.get(4).add('f');

    context.putIfAbsent(5, new HashSet<Character>());
    context.get(5).add('d');

    context.putIfAbsent(6, new HashSet<Character>());
    context.get(6).add('c');
    context.get(6).add('d');

    context.putIfAbsent(7, new HashSet<Character>());
    context.get(7).add('a');
    context.get(7).add('e');
    context.get(7).add('g');

    context.putIfAbsent(8, new HashSet<Character>());
    context.get(8).add('a');
    context.get(8).add('e');
    context.get(8).add('f');
    context.get(8).add('g');

    Hermes<Integer, Character> algorithm = new Hermes<>();
    algorithm.init(context);
    algorithm.compute();

    Set<Pair<Set<Integer>, Set<Character>>> answer = new HashSet<>();
    answer.add(new Pair<Set<Integer>, Set<Character>>(new HashSet<Integer>(Arrays.asList(1)), new HashSet<Character>()));
    answer.add(new Pair<Set<Integer>, Set<Character>>(new HashSet<Integer>(Arrays.asList(2, 8)), new HashSet<Character>()));
    answer.add(new Pair<Set<Integer>, Set<Character>>(new HashSet<Integer>(Arrays.asList(4)), new HashSet<Character>()));
    answer.add(new Pair<Set<Integer>, Set<Character>>(new HashSet<Integer>(Arrays.asList(6)), new HashSet<Character>()));
    answer.add(new Pair<Set<Integer>, Set<Character>>(new HashSet<Integer>(), new HashSet<Character>(Arrays.asList('a', 'g'))));
    answer.add(new Pair<Set<Integer>, Set<Character>>(new HashSet<Integer>(Arrays.asList(3)), new HashSet<Character>(Arrays.asList('b'))));
    answer.add(new Pair<Set<Integer>, Set<Character>>(new HashSet<Integer>(), new HashSet<Character>(Arrays.asList('c'))));
    answer.add(new Pair<Set<Integer>, Set<Character>>(new HashSet<Integer>(Arrays.asList(5)), new HashSet<Character>(Arrays.asList('d'))));
    answer.add(new Pair<Set<Integer>, Set<Character>>(new HashSet<Integer>(Arrays.asList(7)), new HashSet<Character>(Arrays.asList('e'))));
    answer.add(new Pair<Set<Integer>, Set<Character>>(new HashSet<Integer>(), new HashSet<Character>(Arrays.asList('f'))));

    assertEquals ( answer, algorithm.listAllSimplifiedConcepts() );
  }

  @Test
  public void testListAllConcepts() {
    Map<String, Set<String>> context = new HashMap<>();
    context.put("鹰", new HashSet<String>());
    context.get("鹰").add("鸟类");
    context.get("鹰").add("会飞的");
    context.get("鹰").add("食肉的");

    context.put("鹅", new HashSet<String>());
    context.get("鹅").add("鸟类");
    context.get("鹅").add("食草的");

    context.put("马", new HashSet<String>());
    context.get("马").add("哺乳动物");
    context.get("马").add("食草的");
    context.get("马").add("奇蹄的");

    context.put("牛", new HashSet<String>());
    context.get("牛").add("哺乳动物");
    context.get("牛").add("食草的");
    context.get("牛").add("偶蹄的");

    context.put("豹", new HashSet<String>());
    context.get("豹").add("哺乳动物");
    context.get("豹").add("食肉的");

    context.put("狮", new HashSet<String>());
    context.get("狮").add("哺乳动物");
    context.get("狮").add("食肉的");

    Hermes<String, String> algorithm = new Hermes<>();
    algorithm.init(context);
    algorithm.compute();

    Set<Pair<Set<String>, Set<String>>> answer = new HashSet<>();
    answer.add(new Pair<Set<String>, Set<String>>(new HashSet<String>(), new HashSet<String>(Arrays.asList("鸟类", "食肉的", "食草的", "会飞的", "哺乳动物", "偶蹄的", "奇蹄的"))));
    answer.add(new Pair<Set<String>, Set<String>>(new HashSet<String>(Arrays.asList("鹰", "鹅", "豹", "狮", "牛", "马")), new HashSet<String>()));
    answer.add(new Pair<Set<String>, Set<String>>(new HashSet<String>(Arrays.asList("鹰", "鹅")), new HashSet<String>(Arrays.asList("鸟类"))));
    answer.add(new Pair<Set<String>, Set<String>>(new HashSet<String>(Arrays.asList("鹰", "豹", "狮")), new HashSet<String>(Arrays.asList("食肉的"))));
    answer.add(new Pair<Set<String>, Set<String>>(new HashSet<String>(Arrays.asList("鹅", "牛", "马")), new HashSet<String>(Arrays.asList("食草的"))));
    answer.add(new Pair<Set<String>, Set<String>>(new HashSet<String>(Arrays.asList("豹", "狮", "牛", "马")), new HashSet<String>(Arrays.asList("哺乳动物"))));
    answer.add(new Pair<Set<String>, Set<String>>(new HashSet<String>(Arrays.asList("鹰")), new HashSet<String>(Arrays.asList("鸟类", "食肉的", "会飞的"))));
    answer.add(new Pair<Set<String>, Set<String>>(new HashSet<String>(Arrays.asList("鹅")), new HashSet<String>(Arrays.asList("鸟类", "食草的"))));
    answer.add(new Pair<Set<String>, Set<String>>(new HashSet<String>(Arrays.asList("豹", "狮")), new HashSet<String>(Arrays.asList("食肉的", "哺乳动物"))));
    answer.add(new Pair<Set<String>, Set<String>>(new HashSet<String>(Arrays.asList("牛")), new HashSet<String>(Arrays.asList("食草的", "哺乳动物", "偶蹄的"))));
    answer.add(new Pair<Set<String>, Set<String>>(new HashSet<String>(Arrays.asList("牛", "马")), new HashSet<String>(Arrays.asList("食草的", "哺乳动物"))));
    answer.add(new Pair<Set<String>, Set<String>>(new HashSet<String>(Arrays.asList("马")), new HashSet<String>(Arrays.asList("食草的", "哺乳动物", "奇蹄的"))));

    assertEquals( answer, algorithm.listAllConcepts() );
  }
}
