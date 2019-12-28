/*
 * LexicalMatcher.java
 * Copyright (C) 2019 Guowei Chen <icgw@outlook.com>
 *
 * Distributed under terms of the GPL license.
 */

package cn.ac.amss.semanticweb.matching;

import cn.ac.amss.semanticweb.alignment.Mapping;

public interface LexicalMatcher extends FcaMatcher
{
  public void mapInstances(Mapping mappings);

  public void mapCategories(Mapping mappings);

  public void mapOntProperties(Mapping mappings);

  public void mapDataTypeProperties(Mapping mappings);

  public void mapObjectProperties(Mapping mappings);

  public void mapOntClasses(Mapping mappings);
}
