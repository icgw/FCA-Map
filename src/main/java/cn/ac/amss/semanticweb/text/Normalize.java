/*
 * Normalize.java
 * Copyright (C) 2019 Guowei Chen <icgw@outlook.com>
 *
 * Distributed under terms of the GPL license.
 */

package cn.ac.amss.semanticweb.text;

import java.util.regex.Pattern;
import java.text.Normalizer;

/**
 * Transform a text string into a normal style
 *
 * @author Guowei Chen (icgw@outlook.com)
 */
public class Normalize
{
  private static final String RE_CAMEL_OR_SNAKE_DELIMITER = "(?<!^|[A-Z_\\-])(?=[A-Z])|(?<!^|[_\\-])(?=[A-Z][a-z])|_";
  private static final String RE_CONTRACTION              = "'s\\b|'m\\b|'re\\b|'ve|'d\\b|'ll\\b|(?<= )o'\\b";
  private static final String RE_DIACRITICS_AND_FRIENDS   = "[\\p{InCombiningDiacriticalMarks}\\p{IsLm}\\p{IsSk}]+";
  private static final String RE_ACRONYM_WITH_STOP        = "(?<=[a-zA-Z])\\.";
  private static final String RE_NORM_DIGIT               = "(?<=\\d)\\W+(?=\\d)";
  private static final String RE_DIGIT_SUPPRESSION        = "[^a-zA-Z]*\\d+[^a-zA-Z]*";

  private static final Pattern CAMEL_OR_SNAKE_DELIMITER = Pattern.compile(RE_CAMEL_OR_SNAKE_DELIMITER);
  private static final Pattern CONTRACTION              = Pattern.compile(RE_CONTRACTION);
  private static final Pattern DIACRITICS_AND_FRIENDS   = Pattern.compile(RE_DIACRITICS_AND_FRIENDS);
  private static final Pattern ACRONYM_WITH_STOP        = Pattern.compile(RE_ACRONYM_WITH_STOP);
  private static final Pattern NORM_DIGIT               = Pattern.compile(RE_NORM_DIGIT);
  private static final Pattern DIGIT_SUPPRESSION        = Pattern.compile(RE_DIGIT_SUPPRESSION);

  /**
   * Replace the delimiter of camel or snake into one space
   *
   * @param s the string may be camel case style or snake case style
   * @return the string which delimiter is one space
   */
  public static final String normCamelSnakeDelimiter(String s) {
    return CAMEL_OR_SNAKE_DELIMITER.matcher(s).replaceAll(" ");
  }

  /**
   * Remove the diacritics style letter
   *
   * @param s the string contains diacritics
   * @return a string without diacritics
   */
  public static final String stripDiacritics(String s) {
    s = Normalizer.normalize(s, Normalizer.Form.NFD);
    s = DIACRITICS_AND_FRIENDS.matcher(s).replaceAll("");
    return s;
  }

  /**
   * Remove contraction 's, 'm, 're, 've, 'd, 'll and 'o
   *
   * @param s the string contains contraction
   * @return the string without the designated contraction
   */
  public static final String removeContraction(String s) {
    return CONTRACTION.matcher(s).replaceAll("");
  }

  /**
   * Change the acronym with stop into the one without stop
   *
   * @param s the string (acronym) with stop
   * @return the one without stop
   */
  public static final String normAcronym(String s) {
    return ACRONYM_WITH_STOP.matcher(s).replaceAll("");
  }

  /**
   * Add hyphen (-) between two numbers
   *
   * @param s the string contain digit
   * @return the string cotain digit connected with hyphen
   */
  public static final String normDigit(String s) {
    return NORM_DIGIT.matcher(s).replaceAll("-");
  }

  /**
   * Suppresses digits.
   *
   * @param s the string contain digit
   * @return the string without digit
   */
  public static final String suppressDigit(String s) {
    return DIGIT_SUPPRESSION.matcher(s).replaceAll(" ").trim();
  }
}
