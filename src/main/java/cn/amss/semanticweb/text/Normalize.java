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

public class Normalize
{
  private static final String RE_CAMELCASE_OR_UNDERSCORE = "(?<!(^|[A-Z]))(?=[A-Z])|(?<!^)(?=[A-Z][a-z])|_";

  private static final Pattern DIACRITICS_AND_FRIENDS = Pattern.compile("[\\p{InCombiningDiacriticalMarks}\\p{IsLm}\\p{IsSk}]+");

  public static final String normalizeCaseStyle(String s) {
    if (s == null || s.isEmpty()) {
      return s;
    }

    StringJoiner sj = new StringJoiner(" ");
    for (String word : s.split(RE_CAMELCASE_OR_UNDERSCORE)) {
      if (!word.isEmpty()) {
        sj.add(word.trim());
      }
    }
    return sj.toString();
  }

  public static final String stripDiacritics(String s) {
    s = Normalizer.normalize(s, Normalizer.Form.NFD);
    s = DIACRITICS_AND_FRIENDS.matcher(s).replaceAll("");
    return s;
  }
}
