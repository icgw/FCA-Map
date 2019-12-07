/*
 * Stemmer.java
 * Copyright (C) 2019 Guowei Chen <icgw@outlook.com>
 *
 * Distributed under terms of the GPL license.
 */

package cn.ac.amss.semanticweb.linguistics.stemming;

/**
 * The interface of stemming method class
 *
 * @author Guowei Chen (icgw@outlook.com)
 */
public interface Stemmer
{
  public String mutate(String value);
}
