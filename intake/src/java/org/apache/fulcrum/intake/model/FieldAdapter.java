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

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;

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
    private static Map<String, FieldAdapter.FieldCtor> fieldCtors = initFieldCtors();

    private static Map<String, FieldAdapter.FieldCtor> initFieldCtors()
    {
        fieldCtors = new HashMap<String, FieldAdapter.FieldCtor>();

        fieldCtors.put("int", new FieldAdapter.FieldCtor()
        {
            @Override
            public Field<?> getInstance(XmlField f, Group g)
                    throws IntakeException
            {
                return new IntegerField(f, g);
            }
        }
        );
        fieldCtors.put("boolean", new FieldAdapter.FieldCtor()
        {
            @Override
            public Field<?> getInstance(XmlField f, Group g)
                    throws IntakeException
            {
                return new BooleanField(f, g);
            }
        }
        );
        fieldCtors.put("String", new FieldAdapter.FieldCtor()
        {
            @Override
            public Field<?> getInstance(XmlField f, Group g)
                    throws IntakeException
            {
                return new StringField(f, g);
            }
        }
        );
        fieldCtors.put("BigDecimal", new FieldAdapter.FieldCtor()
        {
            @Override
            public Field<?> getInstance(XmlField f, Group g)
                    throws IntakeException
            {
                return new BigDecimalField(f, g);
            }
        }
        );
        fieldCtors.put("FileItem", new FieldAdapter.FieldCtor()
        {
            @Override
            public Field<?> getInstance(XmlField f, Group g)
                    throws IntakeException
            {
                return new FileItemField(f, g);
            }
        }
        );
        fieldCtors.put("DateString", new FieldAdapter.FieldCtor()
        {
            @Override
            public Field<?> getInstance(XmlField f, Group g)
                    throws IntakeException
            {
                return new DateStringField(f, g);
            }
        }
        );
        fieldCtors.put("float", new FieldAdapter.FieldCtor()
        {
            @Override
            public Field<?> getInstance(XmlField f, Group g)
                    throws IntakeException
            {
                return new FloatField(f, g);
            }
        }
        );
        fieldCtors.put("double", new FieldAdapter.FieldCtor()
        {
            @Override
            public Field<?> getInstance(XmlField f, Group g)
                    throws IntakeException
            {
                return new DoubleField(f, g);
            }
        }
        );
        fieldCtors.put("short", new FieldAdapter.FieldCtor()
        {
            @Override
            public Field<?> getInstance(XmlField f, Group g)
                    throws IntakeException
            {
                return new ShortField(f, g);
            }
        }
        );
        fieldCtors.put("long", new FieldAdapter.FieldCtor()
        {
            @Override
            public Field<?> getInstance(XmlField f, Group g)
                    throws IntakeException
            {
                return new LongField(f, g);
            }
        }
        );
        fieldCtors.put("custom", new FieldAdapter.FieldCtor()
        {
            @Override
            public Field<?> getInstance(XmlField f, Group g)
                    throws IntakeException
            {
                String fieldClass = f.getFieldClass();

                if (fieldClass != null
                        && fieldClass.indexOf('.') == -1)
                {
                    fieldClass = Field.defaultFieldPackage + fieldClass;
                }

                if (fieldClass != null)
                {
                    Class<?> field;

                    try
                    {
                        field = Class.forName(fieldClass);
                        Constructor<?> constructor =
                            field.getConstructor(XmlField.class, Group.class);

                        return (Field<?>)constructor.newInstance(f, g);
                    }
                    catch (ClassNotFoundException e)
                    {
                        throw new IntakeException(
                                "Could not load Field class("
                                + fieldClass + ")", e);
                    }
                    catch (Exception e)
                    {
                        throw new IntakeException(
                                "Could not create new instance of Field("
                                + fieldClass + ")", e);
                    }
                }
                else
                {
                    throw new IntakeException(
                            "Custom field types must define a fieldClass");
                }
            }
        }
        );
        return fieldCtors;
    }

    protected interface FieldCtor
    {
        public Field<?> getInstance(XmlField f, Group g) throws IntakeException;
    }

    /**
     * Creates a Field object appropriate for the type specified
     * in the xml file.
     *
     * @param xmlField a <code>XmlField</code> value
     * @param xmlGroup the group this field belongs to
     * @return a <code>Field</code> value
     * @throws IntakeException indicates that an unknown type was specified for a field.
     */
    public static final Field<?> getInstance(XmlField xmlField, Group xmlGroup)
            throws IntakeException
    {
        FieldCtor fieldCtor = null;
        Field<?> field = null;
        String type = xmlField.getType();

        fieldCtor = fieldCtors.get(type);
        if (fieldCtor == null)
        {
            throw new IntakeException("An Unsupported type has been specified for " +
                    xmlField.getName() + " in group " + xmlGroup.getIntakeGroupName() + " type = " + type);
        }
        else
        {
            field = fieldCtor.getInstance(xmlField, xmlGroup);
        }

        return field;
    }

    /**
     * @see javax.xml.bind.annotation.adapters.XmlAdapter#unmarshal(java.lang.Object)
     */
    @Override
    public Field<?> unmarshal(XmlField xmlField) throws Exception
    {
        return getInstance(xmlField, xmlField.getGroup());
    }

    /**
     * @see javax.xml.bind.annotation.adapters.XmlAdapter#marshal(java.lang.Object)
     */
    @Override
    public XmlField marshal(Field<?> field) throws Exception
    {
        // This is never used in this context
        XmlField xml = new XmlField();
        return xml;
    }

}
