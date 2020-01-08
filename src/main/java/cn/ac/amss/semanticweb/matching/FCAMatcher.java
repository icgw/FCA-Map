/*
 * FCAMatcher.java
 * Copyright (C) 2019 Guowei Chen <icgw@outlook.com>
 *
 * Distributed under terms of the GPL license.
 */

package cn.ac.amss.semanticweb.matching;

import cn.ac.amss.semanticweb.model.ModelStorage;

public interface FCAMatcher
{
  public void setSourceTarget(ModelStorage source, ModelStorage target);

  public boolean addOtherModel(ModelStorage other);

  public void clearOtherModels();

  public void setExtractType(boolean isEnabledGSH, boolean isEnabledLattice);

  public void setLowerBoundOfGSHObjectsSize(int size);

  public void setUpperBoundOfGSHObjectsSize(int size);

  public void setLowerBoundOfGSHAttributesSize(int size);

  public void setUpperBoundOfGSHAttributesSize(int size);

  public void setLowerBoundOfLatticeObjectsSize(int size);

  public void setUpperBoundOfLatticeObjectsSize(int size);

  public void setLowerBoundOfLatticeAttributesSize(int size);

  public void setUpperBoundOfLatticeAttributesSize(int size);

  public void setMaximumSizeOfConcepts(int size);
}
