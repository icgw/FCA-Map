/*
 * StringSimilarity.java
 * Copyright (C) 2020 Guowei Chen <icgw@outlook.com>
 *
 * Distributed under terms of the GPL license.
 */
package cn.ac.amss.stringsimilarity;

/**
 *
 * @author Guowei Chen (icgw@outlook.com)
 */
public interface StringSimilarity
{
  double similarity(String s1, String s2);
}
