/*
 * ModelStorageTest.java
 * Copyright (C) 2019 Guowei Chen <icgw@outlook.com>
 *
 * Distributed under terms of the GPL license.
 */

package cn.ac.amss.semanticweb.model;

import org.junit.Test;

public class ModelStorageTest
{
  @Test
  public void testModelStorage() {
    ModelStorage localModel = new ModelStorage("oaei/conference/Conference.owl");
    localModel.clear();

    // ModelStorage remoteModel = new ModelStorage("https://raw.githubusercontent.com/icgw/FCA-Map/master/src/test/resources/oaei/conference/ekaw.owl");
    // remoteModel.clear();
  }
}
