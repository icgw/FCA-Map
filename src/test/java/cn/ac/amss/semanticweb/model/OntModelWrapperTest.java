/*
 * OntModelWrapperTest.java
 * Copyright (C) 2019 Guowei Chen <icgw@outlook.com>
 *
 * Distributed under terms of the GPL license.
 */

package cn.ac.amss.semanticweb.model;

import org.junit.Test;

public class OntModelWrapperTest
{
  @Test
  public void testOntModelWrapper() {
    String conference_url = "https://raw.githubusercontent.com/icgw/FCA-Map/master/src/test/resources/oaei/conference/Conference.owl";
    OntModelWrapper conf = new OntModelWrapper(conference_url);

    String ekaw_url = "https://raw.githubusercontent.com/icgw/FCA-Map/master/src/test/resources/oaei/conference/ekaw.owl";
    OntModelWrapper ekaw = new OntModelWrapper(ekaw_url);
  }
}
