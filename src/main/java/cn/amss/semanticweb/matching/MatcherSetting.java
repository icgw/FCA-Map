/*
 * MatcherSetting.java
 * Copyright (C) 2019 Guowei Chen <icgw@outlook.com>
 *
 * Distributed under terms of the GPL license.
 */

package cn.amss.semanticweb.matching;

import cn.amss.semanticweb.model.OntModelWrapper;

/**
 * The common setting for matcher based on FCA
 *
 * @author Guowei Chen (icgw@outlook.com)
 */
public interface MatcherSetting
{
  /**
   * Set the extracting type
   *
   * @param b_GSH true means utilize the GSH to find correspondences, otherwise not use GSH
   * @param b_Lattice true means utilize the complete lattice to find correspondences, otherwise not use lattice
   */
  public void setExtractType(boolean b_GSH, boolean b_Lattice);

  @Deprecated
  /**
   * Set the limit of GSH extracting method
   *
   * @param objects_limit the limit of objects' size
   * @param attributes_limit the limit of attributes' size
   */
  public void setGSHLimit(int objects_limit, int attributes_limit);

  @Deprecated
  /**
   * Set the limit of complete lattice extracting method
   *
   * @param objects_limit the limit of objects' size
   * @param attributes_limit the limit of attributes' size
   */
  public void setLatticeLimit(int objects_limit, int attributes_limit);

  /**
   * Set the least and most size of GSH extracting method
   *
   * @param objects_least the least size of objects
   * @param objects_most the most size of objects
   * @param attributes_least the least size of attributes
   * @param attributes_most the most size of attributes
   */
  public void setGSHLeastMost(int objects_least, int objects_most, int attributes_least, int attributes_most);

  /**
   * Set the least and most size of lattice extracting method
   *
   * @param objects_least the least size of objects
   * @param objects_most the most size of objects
   * @param attributes_least the least size of attributes
   * @param attributes_most the most size of attributes
   */
  public void setLatticeLeastMost(int objects_least, int objects_most, int attributes_least, int attributes_most);

  /**
   * Set the source and the target
   *
   * @param source the ontology model wrapper of source
   * @param target the ontology model wrapper of target
   */
  public void setSourceTargetOntModelWrapper(OntModelWrapper source, OntModelWrapper target);
}
