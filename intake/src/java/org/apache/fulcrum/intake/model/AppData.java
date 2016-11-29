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
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.apache.fulcrum.intake.IntakeException;

/**
 * A class for holding application data structures.
 *
 * @author <a href="mailto:jmcnally@collab.net">John McNally</a>
 * @author <a href="mailto:hps@intermeta.de">Henning P. Schmiedehausen</a>
 * @author <a href="mailto:tv@apache.org">Thomas Vandahl</a>
 * @version $Id$
 */
@XmlRootElement(name="input-data")
@XmlAccessorType(XmlAccessType.NONE)
public class AppData implements Serializable
{
    /**
     * Serial version id
     */
    private static final long serialVersionUID = -3953843038383617960L;

    /** List of groups */
    private List<Group> groups;

    /** Package that will be used for all mapTo objects */
    private String basePackage = "";

    /** Prefix string that will be used to qualify &lt;prefix&gt;:&lt;intakegroup&gt; names */
    private String groupPrefix;

    /**
     * Return a collection of input sections (&lt;group&gt;).
     * The names of the groups returned here are only unique
     * to this AppData object and not qualified with the groupPrefix.
     * This method is used in the IntakeService to register all the
     * groups with and without prefix in the service.
     *
     * @return the list of groups
     */
    public List<Group> getGroups()
    {
        return groups;
    }

    /**
     * Set the collection of groups
     *
     * @param groups the groups to set
     */
    @XmlElement(name="group")
    public void setGroups(List<Group> groups)
    {
        this.groups = groups;
    }

    /**
     * Get a XmlGroup with the given name. It finds both
     * qualified and unqualified names in this package.
     *
     * @param groupName a <code>String</code> value
     * @return a <code>Group</code> value
     * @throws IntakeException indicates that the groupName was null
     */
    public Group getGroup(String groupName)
            throws IntakeException
    {
        if (groupName == null)
        {
            throw new IntakeException(
                    "Intake AppData.getGroup(groupName) is null");
        }

        String groupPrefix = getGroupPrefix();

        for (Group group : groups)
        {
            if (group.getIntakeGroupName().equals(groupName))
            {
                return group;
            }
            if (groupPrefix != null)
            {
                StringBuilder qualifiedGroupName = new StringBuilder();

                qualifiedGroupName.append(groupPrefix)
                        .append(':')
                        .append(group.getIntakeGroupName());

                if (qualifiedGroupName.toString().equals(groupName))
                {
                    return group;
                }
            }
        }
        return null;
    }

    /**
     * Get the base package String that will be appended to
     * any mapToObjects
     *
     * @return value of basePackage.
     */
    public String getBasePackage()
    {
        return basePackage;
    }

    /**
     * Set the base package String that will be appended to
     * any mapToObjects
     *
     * @param v  Value to assign to basePackage.
     */
    @XmlAttribute
    public void setBasePackage(String v)
    {
        if (v == null)
        {
            this.basePackage = "";
        }
        else
        {
            if (v.endsWith("."))
            {
                this.basePackage = v;
            }
            else
            {
                this.basePackage = v + ".";
            }
        }

    }

    /**
     * Get the prefix String that will be used to qualify
     * intake groups when using multiple XML files
     *
     * @return value of groupPrefix
     */
    public String getGroupPrefix()
    {
        return groupPrefix;
    }

    /**
     * Set the prefix String that will be used to qualify
     * intake groups when using multiple XML files
     *
     * @param groupPrefix  Value to assign to basePackage.
     */
    @XmlAttribute
    public void setGroupPrefix(String groupPrefix)
    {
        this.groupPrefix = groupPrefix;
    }

    /**
     * Creates a string representation of this AppData.
     * The representation is given in xml format.
     */
    @Override
    public String toString()
    {
        StringBuilder result = new StringBuilder();

        result.append("<input-data>\n");
        for (Group group : groups)
        {
            result.append(group);
        }
        result.append("</input-data>");
        return result.toString();
    }
}
