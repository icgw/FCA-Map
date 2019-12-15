/*
 * OntModelWrapperTest.java
 * Copyright (C) 2019 Guowei Chen <icgw@outlook.com>
 *
 * Distributed under terms of the GPL license.
 */

package cn.ac.amss.semanticweb.model;

import java.io.InputStream;

import org.junit.Test;

public class OntModelWrapperTest
{
  @Test
  public void testOntModelWrapper() {
    InputStream inSource = this.getClass().getResourceAsStream("/oaei/conference/Conference.owl");
    OntModelWrapper conf = new OntModelWrapper(inSource);

    InputStream inTarget = this.getClass().getResourceAsStream("/oaei/conference/ekaw.owl");
    OntModelWrapper ekaw = new OntModelWrapper(inTarget);
  }
}
