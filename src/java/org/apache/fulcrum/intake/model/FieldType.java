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
import java.lang.reflect.Constructor;


import org.apache.fulcrum.intake.IntakeException;

import jakarta.xml.bind.annotation.XmlEnum;
import jakarta.xml.bind.annotation.XmlEnumValue;

/**
 * Enum for valid field types.
 *
 * @author <a href="mailto:tv@apache.org">Thomas Vandahl</a>
 */
@XmlEnum(String.class)
public enum FieldType implements Serializable
{
    @XmlEnumValue("boolean") FIELD_BOOLEAN("boolean")
    {
        @Override
        public Field<?> getInstance(XmlField f, Group g) throws IntakeException
        {
            return new BooleanField(f, g);
        }
    },
    @XmlEnumValue("BigDecimal") FIELD_BIGDECIMAL("BigDecimal")
    {
        @Override
        public Field<?> getInstance(XmlField f, Group g) throws IntakeException
        {
            return new BigDecimalField(f, g);
        }
    },
    @XmlEnumValue("int") FIELD_INT("int")
    {
        @Override
        public Field<?> getInstance(XmlField f, Group g) throws IntakeException
        {
            return new IntegerField(f, g);
        }
    },
    @XmlEnumValue("float") FIELD_FLOAT("float")
    {
        @Override
        public Field<?> getInstance(XmlField f, Group g) throws IntakeException
        {
            return new FloatField(f, g);
        }
    },
    @Deprecated
    @XmlEnumValue("FileItem") FIELD_FILEITEM("FileItem")
    {
        @Override
        public Field<?> getInstance(XmlField f, Group g) throws IntakeException
        {
            return new UploadPartField(f, g);
        }
    },
    @XmlEnumValue("UploadPart") FIELD_UPLOADPART("UploadPart")
    {
        @Override
        public Field<?> getInstance(XmlField f, Group g) throws IntakeException
        {
            return new UploadPartField(f, g);
        }
    },
    @XmlEnumValue("String") FIELD_STRING("String")
    {
        @Override
        public Field<?> getInstance(XmlField f, Group g) throws IntakeException
        {
            return new StringField(f, g);
        }
    },
    @XmlEnumValue("DateString") FIELD_DATESTRING("DateString")
    {
        @Override
        public Field<?> getInstance(XmlField f, Group g) throws IntakeException
        {
            return new DateStringField(f, g);
        }
    },
    @XmlEnumValue("ComboKey") FIELD_COMBOKEY("ComboKey")
    {
        @Override
        public Field<?> getInstance(XmlField f, Group g) throws IntakeException
        {
            throw new IntakeException("An unsupported type has been specified for " +
                    f.getName() + " in group " + g.getIntakeGroupName() + " type = " + value());
        }
    },
    @XmlEnumValue("double") FIELD_DOUBLE("double")
    {
        @Override
        public Field<?> getInstance(XmlField f, Group g) throws IntakeException
        {
            return new DoubleField(f, g);
        }
    },
    @XmlEnumValue("short") FIELD_SHORT("short")
    {
        @Override
        public Field<?> getInstance(XmlField f, Group g) throws IntakeException
        {
            return new ShortField(f, g);
        }
    },
    @XmlEnumValue("long") FIELD_LONG("long")
    {
        @Override
        public Field<?> getInstance(XmlField f, Group g) throws IntakeException
        {
            return new LongField(f, g);
        }
    },
    @XmlEnumValue("custom") FIELD_CUSTOM("custom")
    {
        @Override
        public Field<?> getInstance(XmlField f, Group g) throws IntakeException
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
                    Constructor<?> constructor = field.getConstructor(XmlField.class, Group.class);

                    return (Field<?>) constructor.newInstance(f, g);
                }
                catch (ClassNotFoundException e)
                {
                    throw new IntakeException(
                            "Could not load Field class("
                                    + fieldClass + ")",
                            e);
                }
                catch (Exception e)
                {
                    throw new IntakeException(
                            "Could not create new instance of Field("
                                    + fieldClass + ")",
                            e);
                }
            }
            else
            {
                throw new IntakeException(
                        "Custom field types must define a fieldClass");
            }
        }
    };

    /** Serial version */
    private static final long serialVersionUID = -8563326491799622016L;

    /** String value of the field type */
    private String stringValue;

    /**
     * Constructor
     *
     * @param stringValue
     */
    FieldType(String stringValue)
    {
        this.stringValue = stringValue;
    }

    /**
     * Return the string value
     *
     * @return a <code>String</code> value
     */
    public String value()
    {
        return stringValue;
    }

    /**
     * Create a specific field instance from its XML representation
     *
     * @param f the XML object
     * @param g the group this field belongs to
     * @return a Field&lt;?&gt; instance
     *
     * @throws IntakeException if the field could not be created
     */
    public abstract Field<?> getInstance(XmlField f, Group g) throws IntakeException;
}
