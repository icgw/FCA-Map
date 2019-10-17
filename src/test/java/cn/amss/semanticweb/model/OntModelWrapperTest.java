/*
 * OntModelWrapperTest.java
 * Copyright (C) 2019 Guowei Chen <icgw@outlook.com>
 *
 * Distributed under terms of the GPL license.
 */

package cn.amss.semanticweb.model;

import org.junit.Test;

public class OntModelWrapperTest
{
  @Test
  public void testOntModelWrapper() {
    String conference_url = "http://oaei.ontologymatching.org/2019/conference/data/Conference.owl";
    OntModelWrapper conf = new OntModelWrapper(conference_url);

    String ekaw_url = "http://oaei.ontologymatching.org/2019/conference/data/ekaw.owl";
    OntModelWrapper ekaw = new OntModelWrapper(ekaw_url);
  }
}
