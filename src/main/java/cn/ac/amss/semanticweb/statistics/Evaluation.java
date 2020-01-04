/*
 * Evaluation.java
 * Copyright (C) 2019 Guowei Chen <icgw@outlook.com>
 *
 * Distributed under terms of the GPL license.
 */

package cn.ac.amss.semanticweb.statistics;

import cn.ac.amss.semanticweb.alignment.Mapping;
import cn.ac.amss.semanticweb.alignment.MappingCell;
import cn.ac.amss.semanticweb.vocabulary.DBkWik;
import cn.ac.amss.semanticweb.util.ConfusionMatrix;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import java.util.Set;
import java.util.HashSet;
import java.util.Arrays;

public class Evaluation
{
  private final static Logger logger = LogManager.getLogger(Evaluation.class.getName());

  private static final Set<String> CLASS_TYPES    = new HashSet<>(Arrays.asList("class", "null"));
  private static final Set<String> PROPERTY_TYPES = new HashSet<>(Arrays.asList("property", "null"));
  private static final Set<String> INSTANCE_TYPES = new HashSet<>(Arrays.asList("resource", "null"));

  private Mapping m_reference = null;
  private Mapping m_system    = null;

  private Mapping m_class_reference    = null;
  private Mapping m_property_reference = null;
  private Mapping m_instance_reference = null;

  private Mapping m_class_system    = null;
  private Mapping m_property_system = null;
  private Mapping m_instance_system = null;

  private static final void getAlignmentTypes(Mapping m, Mapping clzz, Mapping prop, Mapping inst) {
    for (MappingCell mc : m) {
      String source_type = DBkWik.getType(mc.getEntity1());
      String target_type = DBkWik.getType(mc.getEntity2());

      if (CLASS_TYPES.contains(source_type) && CLASS_TYPES.contains(target_type)) {
        clzz.add(mc);
      }
      else if (PROPERTY_TYPES.contains(source_type) && PROPERTY_TYPES.contains(target_type)) {
        prop.add(mc);
      }
      else if (INSTANCE_TYPES.contains(source_type) && INSTANCE_TYPES.contains(target_type)) {
        inst.add(mc);
      }
    }

    clzz.setOntSourceTarget(m.getSource(), m.getTarget());
    prop.setOntSourceTarget(m.getSource(), m.getTarget());
    inst.setOntSourceTarget(m.getSource(), m.getTarget());
  }

  public Evaluation(Mapping reference, Mapping system) {
    m_reference = reference;
    m_system    = system;

    m_class_reference    = new Mapping();
    m_property_reference = new Mapping();
    m_instance_reference = new Mapping();

    m_class_system    = new Mapping();
    m_property_system = new Mapping();
    m_instance_system = new Mapping();

    getAlignmentTypes(reference, m_class_reference, m_property_reference, m_instance_reference);
    getAlignmentTypes(system, m_class_system, m_property_system, m_instance_system);
  }

  public void printResult(boolean left_duplicate_free, boolean right_duplicate_free) {
    ConfusionMatrix instances_eval  = new ConfusionMatrix(m_instance_system, m_instance_reference,
                                                          left_duplicate_free, right_duplicate_free);

    ConfusionMatrix properties_eval = new ConfusionMatrix(m_property_system, m_property_reference,
                                                          left_duplicate_free, right_duplicate_free);

    ConfusionMatrix classes_eval    = new ConfusionMatrix(m_class_system, m_class_reference,
                                                          left_duplicate_free, right_duplicate_free);

    ConfusionMatrix overall_eval    = new ConfusionMatrix(m_system, m_reference,
                                                          left_duplicate_free, right_duplicate_free);

    if (logger.isInfoEnabled()) {
      String info = String.format("%nInstance - pre.: %.2f, f1m.: %.2f, rec.: %.2f, #: %8d. (TP: %8d, FP: %8d, FN: %8d)" +
                                  "%nProperty - pre.: %.2f, f1m.: %.2f, rec.: %.2f, #: %8d. (TP: %8d, FP: %8d, FN: %8d)" +
                                  "%nClass    - pre.: %.2f, f1m.: %.2f, rec.: %.2f, #: %8d. (TP: %8d, FP: %8d, FN: %8d)" +
                                  "%nOverall  - pre.: %.2f, f1m.: %.2f, rec.: %.2f, #: %8d. (TP: %8d, FP: %8d, FN: %8d)", 
      instances_eval.getPrecision()     , instances_eval.getF1measure()      , instances_eval.getRecall()         , m_instance_system.size() ,
      instances_eval.getTruePositive()  , instances_eval.getFalsePositive()  , instances_eval.getFalseNegative()  ,
      properties_eval.getPrecision()    , properties_eval.getF1measure()     , properties_eval.getRecall()        , m_property_system.size() ,
      properties_eval.getTruePositive() , properties_eval.getFalsePositive() , properties_eval.getFalseNegative() ,
      classes_eval.getPrecision()       , classes_eval.getF1measure()        , classes_eval.getRecall()           , m_class_system.size()    ,
      classes_eval.getTruePositive()    , classes_eval.getFalsePositive()    , classes_eval.getFalseNegative()    ,
      overall_eval.getPrecision()       , overall_eval.getF1measure()        , overall_eval.getRecall()           , m_system.size()          ,
      overall_eval.getTruePositive()    , overall_eval.getFalsePositive()    , overall_eval.getFalseNegative());

      logger.info(info);
    }
  }
}
