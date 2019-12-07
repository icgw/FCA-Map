/*
 * XMLAlignReader.java
 * Copyright (C) 2019 Guowei Chen <icgw@outlook.com>
 *
 * Distributed under terms of the GPL license.
 */

package cn.ac.amss.semanticweb.io;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URL;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamReader;

public class XMLAlignReader extends MappingReader
{
  public XMLAlignReader(URL url) throws Exception {
    this(url.openStream());
  }

  public XMLAlignReader(String file_path) throws Exception {
    this(new FileInputStream(new File(file_path)));
  }

  public XMLAlignReader(InputStream in) throws Exception {
    XMLInputFactory factory = XMLInputFactory.newInstance();
    m_mappings.clear();

    String iri1, iri2, relation, confidence;
    iri1 = iri2 = relation = confidence = "";

    for (XMLStreamReader reader = factory.createXMLStreamReader(in);
         reader.hasNext();
         reader.next()) {
      if (reader.getEventType() == XMLStreamConstants.START_ELEMENT) {
        if (reader.hasName()) {
          if (reader.getLocalName().equals(CELL)) {
            iri1 = iri2 = relation = confidence = "";
          }
          else if (reader.getLocalName().equals(ENTITY1)) {
            if (reader.getAttributeCount() > 0) {
              iri1 = reader.getAttributeValue(0);
            }
          }
          else if (reader.getLocalName().equals(ENTITY2)) {
            if (reader.getAttributeCount() > 0) {
              iri2 = reader.getAttributeValue(0);
            }
          }
          else if (reader.getLocalName().equals(RELATION)) {
            relation = reader.getElementText();
          }
          else if (reader.getLocalName().equals(MEASURE)) {
            confidence = reader.getElementText();
          }
        }
      }
      else if (reader.getEventType() == XMLStreamConstants.END_ELEMENT) {
        if (reader.hasName()) {
          if (reader.getLocalName().equals(CELL)) {
            m_mappings.add(iri1, iri2, relation, confidence);
          }
        }
      }
    }
  }
}
