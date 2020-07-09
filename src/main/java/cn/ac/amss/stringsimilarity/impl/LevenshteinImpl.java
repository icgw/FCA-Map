/*
 * LevenshteinImpl.java
 * Copyright (C) 2020 Guowei Chen <icgw@outlook.com>
 *
 * Distributed under terms of the GPL license.
 */
package cn.ac.amss.stringsimilarity.impl;

import cn.ac.amss.stringsimilarity.StringDistance;

/**
 * The original paper is
 *
 *    Levenshtein, Vladimir I. 1966,
 *    Binary codes capable of correcting deletions, insertions, and reversals.
 *
 * Improved using dynamic programming.
 *
 * @author Guowei Chen (icgw@outlook.com)
 */
public class LevenshteinImpl implements StringDistance
{
  private LevenshteinImpl() {}

  public final double distance(final String s1, final String s2) {
    if (null == s1 || null == s2)
      throw new NullPointerException("s1 or s2 in Levenshtein Distance Function must not be null");

    if (s1.length() > s2.length()) return distance(s2, s1);

    if (s1.equals(s2)) return .0;

    int length1 = s1.length(), length2 = s2.length();

    if (0 == length1) return length2;

    if (0 == length2) return length1;

    int[] dp = new int[length2 + 1];
    for (int i = 0; i <= length2; ++i) dp[i] = i;

    for (int i = 0; i < length1; ++i) {
      int prev = dp[0];
      dp[0]    = i + 1;
      for (int j = 0; j < length2; ++j) {
        int temp = dp[j + 1];
        if (s1.charAt(i) == s2.charAt(j)) {
          dp[j + 1] = prev;
        }
        else {
          dp[j + 1] = Math.min(
            prev + 1,       // Cost of substitution
            Math.min(
              dp[j] + 1,    // Cost of remove
              dp[j + 1] + 1 // Cost of insertion
            )
          );
        }
        prev = temp;
      }
    }

    return dp[length2];
  }
}

