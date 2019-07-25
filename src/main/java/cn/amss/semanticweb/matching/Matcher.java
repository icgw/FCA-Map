/*
 * Matcher.java
 * Copyright (C) 2019 Guowei Chen <icgw@outlook.com>
 *
 * Distributed under terms of the GPL license.
 */

package cn.amss.semanticweb.matching;

import org.apache.jena.rdf.model.Resource;
import java.util.Set;

import cn.amss.semanticweb.alignment.Mapping;

public interface Matcher
{
  public void matchResources(Set<Resource> sources, Set<Resource> targets, Mapping mappings);

  public void matchInstances(Set<Resource> sources, Set<Resource> targets, Mapping mappings);

  public void matchProperties(Set<Resource> sources, Set<Resource> targets, Mapping mappings);

  public void matchClasses(Set<Resource> sources, Set<Resource> targets, Mapping mappings);

}
