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
  public void testListSimplifiedExtents() {
    Context<Integer, Character> context = new Context<>();
    context.put(1, 'a');
    context.put(1, 'c');
    context.put(1, 'd');
    context.put(1, 'e');
    context.put(1, 'g');

    context.put(2, 'a');
    context.put(2, 'e');
    context.put(2, 'f');
    context.put(2, 'g');

    context.put(3, 'a');
    context.put(3, 'b');
    context.put(3, 'c');
    context.put(3, 'd');
    context.put(3, 'f');
    context.put(3, 'g');

    context.put(4, 'c');
    context.put(4, 'f');

    context.put(5, 'd');

    context.put(6, 'c');
    context.put(6, 'd');

    context.put(7, 'a');
    context.put(7, 'e');
    context.put(7, 'g');

    context.put(8, 'a');
    context.put(8, 'e');
    context.put(8, 'f');
    context.put(8, 'g');

    Set<Set<Integer>> expected = new HashSet<>();
    expected.add(new HashSet<>(Arrays.asList()));
    expected.add(new HashSet<>(Arrays.asList(1)));
    expected.add(new HashSet<>(Arrays.asList(2, 8)));
    expected.add(new HashSet<>(Arrays.asList(3)));
    expected.add(new HashSet<>(Arrays.asList(4)));
    expected.add(new HashSet<>(Arrays.asList(5)));
    expected.add(new HashSet<>(Arrays.asList(6)));
    expected.add(new HashSet<>(Arrays.asList(7)));

    FcaBuilder<Integer, Character> fca = new FcaBuilder<>();
    fca.init(context);
    fca.exec();

    assertEquals( expected, fca.listSimplifiedExtents() );

    fca.clear();
  }

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

  @Test
  public void testListSimplifiedConcepts() {
    Context<Integer, Character> context = new Context<>();
    context.put(1, 'a');
    context.put(1, 'c');
    context.put(1, 'd');
    context.put(1, 'e');
    context.put(1, 'g');

    context.put(2, 'a');
    context.put(2, 'e');
    context.put(2, 'f');
    context.put(2, 'g');

    context.put(3, 'a');
    context.put(3, 'b');
    context.put(3, 'c');
    context.put(3, 'd');
    context.put(3, 'f');
    context.put(3, 'g');

    context.put(4, 'c');
    context.put(4, 'f');

    context.put(5, 'd');

    context.put(6, 'c');
    context.put(6, 'd');

    context.put(7, 'a');
    context.put(7, 'e');
    context.put(7, 'g');

    context.put(8, 'a');
    context.put(8, 'e');
    context.put(8, 'f');
    context.put(8, 'g');

    Set<Concept<Integer, Character>> expected = new HashSet<>();
    expected.add(new Concept<Integer, Character>(
        new HashSet<Integer>(Arrays.asList()),
        new HashSet<Character>(Arrays.asList('a', 'g'))
      )
    );
    expected.add(new Concept<Integer, Character>(
        new HashSet<Integer>(Arrays.asList(5)),
        new HashSet<Character>(Arrays.asList('d'))
      )
    );
    expected.add(new Concept<Integer, Character>(
        new HashSet<Integer>(Arrays.asList()),
        new HashSet<Character>(Arrays.asList('c'))
      )
    );
    expected.add(new Concept<Integer, Character>(
        new HashSet<Integer>(Arrays.asList()),
        new HashSet<Character>(Arrays.asList('f'))
      )
    );
    expected.add(new Concept<Integer, Character>(
        new HashSet<Integer>(Arrays.asList(7)),
        new HashSet<Character>(Arrays.asList('e'))
      )
    );
    expected.add(new Concept<Integer, Character>(
        new HashSet<Integer>(Arrays.asList(6)),
        new HashSet<Character>(Arrays.asList())
      )
    );
    expected.add(new Concept<Integer, Character>(
        new HashSet<Integer>(Arrays.asList(4)),
        new HashSet<Character>(Arrays.asList())
      )
    );
    expected.add(new Concept<Integer, Character>(
        new HashSet<Integer>(Arrays.asList(2, 8)),
        new HashSet<Character>(Arrays.asList())
      )
    );
    expected.add(new Concept<Integer, Character>(
        new HashSet<Integer>(Arrays.asList(1)),
        new HashSet<Character>(Arrays.asList())
      )
    );
    expected.add(new Concept<Integer, Character>(
        new HashSet<Integer>(Arrays.asList(3)),
        new HashSet<Character>(Arrays.asList('b'))
      )
    );

    FcaBuilder<Integer, Character> fca = new FcaBuilder<>();
    fca.init(context);
    fca.exec();

    assertEquals( expected, fca.listSimplifiedConcepts() );

    fca.clear();
  }

  @Test
  public void testListConcepts() {
    Context<String, String> context = new Context<>();
    String[] objects = {
      "Black Windows", "Captain America", "Hela", "Hulk", "Iron Man", "Thanos", "Thor"
    };
    String[] properties = {
      "Asgardian", "Avenger", "Female", "Human", "Infinity Stones User", "Male", "Scientist", "Villain"
    };
    context.put(objects[0], properties[1]);
    context.put(objects[0], properties[2]);
    context.put(objects[0], properties[3]);

    context.put(objects[1], properties[1]);
    context.put(objects[1], properties[3]);
    context.put(objects[1], properties[5]);

    context.put(objects[2], properties[0]);
    context.put(objects[2], properties[2]);
    context.put(objects[2], properties[7]);

    context.put(objects[3], properties[1]);
    context.put(objects[3], properties[3]);
    context.put(objects[3], properties[4]);
    context.put(objects[3], properties[5]);
    context.put(objects[3], properties[6]);

    context.put(objects[4], properties[1]);
    context.put(objects[4], properties[3]);
    context.put(objects[4], properties[4]);
    context.put(objects[4], properties[5]);
    context.put(objects[4], properties[6]);

    context.put(objects[5], properties[4]);
    context.put(objects[5], properties[5]);
    context.put(objects[5], properties[7]);

    context.put(objects[6], properties[0]);
    context.put(objects[6], properties[1]);
    context.put(objects[6], properties[5]);

    Set<Concept<String, String>> expected = new HashSet<>();
    expected.add(new Concept<String, String>(
        new HashSet<String>(Arrays.asList(objects[0], objects[1], objects[2], objects[3], objects[4], objects[5], objects[6])),
        new HashSet<String>()
      )
    );
    expected.add(new Concept<String, String>(
        new HashSet<String>(Arrays.asList(objects[0], objects[1], objects[3], objects[4], objects[6])),
        new HashSet<String>(Arrays.asList(properties[1]))
      )
    );
    expected.add(new Concept<String, String>(
        new HashSet<String>(Arrays.asList(objects[1], objects[3], objects[4], objects[5], objects[6])),
        new HashSet<String>(Arrays.asList(properties[5]))
      )
    );
    expected.add(new Concept<String, String>(
        new HashSet<String>(Arrays.asList(objects[0], objects[1], objects[3], objects[4])),
        new HashSet<String>(Arrays.asList(properties[1], properties[3]))
      )
    );
    expected.add(new Concept<String, String>(
        new HashSet<String>(Arrays.asList(objects[1], objects[3], objects[4], objects[6])),
        new HashSet<String>(Arrays.asList(properties[1], properties[5]))
      )
    );
    expected.add(new Concept<String, String>(
        new HashSet<String>(Arrays.asList(objects[3], objects[4], objects[5])),
        new HashSet<String>(Arrays.asList(properties[4], properties[5]))
      )
    );
    expected.add(new Concept<String, String>(
        new HashSet<String>(Arrays.asList(objects[0], objects[2])),
        new HashSet<String>(Arrays.asList(properties[2]))
      )
    );
    expected.add(new Concept<String, String>(
        new HashSet<String>(Arrays.asList(objects[2], objects[6])),
        new HashSet<String>(Arrays.asList(properties[0]))
      )
    );
    expected.add(new Concept<String, String>(
        new HashSet<String>(Arrays.asList(objects[2], objects[5])),
        new HashSet<String>(Arrays.asList(properties[7]))
      )
    );
    expected.add(new Concept<String, String>(
        new HashSet<String>(Arrays.asList(objects[1], objects[3], objects[4])),
        new HashSet<String>(Arrays.asList(properties[1], properties[3], properties[5]))
      )
    );
    expected.add(new Concept<String, String>(
        new HashSet<String>(Arrays.asList(objects[0])),
        new HashSet<String>(Arrays.asList(properties[1], properties[2], properties[3]))
      )
    );
    expected.add(new Concept<String, String>(
        new HashSet<String>(Arrays.asList(objects[6])),
        new HashSet<String>(Arrays.asList(properties[0], properties[1], properties[5]))
      )
    );
    expected.add(new Concept<String, String>(
        new HashSet<String>(Arrays.asList(objects[5])),
        new HashSet<String>(Arrays.asList(properties[4], properties[5], properties[7]))
      )
    );
    expected.add(new Concept<String, String>(
        new HashSet<String>(Arrays.asList(objects[2])),
        new HashSet<String>(Arrays.asList(properties[0], properties[2], properties[7]))
      )
    );
    expected.add(new Concept<String, String>(
        new HashSet<String>(Arrays.asList(objects[3], objects[4])),
        new HashSet<String>(Arrays.asList(properties[1], properties[3], properties[4], properties[5], properties[6]))
      )
    );
    expected.add(new Concept<String, String>(
        new HashSet<String>(),
        new HashSet<String>(Arrays.asList(properties[0], properties[1], properties[2], properties[3], properties[4], properties[5], properties[6], properties[7]))
      )
    );

    FcaBuilder<String, String> fca = new FcaBuilder<>();
    fca.init(context);
    fca.exec();

    assertEquals( expected, fca.listConcepts() );

    fca.clear();
  }
}
