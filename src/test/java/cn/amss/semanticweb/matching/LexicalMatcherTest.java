/*
 * LexicalMatcherTest.java
 * Copyright (C) 2019 Guowei Chen <icgw@outlook.com>
 *
 * Distributed under terms of the GPL license.
 */

package cn.amss.semanticweb.matching;

import cn.amss.semanticweb.model.OntModelWrapper;
import cn.amss.semanticweb.alignment.Mapping;
import cn.amss.semanticweb.io.XMLAlignReader;
import cn.amss.semanticweb.matching.MatcherFactory;
import cn.amss.semanticweb.matching.LexicalMatcher;

import java.io.InputStream;

import org.junit.Test;
import static org.junit.Assert.assertEquals;

public class LexicalMatcherTest
{
  @Test
  public void testLexicalMatcher() throws Exception {
    InputStream inSource = this.getClass().getResourceAsStream("/oaei/conference/Conference.owl");
    InputStream inTarget = this.getClass().getResourceAsStream("/oaei/conference/ekaw.owl");

    OntModelWrapper source = new OntModelWrapper(inSource);
    OntModelWrapper target = new OntModelWrapper(inTarget);

    LexicalMatcher lm = MatcherFactory.createLexicalMatcher();

    lm.setExtractType(true, true);

    lm.setSourceOntModelWrapper(source);
    lm.setTargetOntModelWrapper(target);

    Mapping mappings = new Mapping();
    lm.mapOntClasses(mappings);
    lm.mapDatatypeProperties(mappings);
    lm.mapObjectProperties(mappings);

    InputStream inAlignment = this.getClass().getResourceAsStream("/oaei/conference/alignment/conference-ekaw.rdf");

    XMLAlignReader alignReader = new XMLAlignReader(inAlignment);

    assertEquals (alignReader.getMapping(), mappings);
  }
}
