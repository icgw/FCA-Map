/*
 * MatcherFactoryTest.java
 * Copyright (C) 2019 Guowei Chen <icgw@outlook.com>
 *
 * Distributed under terms of the GPL license.
 */

package cn.ac.amss.semanticweb.matching;

import cn.ac.amss.semanticweb.model.ModelStorage;
import cn.ac.amss.semanticweb.alignment.Mapping;
import cn.ac.amss.semanticweb.io.XMLAlignReader;

import org.junit.Test;
import static org.junit.Assert.assertEquals;

public class MatcherFactoryTest
{
  @Test
  public void testLexicalMatcher() {
    ModelStorage source = new ModelStorage("src/test/resources/oaei/conference/Conference.owl");
    ModelStorage target = new ModelStorage("src/test/resources/oaei/conference/ekaw.owl");

    LexicalMatcher lm = MatcherFactory.createLexicalMatcher();

    lm.setSourceTarget(source, target);
    lm.setExtractType(true, true);

    Mapping mappings = new Mapping();
    lm.mapOntClasses(mappings);
    lm.mapObjectProperties(mappings);

    try {
      XMLAlignReader alignReader
        = new XMLAlignReader("src/test/resources/oaei/conference/alignment/conference-ekaw.rdf");
      assertEquals( alignReader.getMapping(), mappings );
    } catch (Exception e) {
      System.err.println("Caught Exception: " + e.getMessage());
    }

    source.clear();
    target.clear();
  }

  @Test
  public void testStructuralMatcher() {
    // TODO:
  }
}
