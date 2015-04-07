package org.apache.fulcrum.intake.model;

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

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlValue;

import org.apache.fulcrum.intake.validator.Constraint;

/**
 * A Class for holding data about a constraint on a property.
 *
 * @author <a href="mailto:jmcnally@collab.net">John McNally</a>
 * @author <a href="mailto:tv@apache.org">Thomas Vandahl</a>
 * @version $Id$
 */
@XmlType(name="rule")
@XmlAccessorType(XmlAccessType.NONE)
public class Rule implements Constraint, Serializable
{
    /**
     * Serial version id
     */
    private static final long serialVersionUID = -4059931768288150848L;

    @XmlAttribute(required=true)
    private String name;

    @XmlAttribute(required=true)
    private String value;

    @XmlValue
    private String message;

    /**
     * Get the name of the parameter
     */
    @Override
    public String getName()
    {
        return name;
    }

    /**
     * Get the value of the parameter
     */
    @Override
    public String getValue()
    {
        return value;
    }

    /**
     * Get the error message
     */
    @Override
    public String getMessage()
    {
        return message;
    }

    /**
     * String representation of the column. This
     * is an xml representation.
     */
    @Override
    public String toString()
    {
        StringBuilder result = new StringBuilder(100);

        result.append("<rule name=\"").append(name).append("\"")
            .append(" value=\"").append(value).append("\"");

        if (message == null)
        {
            result.append(" />\n");
        }
        else
        {
            result.append(">")
                    .append(message)
                    .append("</rule>\n");
        }

        return result.toString();
    }
}
