/*
 * Preprocessing.java
 * Copyright (C) 2019 Guowei Chen <icgw@outlook.com>
 *
 * Distributed under terms of the GPL license.
 */
package cn.ac.amss.semanticweb.text;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.Set;
import java.util.HashSet;
import java.util.regex.Pattern;

import cn.ac.amss.semanticweb.linguistics.stemming.Stemmer;
import cn.ac.amss.semanticweb.linguistics.stemming.PorterStemmer;

/**
 * Text preprocessing.
 *
 * @author Guowei Chen (icgw@outlook.com)
 */
public class Preprocessing
{
  private static Pattern TOKEN_DELIMITER = Init.patternTokenDelimiterEN();

  private static Set<String> stopWords = new HashSet<>();
  private static Stemmer stemmer       = Init.porterStemmer();

  private static boolean enableLowerCase     = true;
  private static boolean enableStemming      = true;
  private static boolean enableStopWords     = true;
  private static boolean enableNormalization = true;

  public static boolean isLowerCaseEnabled() {
    return enableLowerCase;
  }

  public static boolean isStemmerEnabled() {
    return enableStemming;
  }

  public static boolean isStopWordsEnabled() {
    return enableStopWords;
  }

  public static boolean isNormalizationEnabled() {
    return enableNormalization;
  }

  public static void setEnableLowerCase(boolean enableLowerCase) {
    Preprocessing.enableLowerCase = enableLowerCase;
  }

  public static void setEnableStemming(boolean enableStemming) {
    Preprocessing.enableStemming = enableStemming;
  }

  public static void setEnableStopWords(boolean enableStopWords) {
    Preprocessing.enableStopWords = enableStopWords;
  }

  public static void setEnableNormalization(boolean enableNormalization) {
    Preprocessing.enableNormalization = enableNormalization;
  }

  private static class StopWords {
    private static final String PATH = "/stopwords/";
    private static final String EN   = fileName( "en.txt" );
    // TODO: other stopwords in different language

    private static String fileName(String s) { return PATH + s; }
  }

  public static void defaultInit() throws IOException {
    stopWords.clear();
    Init.en();
  }

  private static class Init {
    private static final String RE_TOKEN_DELIMITER_EN = "[^a-zA-Z\\d\\-']+[^a-zA-Z\\d]*[^a-zA-Z\\d\\-']*";

    private static void en() throws IOException {
      try ( InputStream in = Preprocessing.class.getResourceAsStream(StopWords.EN);
            BufferedReader br = new BufferedReader(new InputStreamReader(in))
      ) {
        for (String line; (line = br.readLine()) != null; ) {
          if (line == null) continue;
          line = line.trim();

          if (line.isEmpty() || line.equals("")) continue;

          if (enableLowerCase) {
            line = line.toLowerCase();
          }

          stopWords.add(line);
        }
      }
    }

    private static Pattern patternTokenDelimiterEN() {
      return Pattern.compile(RE_TOKEN_DELIMITER_EN);
    }

    private static Stemmer porterStemmer() {
      return new PorterStemmer();
    }
  }

  private Preprocessing() {}

  /**
   * Check if the string is a stopword.
   *
   * @param s a string
   * @return whether the string is a stopword
   */
  public static boolean isStopWord(String s) {
    if (enableLowerCase) {
      s = s.toLowerCase();
    }
    return stopWords.contains(s);
  }

  /**
   * Remove all the stopwords in the set of words
   *
   * @param words IN: the set of word may contain stopwords, OUT: the one without stopwords
   */
  public static void removeStopWords(Set<String> words) {
    if (words == null || words.isEmpty() || stopWords == null || stopWords.isEmpty()) return;

    words.removeAll(stopWords);
  }

  /**
   * Acqure all tokens from string
   *
   * @param s a string
   * @return an string array of token
   */
  public static String[] stringTokenize(String s) {
    if (s == null) return new String[]{};

    return TOKEN_DELIMITER.split(s);
  }

  /**
   * Stemmer method
   *
   * @param word a string
   * @return a stemming of the string
   */
  public static String stem(String word) {
    if (word == null || word.isEmpty()) return "";
    return stemmer.mutate(word);
  }

  public static class NormalizationSetting {
    private static boolean stripDiacritics         = true;
    private static boolean normAcronym             = true;
    private static boolean normCamelSnakeDelimiter = true;
    private static boolean normDigit               = true;
    private static boolean removeContraction       = true;

    public static boolean isStripDiacriticsEnabled() {
      return stripDiacritics;
    }

    public static boolean isNormAcronymEnabled() {
      return normAcronym;
    }

    public static boolean isNormCamelSnakeDelimiterEnabled() {
      return normCamelSnakeDelimiter;
    }

    public static boolean isNormDigitEnabled() {
      return normDigit;
    }

    public static boolean isLowerCaseEnabled() {
      return Preprocessing.isLowerCaseEnabled();
    }

    public static boolean isRemoveContraction() {
      return removeContraction;
    }
  }

  public static String normalize(String text) {
    if (text == null || text.isEmpty()) return text;

    if (NormalizationSetting.isStripDiacriticsEnabled()) {
      text = Normalize.stripDiacritics(text);
    }

    if (NormalizationSetting.isNormAcronymEnabled()) {
      text = Normalize.normAcronym(text);
    }

    if (NormalizationSetting.isNormCamelSnakeDelimiterEnabled()) {
      text = Normalize.normCamelSnakeDelimiter(text);
    }

    if (NormalizationSetting.isNormDigitEnabled()) {
      text = Normalize.normDigit(text);
    }

    if (NormalizationSetting.isLowerCaseEnabled()) {
      text = text.toLowerCase();
    }

    if (NormalizationSetting.isRemoveContraction()) {
      text = Normalize.removeContraction(text);
    }

    return text;
  }
}
