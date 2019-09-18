/*
 * ConceptLatticeTest.java
 * Copyright (C) 2019 Guowei Chen <icgw@outlook.com>
 *
 * Distributed under terms of the GPL license.
 */

package cn.amss.semanticweb.fca;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import java.util.Set;
import java.util.HashSet;
import java.util.Map;
import java.util.HashMap;
import java.util.Arrays;

public class ConceptLatticeTest
{
  @Test
  public void testConceptLattice() {
    Set<Concept<String, String>> concepts = new HashSet<>();

    Concept<String, String> c0 = new Concept<String, String>(new HashSet<String>(Arrays.asList("ewe", "mare", "foal", "calf", "filly", "lamb", "colt", "cow", "stallion", "bull", "ram")), new HashSet<String>());
    Concept<String, String> c1 = new Concept<String, String>(new HashSet<String>(Arrays.asList("ewe", "mare", "cow", "stallion", "bull", "ram")), new HashSet<String>(Arrays.asList("adult")));
    Concept<String, String> c2 = new Concept<String, String>(new HashSet<String>(Arrays.asList("foal", "calf", "filly", "lamb", "colt")), new HashSet<String>(Arrays.asList("juvenile")));
    Concept<String, String> c3 = new Concept<String, String>(new HashSet<String>(Arrays.asList("colt", "stallion", "bull", "ram")), new HashSet<String>(Arrays.asList("male")));
    Concept<String, String> c4 = new Concept<String, String>(new HashSet<String>(Arrays.asList("ewe", "mare", "filly", "cow")), new HashSet<String>(Arrays.asList("female")));
    Concept<String, String> c5 = new Concept<String, String>(new HashSet<String>(Arrays.asList("ewe", "mare", "cow")), new HashSet<String>(Arrays.asList("female", "adult")));
    Concept<String, String> c6 = new Concept<String, String>(new HashSet<String>(Arrays.asList("stallion", "bull", "ram")), new HashSet<String>(Arrays.asList("adult", "male")));
    Concept<String, String> c7 = new Concept<String, String>(new HashSet<String>(Arrays.asList("colt")), new HashSet<String>(Arrays.asList("juvenile", "male")));
    Concept<String, String> c8 = new Concept<String, String>(new HashSet<String>(Arrays.asList("filly")), new HashSet<String>(Arrays.asList("juvenile", "female")));
    Concept<String, String> c9 = new Concept<String, String>(new HashSet<String>(), new HashSet<String>(Arrays.asList("juvenile", "female", "adult", "male")));

    concepts.add(c0);
    concepts.add(c1);
    concepts.add(c2);
    concepts.add(c3);
    concepts.add(c4);
    concepts.add(c5);
    concepts.add(c6);
    concepts.add(c7);
    concepts.add(c8);
    concepts.add(c9);

    ConceptLattice<String, String> cl = new ConceptLattice<>(concepts);
    cl.buildTopDown();

    Map<Concept<String, String>, Set<Concept<String, String>>> answer = new HashMap<>();
    answer.put(c0, new HashSet<>(Arrays.asList(c1, c2, c3, c4)));
    answer.put(c1, new HashSet<>(Arrays.asList(c5, c6)));
    answer.put(c2, new HashSet<>(Arrays.asList(c7, c8)));
    answer.put(c3, new HashSet<>(Arrays.asList(c6, c7)));
    answer.put(c4, new HashSet<>(Arrays.asList(c5, c8)));
    answer.put(c5, new HashSet<>(Arrays.asList(c9)));
    answer.put(c6, new HashSet<>(Arrays.asList(c9)));
    answer.put(c7, new HashSet<>(Arrays.asList(c9)));
    answer.put(c8, new HashSet<>(Arrays.asList(c9)));

    assertEquals( answer, cl.getSupSubConcepts() );
  }
}
