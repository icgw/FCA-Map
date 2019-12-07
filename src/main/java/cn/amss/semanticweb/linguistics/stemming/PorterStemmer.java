/*
 * PorterStemmer.java
 * Copyright (C) 2019 Guowei Chen <icgw@outlook.com>
 *
 * Distributed under terms of the MIT license.
 */

package cn.amss.semanticweb.linguistics.stemming;

/**
 *  Porter stemmer in Java. The original paper is in
 *
 *      Porter, 1980, An algorithm for suffix stripping, Program, Vol. 14,
 *      no. 3, pp 130-137,
 *
 *  See also http://www.tartarus.org/~martin/PorterStemmer
 *
 * Some changes as follows:
 *   + 'ization' == 'isation'
 * @author Guowei Chen (icgw@outlook.com)
 */
public class PorterStemmer implements Stemmer
{
  private static final int INC = 50;

  private char[] b = null;
  private int offset = 0, end = 0;
  private int j = 0, k = 0;

  public PorterStemmer() {
    b = new char[ INC ];
    offset = end = 0;
  }

  private void add(char ch) {
    if (b.length == offset) {
      char[] tmp = new char [ offset + INC ];
      for (int i = 0; i < offset; ++i) {
        tmp[i] = b[i];
      }
      b = tmp;
    }
    b[offset++] = ch;
  }

  private final boolean isConsonant(int i) {
    if (b[i] == 'a' || b[i] == 'e' || b[i] == 'i' || b[i] == 'o' || b[i] == 'u') {
      return false;
    } else if (b[i] == 'y') {
      return ((i == 0) ? (true) : (!isConsonant(i - 1)));
    }
    return true;
  }

  private final int numOfConsonantSeq() {
    int n = 0, i = 0;

    while (true) {
      if (i > j) return n;
      if (!isConsonant(i)) break;
      ++i;
    }
    ++i;

    while (true) {
      while (true) {
        if (i > j) return n;
        if (isConsonant(i)) break;
        ++i;
      }
      ++i;

      ++n;
      while (true) {
        if (i > j) return n;
        if (!isConsonant(i)) break;
        ++i;
      }
      ++i;
    }
  }

  private final boolean vowelInStem() {
    for (int i = 0; i <= j; ++i) {
      if (!isConsonant(i)) return true;
    }
    return false;
  }

  private final boolean doubleConsonant(int i) {
    if (i < 1) return false;
    if (b[i] != b[i - 1]) return false;
    return isConsonant(i);
  }

  private final boolean consonantVowelConsonant(int i) {
    if (i < 2 || !isConsonant(i) || isConsonant(i - 1) || !isConsonant(i - 2)) return false;
    int ch = b[i];
    if (ch == 'w' || ch == 'x' || ch == 'y') return false;
    return true;
  }

  private final boolean endsWith(String suffix) {
    int l = suffix.length();
    int o = k - l + 1;
    if (o < 0) return false;
    for (int i = 0; i < l; ++i) {
      if (b[o + i] != suffix.charAt(i)) return false;
    }
    j = k - l;
    return true;
  }

  private final void append(String s) {
    int l = s.length();
    for (int i = 0, o = j + 1; i < l; ++i, ++o) {
      b[o] = s.charAt(i);
    }
    k = j + l;
  }

  private final void down(String s) {
    if (numOfConsonantSeq() > 0) {
      append(s);
    }
  }

  private final void step1() {
    if (b[k] == 's') {
      if (endsWith("sses")) {
        k -= 2;
      } else if (endsWith("ies")) {
        append("i");
      } else if (b[k - 1] != 's') {
        --k;
      }
    }

    if (endsWith("eed")) {
      if (numOfConsonantSeq() > 0) {
        --k;
      }
    } else if ((endsWith("ed") || endsWith("ing")) && vowelInStem()) {
      k = j;
      if (endsWith("at")) {
        append("ate");
      } else if (endsWith("bl")) {
        append("ble");
      } else if (endsWith("iz")) {
        append("ize");
      } else if (doubleConsonant(k)) {
        --k;
        int ch = b[k];
        if (ch == 'l' || ch == 's' || ch == 'z') ++k;
      } else if (numOfConsonantSeq() == 1 && consonantVowelConsonant(k)) {
        append("e");
      }
    }
  }

  private final void step2() {
    if (endsWith("y") && vowelInStem()) {
      b[k] = 'i';
    }
  }

  private final void step3() {
    if (k == 0) return;
    switch (b[k - 1]) {
      case 'a': if (endsWith("ational")) { down("ate"); break; }
                if (endsWith("tional")) { down("tion"); break; }
                break;
      case 'c': if (endsWith("enci")) { down("ence"); break; }
                if (endsWith("anci")) { down("ance"); break; }
                break;
      case 'e': if (endsWith("izer")) { down("ize"); break; }
                break;
      case 'l': if (endsWith("bli")) { down("ble"); break; }
                if (endsWith("alli")) { down("al"); break; }
                if (endsWith("entli")) { down("ent"); break; }
                if (endsWith("eli")) { down("e"); break; }
                if (endsWith("ousli")) { down("ous"); break; }
                break;
      case 'o': if (endsWith("ization") || endsWith("isation")) { down("ize"); break; }
                if (endsWith("ation")) { down("ate"); break; }
                if (endsWith("ator")) { down("ate"); break; }
                break;
      case 's': if (endsWith("alism")) { down("al"); break; }
                if (endsWith("iveness")) { down("ive"); break; }
                if (endsWith("fulness")) { down("ful"); break; }
                if (endsWith("ousness")) { down("ous"); break; }
                break;
      case 't': if (endsWith("aliti")) { down("al"); break; }
                if (endsWith("iviti")) { down("ive"); break; }
                if (endsWith("biliti")) { down("ble"); break; }
                break;
      case 'g': if (endsWith("logi")) { down("log"); break; }
    }
  }

  private final void step4() {
    switch (b[k]) {
      case 'e': if (endsWith("icate")) { down("ic"); break; }
                if (endsWith("ative")) { down(""); break; }
                if (endsWith("alize")) { down("al"); break; }
                break;
      case 'i': if (endsWith("iciti")) { down("ic"); break; }
                break;
      case 'l': if (endsWith("ical")) { down("ic"); break; }
                if (endsWith("ful")) { down(""); break; }
                break;
      case 's': if (endsWith("ness")) { down(""); break; }
                  break;
    }
  }

  private final void step5() {
    if (0 == k) return ;
    switch (b[k - 1]) {
      case 'a': if (endsWith("al")) break;
                return;
      case 'c': if (endsWith("ance")) break;
                if (endsWith("ence")) break;
                return;
      case 'e': if (endsWith("er")) break;
                return;
      case 'i': if (endsWith("ic")) break;
                return;
      case 'l': if (endsWith("able")) break;
                if (endsWith("ible")) break;
                return;
      case 'n': if (endsWith("ant")) break;
                if (endsWith("ement")) break;
                if (endsWith("ment")) break;
                if (endsWith("ent")) break;
                return;
      case 'o': if (endsWith("ion") && j >= 0 && (b[j] == 's' || b[j] == 't')) break;
                if (endsWith("ou")) break;
                return;
      case 's': if (endsWith("ism")) break;
                return;
      case 't': if (endsWith("ate")) break;
                if (endsWith("iti")) break;
                return;
      case 'u': if (endsWith("ous")) break;
                return;
      case 'v': if (endsWith("ive")) break;
                return;
      case 'z': if (endsWith("ize")) break;
                return;
      default: return;
    }

    if (numOfConsonantSeq() > 1) k = j;
  }

  private final void step6() {
    j = k;
    if (b[k] == 'e') {
      int a = numOfConsonantSeq();
      if (a > 1 || a == 1 && !consonantVowelConsonant(k - 1)) --k;
    }
    if (b[k] == 'l' && doubleConsonant(k) && numOfConsonantSeq() > 1) --k;
  }

  private void stem() {
    k = offset - 1;
    if (k > 1) {
      step1(); step2(); step3(); step4(); step5(); step6();
    }
    end = k + 1; offset = 0;
  }

  private void setCurrent(String value) {
    offset = end = 0;
    for (char ch : value.toCharArray()) {
      ch = Character.toLowerCase(ch);
      add(ch);
    }
  }

  private String getCurrent() {
    return new String(b, 0, end);
  }

  @Override
  public String mutate(String value) {
    setCurrent(value);
    stem();
    return getCurrent();
  }
}
