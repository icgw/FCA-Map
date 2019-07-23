/*
 * Main.java
 * Copyright (C) 2019 Guowei Chen <icgw@outlook.com>
 *
 * Distributed under terms of the MIT license.
 */

package cn.amss.semanticweb.fca;

import java.io.*;

import org.apache.jena.rdf.model.*;
import org.apache.jena.util.*;
import org.apache.jena.vocabulary.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Main
 *
 */
public class Main
{
  final static String inputFileName = "file:/Users/icgw/Desktop/dataset/swtor.xml";

  public static void main(String[] args) {
    InputStream in = FileManager.get().open( inputFileName );
    Model m = ModelFactory.createDefaultModel();
    m.read(in, null);

    int numOfClasses = 0, numOfProperties = 0, numOfInstances = 0;

    ResIterator it = m.listSubjectsWithProperty(RDF.type, OWL.Class);
    while (it.hasNext()) {
      Resource res = it.nextResource();
      numOfClasses++;
    }

    it = m.listSubjectsWithProperty(RDF.type, RDF.Property);
    while (it.hasNext()) {
      Resource res = it.nextResource();
      numOfProperties++;
    }

    Logger _logger = LoggerFactory.getLogger(Main.class);
    _logger.info("#Instances: {}, #Properties: {}, #Classes: {}. ", numOfInstances, numOfProperties, numOfClasses);
  }
}
