/*
 * RDFAlignReader.java
 * Copyright (C) 2019 Guowei Chen <icgw@outlook.com>
 *
 * Distributed under terms of the GPL license.
 */

package cn.amss.semanticweb.oaei.reader;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URL;

public class RDFAlignReader
{
  private static final String CELL     = "Cell";
  private static final String ENTITY1  = "entity1";
  private static final String ENTITY2  = "entity2";
  private static final String RELATION = "relation";
  private static final String MEASURE  = "measure";

  public RDFAlignReader(URL url) throws Exception {
    this(url.openStream());
  }

  public RDFAlignReader(String file_path) throws Exception {
    this(new FileInputStream(new File(file_path)));
  }

  public RDFAlignReader(InputStream in) {
    // TODO:
  }
}
