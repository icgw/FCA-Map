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
import cn.amss.semanticweb.model.OntModelWrapper;

public interface Matcher extends InstanceMatcher, PropertyMatcher, ClassMatcher
{
  public void setSourceOntModelWrapper(OntModelWrapper source);

  public void setTargetOntModelWrapper(OntModelWrapper target);

  public void addIntermediateOntModelWrapper(OntModelWrapper intermediate);

  public void close();
}
