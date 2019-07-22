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

    ResIterator it = m.listSubjectsWithProperty(RDF.type, OWL.Class);
    while (it.hasNext()) {
      Resource res = it.nextResource();
      System.out.println(res.getURI());
    }
  }
}
