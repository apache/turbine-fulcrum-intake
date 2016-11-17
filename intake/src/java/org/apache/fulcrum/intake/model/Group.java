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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.apache.avalon.framework.logger.LogEnabled;
import org.apache.avalon.framework.logger.Logger;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.pool2.BaseKeyedPooledObjectFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;
import org.apache.fulcrum.intake.IntakeException;
import org.apache.fulcrum.intake.IntakeServiceFacade;
import org.apache.fulcrum.intake.Retrievable;
import org.apache.fulcrum.parser.ValueParser;

/**
 * Holds a group of Fields
 *
 * @author <a href="mailto:jmcnally@collab.net">John McNally</a>
 * @author <a href="mailto:hps@intermeta.de">Henning P. Schmiedehausen</a>
 * @author <a href="mailto:quintonm@bellsouth.net">Quinton McCombs</a>
 * @version $Id$
 */
@XmlType(name="group")
@XmlAccessorType(XmlAccessType.NONE)
public class Group implements Serializable, LogEnabled
{
    /** Serial version */
    private static final long serialVersionUID = -5452725641409669284L;

    public static final String EMPTY = "";

    /*
     * An id representing a new object.
     */
    public static final String NEW = "_0";

    /** Logging */
    private transient Logger log;

    /**
     * The key used to represent this group in a parameter.
     * This key is usually a prefix as part of a field key.
     */
    @XmlAttribute(name="key", required=true)
    private String gid;

    /**
     * The name used in templates and java code to refer to this group.
     */
    @XmlAttribute(required=true)
    private String name;

    /**
     * The number of Groups with the same name that will be pooled.
     */
    @XmlAttribute
    private int poolCapacity = 128;

    /**
     * The default map object for this group
     */
    @XmlAttribute(name="mapToObject")
    private String defaultMapToObject;

    /**
     * The parent element in the XML tree
     */
    private AppData parent;

    /**
     * A map of the fields in this group mapped by field name.
     */
    private Map<String, Field<?>> fieldsByName;

    /**
     * Map of the fields by mapToObject
     */
    private Map<String, Field<?>[]> mapToObjectFields;

    /**
     * A list of fields in this group.
     */
    private LinkedList<Field<?>> fields;

    /**
     * The object id used to associate this group to a bean
     * for one request cycle
     */
    private String oid;

    /**
     * The object containing the request data
     */
    private ValueParser pp;

    /**
     * A flag to help prevent duplicate hidden fields declaring this group.
     */
    private boolean isDeclared;

    /**
     * Default constructor
     */
    public Group()
    {
        super();
        this.fields = new LinkedList<Field<?>>();
    }

    /**
	 * Enable Avalon Logging
	 */
	@Override
	public void enableLogging(Logger logger)
	{
		this.log = logger.getChildLogger(getClass().getSimpleName());
	}

	/**
     * Initializes the default Group using parameters.
     *
     * @param pp a <code>ValueParser</code> value
     * @return this Group
     */
    public Group init(ValueParser pp) throws IntakeException
    {
        return init(NEW, pp);
    }

    /**
     * Initializes the Group with parameters from RunData
     * corresponding to key.
     *
     * @param pp a <code>ValueParser</code> value
     * @return this Group
     */
    public Group init(String key, ValueParser pp) throws IntakeException
    {
        this.oid = key;
        this.pp = pp;
        for (ListIterator<Field<?>> i = fields.listIterator(fields.size()); i.hasPrevious();)
        {
            i.previous().init(pp);
        }
        for (ListIterator<Field<?>> i = fields.listIterator(fields.size()); i.hasPrevious();)
        {
            Field<?> field = i.previous();
            if (field.isSet() && !field.isValidated())
            {
                field.validate();
            }
        }
        return this;
    }

    /**
     * Initializes the group with properties from an object.
     *
     * @param obj a <code>Persistent</code> value
     * @return a <code>Group</code> value
     */
    public Group init(Retrievable obj)
    {
        this.oid = obj.getQueryKey();

        Class<?> cls = obj.getClass();
        while (cls != null)
        {
            Field<?>[] flds = mapToObjectFields.get(cls.getName());
            if (flds != null)
            {
                for (int i = flds.length - 1; i >= 0; i--)
                {
                    flds[i].init(obj);
                }
            }

            // Also check any interfaces
            Class<?>[] interfaces = cls.getInterfaces();
            for (int idx = 0; idx < interfaces.length; idx++)
            {
                Field<?>[] interfaceFields =
                    mapToObjectFields.get(interfaces[idx].getName());
                if (interfaceFields != null)
                {
                    for (int i = 0; i < interfaceFields.length; i++)
                    {
                        interfaceFields[i].init(obj);
                    }
                }
            }

            cls = cls.getSuperclass();
        }

        return this;
    }

    /**
     * Gets a list of the names of the fields stored in this object.
     *
     * @return A String array containing the list of names.
     */
    public String[] getFieldNames()
    {
        String nameList[] = new String[fields.size()];
        int i = 0;
        for (Field<?> f : fields)
        {
            nameList[i++] = f.getName();
        }
        return nameList;
    }

    /**
     * Return the name given to this group.  The long name is to
     * avoid conflicts with the get(String key) method.
     *
     * @return a <code>String</code> value
     */
    public String getIntakeGroupName()
    {
        return name;
    }

    /**
     * Get the number of Group objects that will be pooled.
     *
     * @return an <code>int</code> value
     */
    public int getPoolCapacity()
    {
        return poolCapacity;
    }

    /**
     * Get the part of the key used to specify the group.
     * This is specified in the key attribute in the xml file.
     *
     * @return a <code>String</code> value
     */
    public String getGID()
    {
        return gid;
    }

    /**
     * Get the part of the key that distinguishes a group
     * from others of the same name.
     *
     * @return a <code>String</code> value
     */
    public String getOID()
    {
        return oid;
    }

    /**
     * Concatenation of gid and oid.
     *
     * @return a <code>String</code> value
     */
    public String getObjectKey()
    {
        return gid + oid;
    }

    /**
     * Default object to map this group to.
     *
     * @return a <code>String</code> value
     */
    public String getDefaultMapToObject()
    {
        return defaultMapToObject;
    }

    /**
     * Describe <code>getObjects</code> method here.
     *
     * @param pp a <code>ValueParser</code> value
     * @return an <code>ArrayList</code> value
     * @exception IntakeException if an error occurs
     */
    public List<Group> getObjects(ValueParser pp) throws IntakeException
    {
        ArrayList<Group> objs = null;
        String[] oids = pp.getStrings(gid);
        if (oids != null)
        {
            objs = new ArrayList<Group>(oids.length);
            for (int i = oids.length - 1; i >= 0; i--)
            {
                objs.add(IntakeServiceFacade.getGroup(name).init(oids[i], pp));
            }
        }
        return objs;
    }

    /**
     * Get the Field .
     * @return Field.
     * @throws IntakeException indicates the field could not be found.
     */
    public Field<?> get(String fieldName)
            throws IntakeException
    {
        if (fieldsByName.containsKey(fieldName))
        {
            return fieldsByName.get(fieldName);
        }
        else
        {
            throw new IntakeException("Intake Field name: " + fieldName +
                    " not found in Group " + name);
        }
    }

    /**
     * Get the list of Fields .
     * @return list of Fields
     */
    public List<Field<?>> getFields()
    {
        return fields;
    }

    /**
     * Set a collection of fields for this group
     *
     * @param fields the fields to set
     */
    @XmlElement(name="field")
    @XmlJavaTypeAdapter(FieldAdapter.class)
    protected void setFields(List<Field<?>> inputFields)
    {
        fields = new LinkedList<Field<?>>(inputFields);
    }

    /**
     * Performs an AND between all the fields in this group.
     *
     * @return a <code>boolean</code> value
     */
    public boolean isAllValid()
    {
        boolean valid = true;
        for (ListIterator<Field<?>> i = fields.listIterator(fields.size()); i.hasPrevious();)
        {
            Field<?> field = i.previous();
            valid &= field.isValid();
            if (log.isDebugEnabled() && !field.isValid())
            {
                log.debug("Group(" + oid + "): " + name + "; Field: "
                        + field.getName() + "; value=" +
                        field.getValue() + " is invalid!");
            }
        }
        return valid;
    }

    /**
     * Calls a setter methods on obj, for fields which have been set.
     *
     * @param obj Object to be set with the values from the group.
     * @throws IntakeException indicates that a failure occurred while
     * executing the setter methods of the mapped object.
     */
    public void setProperties(Object obj) throws IntakeException
    {
        Class<?> cls = obj.getClass();

        while (cls != null)
        {
            if (log.isDebugEnabled())
            {
                log.debug("setProperties(" + cls.getName() + ")");
            }

            Field<?>[] flds = mapToObjectFields.get(cls.getName());
            if (flds != null)
            {
                for (int i = flds.length - 1; i >= 0; i--)
                {
                    flds[i].setProperty(obj);
                }
            }

            // Also check any interfaces
            Class<?>[] interfaces = cls.getInterfaces();
            for (int idx = 0; idx < interfaces.length; idx++)
            {
                Field<?>[] interfaceFields =
                    mapToObjectFields.get(interfaces[idx].getName());
                if (interfaceFields != null)
                {
                    for (int i = 0; i < interfaceFields.length; i++)
                    {
                        interfaceFields[i].setProperty(obj);
                    }
                }
            }

            cls = cls.getSuperclass();
        }

        log.debug("setProperties() finished");
    }

    /**
     * Calls a setter methods on obj, for fields which pass validity tests.
     * In most cases one should call Intake.isAllValid() and then if that
     * test passes call setProperties.  Use this method when some data is
     * known to be invalid, but you still want to set the object properties
     * that are valid.
     */
    public void setValidProperties(Object obj)
    {
        Class<?> cls = obj.getClass();
        while (cls != null)
        {
            Field<?>[] flds = mapToObjectFields.get(cls.getName());
            if (flds != null)
            {
                for (int i = flds.length - 1; i >= 0; i--)
                {
                    try
                    {
                        flds[i].setProperty(obj);
                    }
                    catch (IntakeException e)
                    {
                        // just move on to next field
                    }
                }
            }

            // Also check any interfaces
            Class<?>[] interfaces = cls.getInterfaces();
            for (int idx = 0; idx < interfaces.length; idx++)
            {
                Field<?>[] interfaceFields =
                    mapToObjectFields.get(interfaces[idx].getName());
                if (interfaceFields != null)
                {
                    for (int i = 0; i < interfaceFields.length; i++)
                    {
                        try
                        {
                            interfaceFields[i].setProperty(obj);
                        }
                        catch(IntakeException e)
                        {
                            // just move on to next field
                        }
                    }
                }
            }

            cls = cls.getSuperclass();
        }
    }

    /**
     * Calls getter methods on objects that are known to Intake
     * so that field values in forms can be initialized from
     * the values contained in the intake tool.
     *
     * @param obj Object that will be used to as a source of data for
     * setting the values of the fields within the group.
     * @throws IntakeException indicates that a failure occurred while
     * executing the setter methods of the mapped object.
     */
    public void getProperties(Object obj) throws IntakeException
    {
        Class<?> cls = obj.getClass();

        while (cls != null)
        {
            Field<?>[] flds = mapToObjectFields.get(cls.getName());
            if (flds != null)
            {
                for (int i = flds.length - 1; i >= 0; i--)
                {
                    flds[i].getProperty(obj);
                }
            }

            // Also check any interfaces
            Class<?>[] interfaces = cls.getInterfaces();
            for (int idx = 0; idx < interfaces.length; idx++)
            {
                Field<?>[] interfaceFields =
                    mapToObjectFields.get(interfaces[idx].getName());
                if (interfaceFields != null)
                {
                    for (int i = 0; i < interfaceFields.length; i++)
                    {
                        interfaceFields[i].getProperty(obj);
                    }
                }
            }

            cls = cls.getSuperclass();
        }
    }

    /**
     * Removes references to this group and its fields from the
     * query parameters
     */
    public void removeFromRequest()
    {
        if (pp != null)
        {
            String[] groups = pp.getStrings(gid);
            if (groups != null)
            {
                pp.remove(gid);
                for (int i = 0; i < groups.length; i++)
                {
                    if (groups[i] != null && !groups[i].equals(oid))
                    {
                        pp.add(gid, groups[i]);
                    }
                }
                for (ListIterator<Field<?>> i = fields.listIterator(fields.size()); i.hasPrevious();)
                {
                    i.previous().removeFromRequest();
                }
            }
        }
    }

    /**
     * To be used in the event this group is used within multiple
     * forms within the same template.
     */
    public void resetDeclared()
    {
        isDeclared = false;
    }

    /**
     * A xhtml valid hidden input field that notifies intake of the
     * group's presence.
     *
     * @return a <code>String</code> value
     */
    public String getHtmlFormInput()
    {
        StringBuilder sb = new StringBuilder(64);
        appendHtmlFormInput(sb);
        return sb.toString();
    }

    /**
     * A xhtml valid hidden input field that notifies intake of the
     * group's presence.
     */
    public void appendHtmlFormInput(StringBuilder sb)
    {
        if (!isDeclared)
        {
            isDeclared = true;
            sb.append("<input type=\"hidden\" name=\"")
                    .append(gid)
                    .append("\" value=\"")
                    .append(oid)
                    .append("\"/>\n");
        }
    }

    /**
     * Creates a string representation of this input group. This
     * is an xml representation.
     */
    @Override
    public String toString()
    {
        StringBuilder result = new StringBuilder();

        result.append("<group name=\"").append(getIntakeGroupName()).append("\"");
        result.append(" key=\"").append(getGID()).append("\"");
        result.append(">\n");

        if (fields != null)
        {
            for (Field<?> field : fields)
            {
                result.append(field);
            }
        }

        result.append("</group>\n");

        return result.toString();
    }

    /**
     * Get the parent AppData for this group
     *
     * @return the parent
     */
    public AppData getAppData()
    {
        return parent;
    }

    /**
     * JAXB callback to set the parent object
     *
     * @param um the Unmarshaller
     * @param parent the parent object (an AppData object)
     */
    public void afterUnmarshal(Unmarshaller um, Object parent)
    {
        this.parent = (AppData)parent;

        // Build map
        fieldsByName = new HashMap<String, Field<?>>((int) (1.25 * fields.size() + 1));

        for (Field<?> field : fields)
        {
            fieldsByName.put(field.getName(), field);
        }

        Map<String, List<Field<?>>> mapToObjectFieldLists =
                new HashMap<String, List<Field<?>>>((int) (1.25 * fields.size() + 1));

        // Fix fields
        for (Field<?> field : fields)
        {
            if (StringUtils.isNotEmpty(field.mapToObject))
            {
                field.mapToObject = this.parent.getBasePackage() + field.mapToObject;
            }

            // map fields by their mapToObject
            List<Field<?>> tmpFields = mapToObjectFieldLists.get(field.getMapToObject());
            if (tmpFields == null)
            {
                tmpFields = new ArrayList<Field<?>>(fields.size());
                mapToObjectFieldLists.put(field.getMapToObject(), tmpFields);
            }

            tmpFields.add(field);
        }

        // Change the mapToObjectFields values to Field[]
        mapToObjectFields = new HashMap<String, Field<?>[]>((int) (1.25 * fields.size() + 1));

        for (Map.Entry<String, List<Field<?>>> entry : mapToObjectFieldLists.entrySet())
        {
            mapToObjectFields.put(entry.getKey(),
                entry.getValue().toArray(new Field[entry.getValue().size()]));
        }
    }

    // ********** PoolableObjectFactory implementation ******************

    public static class GroupFactory
            extends BaseKeyedPooledObjectFactory<String, Group>
    {
        private final AppData appData;

        public GroupFactory(AppData appData)
        {
            this.appData = appData;
        }

        /**
         * Creates an instance that can be returned by the pool.
         * @param key the name of the group
         * @return an instance that can be returned by the pool.
         * @throws IntakeException indicates that the group could not be retrieved
         */
        @Override
        public Group create(String key) throws IntakeException
        {
            return appData.getGroup(key);
        }

        /**
         * @see org.apache.commons.pool2.BaseKeyedPooledObjectFactory#wrap(java.lang.Object)
         */
        @Override
        public PooledObject<Group> wrap(Group group)
        {
            return new DefaultPooledObject<Group>(group);
        }

        /**
         * Uninitialize an instance to be returned to the pool.
         * @param key the name of the group
         * @param pooledGroup the instance to be passivated
         */
        @Override
        public void passivateObject(String key, PooledObject<Group> pooledGroup)
        {
            Group group = pooledGroup.getObject();
            group.oid = null;
            group.pp = null;
            for (ListIterator<Field<?>> i = group.fields.listIterator(group.fields.size());
                    i.hasPrevious();)
            {
                i.previous().dispose();
            }
            group.isDeclared = false;
        }
    }
}


