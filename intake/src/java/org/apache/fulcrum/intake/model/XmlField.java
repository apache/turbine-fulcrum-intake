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

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import org.apache.avalon.framework.logger.LogEnabled;
import org.apache.avalon.framework.logger.Logger;
import org.apache.commons.lang3.StringUtils;

/**
 * A Class for holding data about a property used in an Application.
 *
 * @author <a href="mailto:jmcnally@collab.net">John McNally</a>
 * @author <a href="mailto:hps@intermeta.de">Henning P. Schmiedehausen</a>
 * @author <a href="mailto:quintonm@bellsouth.net">Quinton McCombs</a>
 * @author <a href="mailto:tv@apache.org">Thomas Vandahl</a>
 * @version $Id$
 */
@XmlType(name="field")
@XmlAccessorType(XmlAccessType.NONE)
public class XmlField
        implements Serializable, LogEnabled
{
    /**
     * Serial version id
     */
    private static final long serialVersionUID = -734309157828058007L;

    @XmlAttribute(required=true)
    private String key;

    @XmlAttribute(required=true)
    private String name;

    @XmlAttribute
    private String displayName;

    @XmlAttribute
    private String displaySize;

    @XmlAttribute
    private String type = "String";

    @XmlAttribute
    private boolean multiValued = false;

    @XmlAttribute
    private String fieldClass;

    @XmlAttribute
    private String mapToObject;

    @XmlAttribute
    private String mapToProperty;

    @XmlAttribute
    private String validator;

    @XmlAttribute
    private String defaultValue;

    @XmlAttribute
    private String emptyValue;

    private List<Rule> rules;
    private Map<String, Rule> ruleMap;

    private Group parent;

    private Logger log;

    /**
     * Default Constructor
     */
    public XmlField()
    {
        rules = new ArrayList<Rule>();
        ruleMap = new HashMap<String, Rule>();
    }

    /**
	 * Enable Avalon Logging
	 */
	@Override
	public void enableLogging(Logger logger)
	{
		this.log = logger;
	}

	/**
	 * Return Avalon logger
	 *
	 * @return the logger
	 */
	public Logger getLogger()
	{
		return log;
	}

    /**
     * Get the name of the property
     *
     * @return the raw name of the property
     */
    public String getRawName()
    {
        return name;
    }

    /**
     * Get the name of the property
     *
     * @return the name of the property with underscores removed
     */
    public String getName()
    {
        return StringUtils.replace(name, "_", "");
    }

    /**
     * Get the display name of the property
     *
     * @return the display name of the property
     */
    public String getDisplayName()
    {
        return displayName;
    }

    /**
     * Gets the display size of the field.  This is
     * useful for constructing the HTML input tag.
     *
     * @return the display size for the field
     */
    public String getDisplaySize()
    {
        return this.displaySize;
    }

    /**
     * Get the parameter key of the property
     *
     * @return the key of the property
     */
    public String getKey()
    {
        return key;
    }

    /**
     * Get the type of the property
     *
     * @return the type of the field
     */
    public String getType()
    {
        return type;
    }

    /**
     * Can this field have several values?
     *
     * @return true if the field can have multiple values
     */
    public boolean isMultiValued()
    {
        return multiValued;
    }

    /**
     * Get the name of the object that takes this input
     *
     * @return the name of the mapped object
     */
    public String getMapToObject()
    {
        return mapToObject;
    }

    /**
     * Get the property method that takes this input
     *
     * @return the property this field is mapped to
     */
    public String getMapToProperty()
    {
        if (mapToProperty == null)
        {
            return getName();
        }
        else
        {
            return mapToProperty;
        }
    }

    /**
     * Get the className of the validator
     *
     * @return the validator class name
     */
    public String getValidator()
    {
        return validator;
    }

    /**
     * Get the default Value.
     *
     * @return The default value for this field.
     */
    public String getDefaultValue()
    {
        return defaultValue;
    }

    /**
     * Get the empty Value.
     *
     * @return The empty value for this field.
     */
    public String getEmptyValue()
    {
        return emptyValue;
    }

    /**
     * Get the parent XmlGroup of the field
     *
     * @return the group this field belongs to
     */
    public Group getGroup()
    {
        return this.parent;
    }

    /**
     * Get the value of fieldClass.
     *
     * @return value of fieldClass.
     */
    public String getFieldClass()
    {
        return fieldClass;
    }

    /**
     * The collection of rules for this field.
     *
     * @return a <code>List</code> value
     */
    public List<Rule> getRules()
    {
        return rules;
    }

    /**
     * Set the collection of rules for this field
     *
     * @param rules the rules to set
     */
    @XmlElement(name="rule")
    public void setRules(List<Rule> rules)
    {
        this.rules = rules;
    }

    /**
     * The collection of rules for this field keyed by
     * parameter name.
     *
     * @return a <code>Map</code> value
     */
    public Map<String, Rule> getRuleMap()
    {
        return ruleMap;
    }

    /**
     * JAXB callback to set the parent object
     *
     * @param um the Unmarshaller
     * @param parent the parent object (an XmlGroup)
     */
    public void afterUnmarshal(Unmarshaller um, Object parent)
    {
        this.parent = (Group)parent;

        // Build map
        this.ruleMap.clear();
        for (Rule rule : rules)
        {
            ruleMap.put(rule.getName(), rule);
        }

        if (mapToObject == null)
        {
            // if a mapToProperty exists, set the object to this group's default
            if (mapToProperty != null
                    && !"".equals(mapToProperty)
                    && this.parent.getDefaultMapToObject() != null)
            {
                mapToObject = this.parent.getDefaultMapToObject();
            }
        }
    }

    /**
     * String representation of the column. This
     * is an xml representation.
     *
     * @return the value of this field as an XML representation
     */
    @Override
    public String toString()
    {
        StringBuilder result = new StringBuilder();
        result.append(" <field name=\"").append(name).append("\"")
            .append(" key=\"").append(key).append("\"")
            .append(" type=\"").append(type).append("\"");

        if (displayName != null)
        {
            result.append(" displayName=\"").append(displayName).append("\"");
        }
        if (mapToObject != null)
        {
            result.append(" mapToObject=\"").append(mapToObject).append("\"");
        }
        if (mapToProperty != null)
        {
            result.append(" mapToProperty=\"").append(mapToProperty).append("\"");
        }
        if (validator != null)
        {
            result.append(" validator=\"").append(validator).append("\"");
        }
        if (defaultValue != null)
        {
            result.append(" defaultValue=\"").append(defaultValue).append("\"");
        }

        if (emptyValue != null)
        {
            result.append(" emptyValue=\"").append(emptyValue).append("\"");
        }

        if (rules.size() == 0)
        {
            result.append(" />\n");
        }
        else
        {
            result.append(">\n");
            for (Rule rule : rules)
            {
                result.append(rule);
            }
            result.append("</field>\n");
        }

        return result.toString();
    }

    // this methods are called during serialization
    private void writeObject(ObjectOutputStream stream)
            throws IOException
    {
        stream.defaultWriteObject();
    }

    private void readObject(ObjectInputStream stream)
            throws IOException, ClassNotFoundException
    {
        stream.defaultReadObject();
    }
}
