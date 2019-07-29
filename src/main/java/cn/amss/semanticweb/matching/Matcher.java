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
import cn.amss.semanticweb.matching.InstanceMatcher;
import cn.amss.semanticweb.matching.PropertyMatcher;
import cn.amss.semanticweb.matching.ClassMatcher;

public interface Matcher extends InstanceMatcher, PropertyMatcher, ClassMatcher
{
  public void matchResources(Set<Resource> sources, Set<Resource> targets, Mapping mappings);
}
