/*
 * SimpleStemmer.java
 * Copyright (C) 2019 Guowei Chen <icgw@outlook.com>
 *
 * Distributed under terms of the GPL license.
 */

package cn.amss.semanticweb.linguistics.stemming;

import cn.amss.semanticweb.linguistics.lemmatization.SimpleLemmatizer;
import cn.amss.semanticweb.linguistics.lemmatization.SimpleLemmatizer.ENDING;

/**
 * Simple stemming class
 *
 * @author Guowei Chen (icgw@outlook.com)
 */
public class SimpleStemmer implements Stemmer
{
  public SimpleStemmer() {}

  private static String stem(String word, boolean toLemma) {
    String stemmed, result;
    stemmed = result = "";

    ENDING ending;

    if (word == null || word.isEmpty()) return "";

    int len = word.length();

    if (len > 5 && word.endsWith("ing") && !word.endsWith("thing")) { // 'ing' ending, drop ing
      // double letter added before ing (remove double letter, except double l)
      if (word.charAt(len - 4) != 'l' && (word.charAt(len - 4) == word.charAt(len - 5))) {
        stemmed = word.substring(0, len - 4);
      } else {
        stemmed = word.substring(0, len - 3);
      }
      ending = ENDING.ING;
    }

    else if (len > 6 && word.endsWith("iest")) { // 'iest' endings, replace with y
      stemmed = word.substring(0, len - 4) + "y";
      ending = ENDING.IEST;
    }

    else if (len > 5 && word.endsWith("est")) { // 'est' endings, drop est
      // double letter added before ing (remove double letter, except double l)
      if (word.charAt(len - 4) != 'l' && (word.charAt(len - 4) == word.charAt(len - 5))) {
        stemmed = word.substring(0, len - 4);
      } else {
        stemmed = word.substring(0, len - 3);
      }
      ending = ENDING.EST;
    }

    else if (len > 5 && word.endsWith("ies")) { // 'ies' plural ending, replace with y
      stemmed = word.substring(0, len - 3) + "y";
      ending = ENDING.IES;
    }

    else if (len > 5 && word.endsWith("ves")) { // 'ves' plural ending, replace with fe
      if (word.charAt(len - 4) == 'i' || word.charAt(len - 4) == 'e' || word.charAt(len - 4) == 'o') {
        stemmed = word.substring(0, len - 2);
      } else {
        stemmed = word.substring(0, len - 3) + "fe";
      }

      ending = ENDING.VES;
    }

    else if (len > 5 && word.endsWith("oes")) { // 'oes' plural ending, replace with o
      stemmed = word.substring(0, len - 2);
      ending = ENDING.OES;
    }

    else if (len > 4 && word.endsWith("us")) { // 'us' plural ending, replace with i
        // cannot be proceeded by a vowel
        if (word.charAt( len - 3 ) != 'o' && word.charAt( len - 3 ) != 'u' &&
            word.charAt( len - 3 ) != 'a' && word.charAt( len - 3 ) != 'e' &&
            word.charAt( len - 3 ) != 'i') {
          stemmed = word.substring(0, len - 2) + "i";
        }
        ending = ENDING.US;
    }

    else if (len > 4 && word.endsWith("es")) { // 'es' (or 's') plural endings
      // e before es
      if (word.charAt(len - 3) == 'e') {
      }
      else if (word.charAt(len - 3) == 's' || word.charAt(len - 3) == 'x' || word.charAt(len - 3) == 'z' ||
               (word.charAt(len - 3) == 'h' && (word.charAt(len - 4) == 's' || word.charAt(len - 4) == 'c'))) {
        // noun ends in -s, -ss, -sh, -ch, -x, or -z
        stemmed = word.substring(0, len - 2);
      }
      else { // 's' plural
        stemmed = word.substring(0, len - 1);
      }
      ending = ENDING.ES;
    }

    else if (len > 5 && word.endsWith("ved")) { // 'ved' past tense, drop d
      stemmed = word.substring(0, len - 1);
      ending = ENDING.VED;
    }

    else if (len > 5 && word.endsWith("ied")) { // 'ied' ending, replace with y
      stemmed = word.substring(0, len - 3) + "y";
      ending = ENDING.IED;
    }

    else if (len > 4 && word.endsWith("ed")) { // 'ed' past tense, drop ed
      // e before ed, drop the d
      if (word.charAt( len - 3 ) == 'e') {
        stemmed = word.substring(0, len - 1);
      }
      // double letter added before ing (remove double letter, except double l)
      else if (word.charAt(len - 3) != 'l' && (word.charAt(len - 3) == word.charAt(len - 4))) {
        stemmed = word.substring(0, len - 3);
      }
      // drop ed
      else {
        stemmed = word.substring(0, len - 2);
      }
      ending = ENDING.ED;
    }

    else if (len > 5 && word.endsWith("ier")) { // 'ier' ending, replace with y
      stemmed = word.substring(0, len - 3) + "y";
      ending = ENDING.IER;
    }

    else if (len > 4 && word.endsWith("er")) { // 'er' ending, drop er
      // e before er, drop the r
      if (word.charAt(len - 3) == 'e') {
        stemmed = word.substring(0, len - 1);
      }
      // double letter added before er (remove double letter, except double l)
      else if (word.charAt(len - 3) != 'l' && (word.charAt(len - 3) == word.charAt(len - 4))) {
        stemmed = word.substring(0, len - 3);
      }
      // drop er
      else {
        stemmed = word.substring(0, len - 2);
      }
      ending = ENDING.ER;
    }

    else if (len > 4 && word.endsWith("ly")) { // 'ly' ending, drop ly
      if (word.charAt(len - 3) == 'i') {
        // ily ending, replace with y
        stemmed = word.substring(0, len - 3) + "y";
      } else {
        stemmed = word.substring(0, len - 2);
      }
      ending = ENDING.LY;
    }

    else if (len > 3 && word.endsWith("s")) { // 's' ending, drop s
      // not double ss ending or before e
      if (word.charAt(len - 2) != 's' && word.charAt(len - 2) != 'e') {
        stemmed = word.substring(0, len - 1);
      }
      ending = ENDING.S;
    }

    else {
      return word;
    }

    if (toLemma) {
      return SimpleLemmatizer.lemma(stemmed, ending);
    }

    return stemmed;
  }

  @Override
  public String mutate(String value) {
    return stem(value, true);
  }
}
