/*
 * AbstractMatcherByFCA.java
 * Copyright (C) 2019 Guowei Chen <icgw@outlook.com>
 *
 * Distributed under terms of the GPL license.
 */

package cn.ac.amss.semanticweb.matching.impl;

public abstract class AbstractMatcherByFCA extends AbstractMatcher
{
  protected boolean isEnabledGSH;
  protected boolean isEnabledLattice;

  protected int lowerBoundOfGSHObjectsSize;
  protected int upperBoundOfGSHObjectsSize;

  protected int lowerBoundOfGSHAttributesSize;
  protected int upperBoundOfGSHAttributesSize;

  protected int lowerBoundOfLatticeObjectsSize;
  protected int upperBoundOfLatticeObjectsSize;

  protected int lowerBoundOfLatticeAttributesSize;
  protected int upperBoundOfLatticeAttributesSize;

  protected int maximumSizeOfConcepts;

  public AbstractMatcherByFCA() {
    super();
    isEnabledGSH     = true;
    isEnabledLattice = true;

    lowerBoundOfGSHObjectsSize = 1;
    upperBoundOfGSHObjectsSize = -1;

    lowerBoundOfGSHAttributesSize = 0;
    upperBoundOfGSHAttributesSize = -1;

    lowerBoundOfLatticeObjectsSize = 2;
    upperBoundOfLatticeObjectsSize = 2;

    lowerBoundOfLatticeAttributesSize = 1;
    upperBoundOfLatticeAttributesSize = -1;

    maximumSizeOfConcepts = 300_000;
  }

  public void setExtractType(boolean isEnabledGSH, boolean isEnabledLattice) {
    this.isEnabledGSH = isEnabledGSH;
    this.isEnabledLattice = isEnabledLattice;
  }

  public void setLowerBoundOfGSHObjectsSize(int size) {
    lowerBoundOfGSHObjectsSize = size;
  }

  public void setUpperBoundOfGSHObjectsSize(int size) {
    upperBoundOfGSHObjectsSize = size;
  }

  public void setLowerBoundOfGSHAttributesSize(int size) {
    lowerBoundOfGSHAttributesSize = size;
  }

  public void setUpperBoundOfGSHAttributesSize(int size) {
    upperBoundOfGSHAttributesSize = size;
  }

  public void setLowerBoundOfLatticeObjectsSize(int size) {
    lowerBoundOfLatticeObjectsSize = size;
  }

  public void setUpperBoundOfLatticeObjectsSize(int size) {
    upperBoundOfLatticeObjectsSize = size;
  }

  public void setLowerBoundOfLatticeAttributesSize(int size) {
    lowerBoundOfLatticeAttributesSize = size;
  }

  public void setUpperBoundOfLatticeAttributesSize(int size) {
    upperBoundOfLatticeAttributesSize = size;
  }
}
