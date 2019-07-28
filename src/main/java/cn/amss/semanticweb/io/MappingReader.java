/*
 * MappingReader.java
 * Copyright (C) 2019 Guowei Chen <icgw@outlook.com>
 *
 * Distributed under terms of the GPL license.
 */

package cn.amss.semanticweb.io;

import cn.amss.semanticweb.alignment.Mapping;

public abstract class MappingReader
{
  protected final static String CELL     = "Cell";
  protected final static String ENTITY1  = "entity1";
  protected final static String ENTITY2  = "entity2";
  protected final static String RELATION = "relation";
  protected final static String MEASURE  = "measure";

  protected Mapping m_mappings = null;

  public MappingReader() {
    m_mappings = new Mapping();
  }

  public Mapping getMapping() {
    return m_mappings;
  }

  public int getMappingSize() {
    return m_mappings.size();
  }
}
