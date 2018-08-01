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

import javax.xml.bind.annotation.adapters.XmlAdapter;

import org.apache.fulcrum.intake.IntakeException;

/**
 * Creates Field objects.
 *
 * @author <a href="mailto:tv@apache.org">Thomas Vandahl</a>
 * @version $Id: FieldFactory.java 1200653 2011-11-11 00:05:28Z tv $
 */
public class FieldAdapter extends XmlAdapter<XmlField, Field<?>>
{
    /**
     * Creates a Field object appropriate for the type specified
     * in the xml file.
     *
     * @see javax.xml.bind.annotation.adapters.XmlAdapter#unmarshal(java.lang.Object)
     */
    @Override
    public Field<?> unmarshal(XmlField xmlField) throws Exception
    {
        Field<?> field = null;
        FieldType type = xmlField.getType();

        if (type == null)
        {
            throw new IntakeException("An unsupported type has been specified for " +
                    xmlField.getName() + " in group " + xmlField.getGroup().getIntakeGroupName());
        }
        else
        {
            field = type.getInstance(xmlField, xmlField.getGroup());
        }

        return field;
    }

    /**
     * @see javax.xml.bind.annotation.adapters.XmlAdapter#marshal(java.lang.Object)
     */
    @Override
    public XmlField marshal(Field<?> field) throws Exception
    {
        // This is never used in this context
        return new XmlField();
    }
}
