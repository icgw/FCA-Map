/*
 * ConfusionMatrix.java
 * Copyright (C) 2019 Guowei Chen <icgw@outlook.com>
 *
 * Distributed under terms of the GPL license.
 */

package cn.ac.amss.semanticweb.util;

import cn.ac.amss.semanticweb.alignment.Mapping;
import cn.ac.amss.semanticweb.alignment.MappingCell;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import java.util.HashSet;
import java.util.Arrays;

public class ConfusionMatrix
{
  private final static Logger logger = LogManager.getLogger(ConfusionMatrix.class.getName());

  private int true_positive = 0;

  private int false_positive = 0;
  private int false_negative = 0;

  private double precision  = 0.0f;
  private double recall     = 0.0f;
  private double f1_measure = 0.0f;

  private static void add_to_map(Map<String, Set<String>> m, String key, String value) {
    Set<String> s = m.get(key);
    if (null == s) {
      m.put(key, new HashSet<String>(Arrays.asList(value)));
    } else {
      s.add(value);
    }
  }

  private static double divide_with_two_denominators(double numerator, double denominator1, double denominator2) {
    if ((denominator1 + denominator2) > 0.0) {
      return numerator / (denominator1 + denominator2);
    }
    return 0.0;
  }

  private ConfusionMatrix() {
  }

  public ConfusionMatrix(Mapping system, Mapping reference, boolean left_duplicate_free, boolean right_duplicate_free) {
    true_positive = false_positive = false_negative = 0;
    precision     = recall         = f1_measure     = 0.0f;

    Map<String, Set<String>> system_source_target = new HashMap<>();
    Map<String, Set<String>> system_target_source = new HashMap<>();

    for (MappingCell mc : system) {
      add_to_map(system_source_target, mc.getEntity1(), mc.getEntity2());
      add_to_map(system_target_source, mc.getEntity2(), mc.getEntity1());
    }

    for (MappingCell mc : reference) {
      if (mc.getEntity2().equals("null")) {
        false_positive += system_source_target.getOrDefault(mc.getEntity1(), new HashSet<String>()).size();
      }
      else if (mc.getEntity1().equals("null")) {
        false_positive += system_target_source.getOrDefault(mc.getEntity2(), new HashSet<String>()).size();
      }
      else {
        Set<String> systemTargets = system_source_target.getOrDefault(mc.getEntity1(), new HashSet<String>());
        Set<String> systemSources = system_target_source.getOrDefault(mc.getEntity2(), new HashSet<String>());

        if (systemTargets.contains(mc.getEntity2())) {
          ++true_positive; // TP

          if (left_duplicate_free) {
            false_positive += (systemSources.size() - 1);
            if (logger.isTraceEnabled()) {
              for (String e1 : systemSources) {
                if (e1.equals(mc.getEntity1())) continue;
                logger.trace(String.format("%n>>>False Positive<<<%n* %s%n* %s%n", e1, mc.getEntity2()));
              }
            }
          }

          if (right_duplicate_free) {
            false_positive += (systemTargets.size() - 1);
            if (logger.isTraceEnabled()) {
              for (String e2 : systemTargets) {
                if (e2.equals(mc.getEntity2())) continue;
                logger.trace(String.format("%n>>>False Positive<<<%n* %s%n* %s%n", mc.getEntity1(), e2));
              }
            }
          }
        } else {
          ++false_negative; // FN

          if (left_duplicate_free) {
            false_positive += systemSources.size();
            if (logger.isTraceEnabled()) {
              for (String e1 : systemSources) {
                logger.trace(String.format("%n>>>False Positive<<<%n* %s%n* %s%n", e1, mc.getEntity2()));
              }
            }
          }

          if (right_duplicate_free) {
            false_positive += systemTargets.size();
            if (logger.isTraceEnabled()) {
              for (String e2 : systemTargets) {
                logger.trace(String.format("%n>>>False Positive<<<%n* %s%n* %s%n", mc.getEntity1(), e2));
              }
            }
          }
        }
      }
    }

    precision  = divide_with_two_denominators(true_positive, true_positive, false_positive);
    recall     = divide_with_two_denominators(true_positive, true_positive, false_negative);
    f1_measure = divide_with_two_denominators((2.0 * recall * precision), recall, precision);
  }

  public int getTruePositive() {
    return true_positive;
  }

  public int getFalsePositive() {
    return false_positive;
  }

  public int getFalseNegative() {
    return false_negative;
  }

  public double getPrecision() {
    return precision;
  }

  public double getRecall() {
    return recall;
  }

  public double getF1measure() {
    return f1_measure;
  }
}
