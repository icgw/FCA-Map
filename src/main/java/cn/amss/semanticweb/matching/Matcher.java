/*
 * Matcher.java
 * Copyright (C) 2019 Guowei Chen <icgw@outlook.com>
 *
 * Distributed under terms of the GPL license.
 */

package cn.amss.semanticweb.matching;

import cn.amss.semanticweb.model.OntModelWrapper;

public interface Matcher extends InstanceMatcher, PropertyMatcher, ClassMatcher
{
  public void setSourceOntModelWrapper(OntModelWrapper source);

  public void setTargetOntModelWrapper(OntModelWrapper target);

  public void setSourceTargetOntModelWrapper(OntModelWrapper source, OntModelWrapper target);

  public void addIntermediateOntModelWrapper(OntModelWrapper intermediate);

  public void close();
}
