package org.apache.fulcrum.intake.transform;

/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.avalon.framework.logger.LogEnabled;
import org.apache.avalon.framework.logger.Logger;
import org.apache.fulcrum.intake.xmlmodel.AppData;
import org.apache.fulcrum.intake.xmlmodel.Rule;
import org.apache.fulcrum.intake.xmlmodel.XmlField;
import org.apache.fulcrum.intake.xmlmodel.XmlGroup;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * A Class that is used to parse an input
 * xml schema file and creates and AppData java structure.
 * It uses apache Xerces to do the xml parsing.
 *
 * @author <a href="mailto:jmcnally@collab.net">John McNally</a>
 * @author <a href="mailto:hps@intermeta.de">Henning P. Schmiedehausen</a>
 * @author <a href="mailto:quintonm@bellsouth.net">Quinton McCombs</a>
 * @version $Id$
 */
public class XmlToAppData extends DefaultHandler
    implements LogEnabled
{
    /** Logging */
    private Logger log;

    private AppData app;
    private XmlGroup currGroup;
    private XmlField currField;
    private Rule currRule;
    private String currElement;
    private StringBuffer chars;

    private static SAXParserFactory saxFactory;

    static
    {
        saxFactory = SAXParserFactory.newInstance();
        saxFactory.setValidating(true);
    }

    /**
     * Creates a new instance of the Intake XML Parser
     */
    public XmlToAppData()
    {
        app = new AppData();
    }

    /**
     * Parses a XML input file and returns a newly created and
     * populated AppData structure.
     *
     * @param xmlFile The input file to parse.
     * @return AppData populated by <code>xmlFile</code>.
     * @throws ParserConfigurationException
     * @throws SAXException
     * @throws IOException
     */
    public AppData parseFile(String xmlFile)
            throws ParserConfigurationException, SAXException, IOException
    {
        SAXParser parser = saxFactory.newSAXParser();

        FileReader fr = new FileReader(xmlFile);
        BufferedReader br = new BufferedReader(fr);
        
        chars = new StringBuffer();
        
        try
        {
            InputSource is = new InputSource(br);
            parser.parse(is, this);
        }
        finally
        {
            br.close();
        }

        return app;
    }

    /**
     * Provide an Avalon logger
     * 
     * @see org.apache.avalon.framework.logger.LogEnabled#enableLogging(org.apache.avalon.framework.logger.Logger)
     */
    public void enableLogging(Logger logger)
    {
        this.log = logger.getChildLogger("XmlToAppData");
        
    }

    /**
     * EntityResolver implementation. Called by the XML parser
     *
     * @return an InputSource for the database.dtd file
     */
    public InputSource resolveEntity(String publicId, String systemId)
    {
        return new DTDResolver().resolveEntity(publicId, systemId);
    }

    /**
     * Handles opening elements of the xml file.
     */
    public void startElement(String uri, String localName,
                             String rawName, Attributes attributes)
    {
        currElement = rawName;
        if (rawName.equals("input-data"))
        {
            app.loadFromXML(attributes);
        }
        else if (rawName.equals("group"))
        {
            currGroup = app.addGroup(attributes);
        }
        else if (rawName.equals("field"))
        {
            currField = currGroup.addField(attributes);
        }
        else if (rawName.equals("rule"))
        {
            currRule = currField.addRule(attributes);
        }
    }

    /**
     * Handles closing elements of the xml file.
     * 
     * @see org.xml.sax.helpers.DefaultHandler#endElement(java.lang.String, java.lang.String, java.lang.String)
     */
    public void endElement(String uri, String localName, String name) throws SAXException
    {
        if ("rule".equals(currElement) && chars.length() > 0)
        {
            currRule.setMessage(chars.toString());
        }
        else if ("required-message".equals(currElement) && chars.length() > 0)
        {
            log.warn("The required-message element is deprecated!  " +
                    "You should update your intake.xml file to use the " +
                    "'required' rule instead.");
            currField.setIfRequiredMessage(chars.toString());
        }
        
        chars = new StringBuffer();
    }

    /**
     * Handles the character data, which we are using to specify the
     * error message.
     */
    public void characters(char[] mesgArray, int start, int length)
    {
        this.chars.append(mesgArray, start, length);
    }

    /**
     * Callback function for the xml parser to give warnings.
     *
     * @param spe a <code>SAXParseException</code> value
     */
    public void warning(SAXParseException spe)
    {
        log.warn("Parser Exception: " +
                "Line " + spe.getLineNumber() +
                " Row: " + spe.getColumnNumber() +
                " Msg: " + spe.getMessage());
    }

    /**
     * Callback function for the xml parser to give errors.
     *
     * @param spe a <code>SAXParseException</code> value
     */
    public void error(SAXParseException spe)
    {
        log.error("Parser Exception: " +
                "Line " + spe.getLineNumber() +
                " Row: " + spe.getColumnNumber() +
                " Msg: " + spe.getMessage());
    }

    /**
     * Callback function for the xml parser to give fatalErrors.
     *
     * @param spe a <code>SAXParseException</code> value
     */
    public void fatalError(SAXParseException spe)
    {
        log.fatalError("Parser Exception: " +
                "Line " + spe.getLineNumber() +
                " Row: " + spe.getColumnNumber() +
                " Msg: " + spe.getMessage());
    }
}
