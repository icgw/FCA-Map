/*
 * ClassMatcher.java
 * Copyright (C) 2019 Guowei Chen <icgw@outlook.com>
 *
 * Distributed under terms of the GPL license.
 */

package cn.amss.semanticweb.matching;

import org.apache.jena.rdf.model.Resource;

import java.util.Set;

import cn.amss.semanticweb.alignment.Mapping;

public interface ClassMatcher
{
  public <T extends Resource> void matchClasses(Set<T> sources, Set<T> targets, Mapping mappings);
}
