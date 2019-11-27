/*
 * Normalize.java
 * Copyright (C) 2019 Guowei Chen <icgw@outlook.com>
 *
 * Distributed under terms of the GPL license.
 */

package cn.amss.semanticweb.text;

import java.util.StringJoiner;
import java.util.regex.Pattern;
import java.text.Normalizer;

/**
 * Transform a text string into a normal style
 *
 * @author Guowei Chen (icgw@outlook.com)
 */
public class Normalize
{
  private static final String RE_CAMELCASE_OR_UNDERSCORE      = "(?<!(^|[A-Z]))(?=[A-Z])|(?<!^)(?=[A-Z][a-z])|_";
  private static final String RE_TAIL_WITH_S                  = "['’]s\\b|[a-zA-Z0-9][ \t]@_]";
  private static final String RE_DIACRITICS_AND_FRIENDS       = "[\\p{InCombiningDiacriticalMarks}\\p{IsLm}\\p{IsSk}]+";
  private static final String RE_PARENTHETICAL_DISAMBIGUATION = " \\(.+?\\)$";
  private static final String RE_SUBPAGES                     = "\\/.+?$";

  private static final Pattern CAMELCASE_OR_UNDERSCORE      = Pattern.compile(RE_CAMELCASE_OR_UNDERSCORE);
  private static final Pattern TAIL_WITH_S                  = Pattern.compile(RE_TAIL_WITH_S);
  private static final Pattern DIACRITICS_AND_FRIENDS       = Pattern.compile(RE_DIACRITICS_AND_FRIENDS);
  private static final Pattern PARENTHETICAL_DISAMBIGUATION = Pattern.compile(RE_PARENTHETICAL_DISAMBIGUATION);
  private static final Pattern SUBPAGES                     = Pattern.compile(RE_SUBPAGES);

  /**
   * Transform camel case or underscore case style into a normal case style
   *
   * @param s a text string
   * @return a normal string
   */
  public static final String normalizeCaseStyle(String s) {
    if (s == null || s.isEmpty()) {
      return s;
    }

    StringJoiner sj = new StringJoiner(" ");
    for (String word : CAMELCASE_OR_UNDERSCORE.split(s)) {
      if (!word.isEmpty()) {
        sj.add(word.trim());
      }
    }
    return sj.toString();
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
   * Remove 's in a string
   *
   * @param s the string contains 's
   * @return a string without 's
   */
  public static final String removeS(String s) {
    return TAIL_WITH_S.matcher(s).replaceAll("");
  }

  /**
   * Remove the parenthetical disambiguation in the wikipedia:article title
   *
   * @param s wikipedia:article title
   * @return wikipedia article title without parenthetical disambiguation
   */
  public static final String removeDisambiguation(String s) {
    return PARENTHETICAL_DISAMBIGUATION.matcher(s).replaceFirst("");
  }

  /**
   * Remove the subpages in the wikipedia:article title
   *
   * @param s wikipedia:article title with subpages
   * @return wikipedia:article title without subpages
   */
  public static final String removeSubPages(String s) {
    return SUBPAGES.matcher(s).replaceFirst("");
  }
}
