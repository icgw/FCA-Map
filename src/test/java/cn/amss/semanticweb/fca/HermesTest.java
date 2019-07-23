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
}
