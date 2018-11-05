package org.apache.fulcrum.intake;

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

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;

import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.Unmarshaller.Listener;
import javax.xml.bind.helpers.DefaultValidationEventHandler;
import javax.xml.validation.SchemaFactory;

import org.apache.avalon.framework.activity.Initializable;
import org.apache.avalon.framework.configuration.Configurable;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.context.Context;
import org.apache.avalon.framework.context.ContextException;
import org.apache.avalon.framework.context.Contextualizable;
import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.avalon.framework.logger.LogEnabled;
import org.apache.avalon.framework.service.ServiceException;
import org.apache.avalon.framework.service.ServiceManager;
import org.apache.avalon.framework.service.Serviceable;
import org.apache.commons.pool2.KeyedObjectPool;
import org.apache.commons.pool2.KeyedPooledObjectFactory;
import org.apache.commons.pool2.impl.GenericKeyedObjectPool;
import org.apache.commons.pool2.impl.GenericKeyedObjectPoolConfig;
import org.apache.fulcrum.intake.model.AppData;
import org.apache.fulcrum.intake.model.Field;
import org.apache.fulcrum.intake.model.Group;

/**
 * This service provides access to input processing objects based on an XML
 * specification.
 *
 * avalon.component name="intake"
 * avalon.service type="org.apache.fulcrum.intake.IntakeService"
 *
 * @author <a href="mailto:jmcnally@collab.net">John McNally</a>
 * @author <a href="mailto:hps@intermeta.de">Henning P. Schmiedehausen</a>
 * @author <a href="mailto:quintonm@bellsouth.net">Quinton McCombs</a>
 * @version $Id$
 */
public class IntakeServiceImpl extends AbstractLogEnabled implements
        IntakeService, Configurable, Initializable, Contextualizable,
        Serviceable
{
    /** Map of groupNames -> appData elements */
    private Map<String, AppData> groupNames;

    /** The cache of group names. */
    private Map<String, String> groupNameMap;

    /** The cache of group keys. */
    private Map<String, String> groupKeyMap;

    /** The cache of property getters. */
    private Map<String, Map<String, Method>> getterMap;

    /** The cache of property setters. */
    private Map<String, Map<String, Method>> setterMap;

    /** AppData -> keyed Pools Map */
    private Map<AppData, KeyedObjectPool<String, Group>> keyedPools;

    /** The Avalon Container root directory */
    private String applicationRoot;

    /** List of configured xml specification files */
    private List<String> xmlPathes = null;

    /** Configured location of the serialization file */
    private String serialDataPath = null;

    /**
     * Local Class to enable Avalon logging on the model classes
     *
     */
    private class AvalonLogEnabledListener extends Listener
    {
		/**
		 * @see javax.xml.bind.Unmarshaller.Listener#beforeUnmarshal(java.lang.Object, java.lang.Object)
		 */
		@Override
		public void beforeUnmarshal(Object target, Object parent)
		{
			super.beforeUnmarshal(target, parent);

			if (target instanceof LogEnabled)
			{
				((LogEnabled)target).enableLogging(getLogger());
			}
		}

    }

    /**
     * Registers a given group name in the system
     *
     * @param groupName
     *            The name to register the group under
     * @param group
     *            The Group to register in
     * @param appData
     *            The app Data object where the group can be found
     * @param checkKey
     *            Whether to check if the key also exists.
     *
     * @return true if successful, false if not
     */
    private boolean registerGroup(String groupName, Group group,
            AppData appData, boolean checkKey)
    {
        if (groupNames.containsKey(groupName))
        {
            // This name already exists.
            return false;
        }

        boolean keyExists = groupNameMap.containsKey(group.getGID());

        if (checkKey && keyExists)
        {
            // The key for this package is already registered for another group
            return false;
        }

        groupNames.put(groupName, appData);
        groupKeyMap.put(groupName, group.getGID());

        if (!keyExists)
        {
            // This key does not exist. Add it to the hash.
            groupNameMap.put(group.getGID(), groupName);
        }

        List<Field<?>> fields = group.getFields();
        for (Field<?> field : fields)
        {
            String className = field.getMapToObject();
            if (!getterMap.containsKey(className))
            {
                getterMap.put(className, new HashMap<String, Method>());
                setterMap.put(className, new HashMap<String, Method>());
            }
        }
        return true;
    }

    /**
     * Tries to load a serialized Intake Group file. This can reduce the startup
     * time of Turbine.
     *
     * @param serialDataPath
     *            The path of the File to load.
     *
     * @return A map with appData objects loaded from the file or null if the
     *         map could not be loaded.
     */
    private Map<AppData, File> loadSerialized(String serialDataPath, long timeStamp)
    {
        getLogger().debug(
                "Entered loadSerialized(" + serialDataPath + ", " + timeStamp
                        + ")");

        long timer = System.currentTimeMillis();

        if (serialDataPath == null)
        {
            return null;
        }

        File serialDataFile = new File(serialDataPath);

        if (!serialDataFile.exists())
        {
            getLogger().info("No serialized file found, parsing XML");
            return null;
        }

        if (serialDataFile.lastModified() <= timeStamp)
        {
            getLogger().info("serialized file too old, parsing XML");
            return null;
        }

        ObjectInputStream in = null;
        Map<AppData, File> serialData = null;

        try
        {
        	FileInputStream fin = new FileInputStream(serialDataFile);
            in = new ObjectInputStream(fin);
            Object o = in.readObject();

            if (o instanceof Map)
            {
                @SuppressWarnings("unchecked") // checked with instanceof
				Map<AppData, File> map = (Map<AppData, File>) o;
				serialData = map;
            }
            else
            {
                // Maybe an old file from intake. Ignore it and try to delete
                getLogger().info(
                        "serialized object is not an intake map, ignoring");
                in.close();
                in = null;
                serialDataFile.delete(); // Try to delete the file lying
                                            // around
            }
        }
        catch (IOException e)
        {
            getLogger().error("Serialized File could not be read.", e);

            // We got a corrupt file for some reason.
            // Null out serialData to be sure
            serialData = null;
        }
        catch (ClassNotFoundException e)
        {
            getLogger().error("Objects could not be read from serialized file.", e);

            // This should not happen
            // Null out serialData to be sure
            serialData = null;
        }
        finally
        {
            // Could be null if we opened a file, didn't find it to be a
            // Map object and then nuked it away.
            try
            {
                if (in != null)
                {
                    in.close();
                }
            }
            catch (IOException e)
            {
                getLogger().error("Exception while closing file", e);
            }
        }

        // Recreate transient loggers
        if (serialData != null)
        {
        	for (AppData appData : serialData.keySet())
        	{
        		for (Group group : appData.getGroups())
        		{
        			if (group instanceof LogEnabled)
        			{
        				((LogEnabled)group).enableLogging(getLogger());
        			}

    				for (Field<?> field : group.getFields())
    				{
            			if (field instanceof LogEnabled)
            			{
            				((LogEnabled)field).enableLogging(getLogger());
            			}
    				}
        		}
        	}
        }
        getLogger().info("Loaded serialized map object, ignoring XML");
        getLogger().debug("Loading took " + (System.currentTimeMillis() - timer));
        return serialData;
    }

    /**
     * Writes a parsed XML map with all the appData groups into a file. This
     * will speed up loading time when you restart the Intake Service because it
     * will only unserialize this file instead of reloading all of the XML files
     *
     * @param serialDataPath
     *            The path of the file to write to
     * @param appDataElements
     *            A Map containing all of the XML parsed appdata elements
     */
    private void saveSerialized(String serialDataPath, Map<AppData, File> appDataElements)
    {

        getLogger().debug(
                "Entered saveSerialized(" + serialDataPath
                        + ", appDataElements)");

        long timer = System.currentTimeMillis();

        if (serialDataPath == null)
        {
            return;
        }

        File serialData = new File(serialDataPath);

        try
        {
            serialData.createNewFile();
            serialData.delete();
        }
        catch (IOException e)
        {
            getLogger().info(
                    "Could not create serialized file " + serialDataPath
                            + ", not serializing the XML data", e);
            return;
        }

        ObjectOutputStream out = null;
        ObjectInputStream in = null;

        try
        {
            // write the appData file out
        	FileOutputStream fout = new FileOutputStream(serialDataPath);
            out = new ObjectOutputStream(fout);
            out.writeObject(appDataElements);
            out.flush();

            // read the file back in. for some reason on OSX 10.1
            // this is necessary.
            FileInputStream fin = new FileInputStream(serialDataPath);
            in = new ObjectInputStream(fin);
            /* Map dummy = (Map) */ in.readObject();

            getLogger().debug("Serializing successful");
        }
        catch (IOException e)
        {
            getLogger().info(
                    "Could not write serialized file to " + serialDataPath
                            + ", not serializing the XML data", e);
        }
        catch (ClassNotFoundException e)
        {
            getLogger().info(
                    "Could not re-read serialized file from " + serialDataPath, e);
        }
        finally
        {
            try
            {
                if (out != null)
                {
                    out.close();
                }
            }
            catch (IOException e)
            {
                getLogger().error("Exception while closing file", e);
            }
            try
            {
                if (in != null)
                {
                    in.close();
                }
            }
            catch (IOException e)
            {
                getLogger().error("Exception while closing file", e);
            }
        }

        getLogger().debug("Saving took " + (System.currentTimeMillis() - timer));
    }

    /**
     * Gets an instance of a named group either from the pool or by calling the
     * Factory Service if the pool is empty.
     *
     * @param groupName
     *            the name of the group.
     * @return a Group instance.
     * @throws IntakeException
     *             if recycling fails.
     */
    @Override
    public Group getGroup(String groupName) throws IntakeException
    {
        Group group = null;

        AppData appData = groupNames.get(groupName);

        if (groupName == null)
        {
            throw new IntakeException(
                    "Intake IntakeServiceImpl.getGroup(groupName) is null");
        }

        if (appData == null)
        {
            throw new IntakeException(
                    "Intake IntakeServiceImpl.getGroup(groupName): No XML definition for Group "
                            + groupName + " found");
        }
        try
        {
            group = keyedPools.get(appData).borrowObject(groupName);
        }
        catch (Exception e)
        {
            throw new IntakeException("Could not get group " + groupName, e);
        }
        return group;
    }

    /**
     * Puts a Group back to the pool.
     *
     * @param instance
     *            the object instance to recycle.
     *
     * @throws IntakeException
     *             The passed group name does not exist.
     */
    @Override
    public void releaseGroup(Group instance) throws IntakeException
    {
        if (instance != null)
        {
            String groupName = instance.getIntakeGroupName();
            AppData appData = groupNames.get(groupName);

            if (appData == null)
            {
                throw new IntakeException(
                        "Intake IntakeServiceImpl.releaseGroup(groupName): "
                                + "No XML definition for Group " + groupName
                                + " found");
            }

            try
            {
                keyedPools.get(appData).returnObject(groupName, instance);
            }
            catch (Exception e)
            {
                throw new IntakeException("Could not get group " + groupName, e);
            }
        }
    }

    /**
     * Gets the current size of the pool for a group.
     *
     * @param groupName
     *            the name of the group.
     *
     * @throws IntakeException
     *             The passed group name does not exist.
     */
    @Override
    public int getSize(String groupName) throws IntakeException
    {
        AppData appData = groupNames.get(groupName);
        if (appData == null)
        {
            throw new IntakeException(
                    "Intake IntakeServiceImpl.Size(groupName): No XML definition for Group "
                            + groupName + " found");
        }

        KeyedObjectPool<String, Group> kop = keyedPools.get(appData);

        return kop.getNumActive(groupName) + kop.getNumIdle(groupName);
    }

    /**
     * Names of all the defined groups.
     *
     * @return array of names.
     */
    @Override
    public String[] getGroupNames()
    {
        return groupNames.keySet().toArray(new String[0]);
    }

    /**
     * Gets the key (usually a short identifier) for a group.
     *
     * @param groupName
     *            the name of the group.
     * @return the the key.
     */
    @Override
    public String getGroupKey(String groupName)
    {
        return groupKeyMap.get(groupName);
    }

    /**
     * Gets the group name given its key.
     *
     * @param groupKey
     *            the key.
     * @return groupName the name of the group.
     */
    @Override
    public String getGroupName(String groupKey)
    {
        return groupNameMap.get(groupKey);
    }

    /**
     * Gets the Method that can be used to set a property.
     *
     * @param className
     *            the name of the object.
     * @param propName
     *            the name of the property.
     * @return the setter.
     * @throws ClassNotFoundException if the class specified could not be loaded
     * @throws IntrospectionException if the property setter could not be called
     */
    @Override
    public Method getFieldSetter(String className, String propName)
            throws ClassNotFoundException, IntrospectionException
    {
        Map<String, Method> settersForClassName = setterMap.get(className);

        if (settersForClassName == null)
        {
            throw new IntrospectionException("No setter Map for " + className
                    + " available!");
        }

        Method setter = settersForClassName.get(propName);

        if (setter == null)
        {
            PropertyDescriptor pd = new PropertyDescriptor(propName, Class
                    .forName(className));
            synchronized (setterMap)
            {
                setter = pd.getWriteMethod();
                settersForClassName.put(propName, setter);
                if (setter == null)
                {
                    getLogger().error(
                            "Intake: setter for '" + propName + "' in class '"
                                    + className + "' could not be found.");
                }
            }
            // we have already completed the reflection on the getter, so
            // save it so we do not have to repeat
            synchronized (getterMap)
            {
                Map<String, Method> gettersForClassName = getterMap.get(className);

                if (gettersForClassName != null)
                {
                    Method getter = pd.getReadMethod();
                    if (getter != null)
                    {
                        gettersForClassName.put(propName, getter);
                    }
                }
            }
        }
        return setter;
    }

    /**
     * Gets the Method that can be used to get a property value.
     *
     * @param className
     *            the name of the object.
     * @param propName
     *            the name of the property.
     * @return the getter.
     * @throws ClassNotFoundException if the class specified could not be loaded
     * @throws IntrospectionException if the property getter could not be called
     */
    @Override
    public Method getFieldGetter(String className, String propName)
            throws ClassNotFoundException, IntrospectionException
    {
        Map<String, Method> gettersForClassName = getterMap.get(className);

        if (gettersForClassName == null)
        {
            throw new IntrospectionException("No getter Map for " + className
                    + " available!");
        }

        Method getter = gettersForClassName.get(propName);

        if (getter == null)
        {
            PropertyDescriptor pd = null;
            synchronized (getterMap)
            {
                pd = new PropertyDescriptor(propName, Class.forName(className));
                getter = pd.getReadMethod();
                gettersForClassName.put(propName, getter);
                if (getter == null)
                {
                    getLogger().error(
                            "Intake: getter for '" + propName + "' in class '"
                                    + className + "' could not be found.");
                }
            }
            // we have already completed the reflection on the setter, so
            // save it so we do not have to repeat
            synchronized (setterMap)
            {
                Map<String, Method> settersForClassName = getterMap.get(className);

                if (settersForClassName != null)
                {
                    Method setter = pd.getWriteMethod();
                    if (setter != null)
                    {
                        settersForClassName.put(propName, setter);
                    }
                }
            }
        }
        return getter;
    }

    // ---------------- Avalon Lifecycle Methods ---------------------
    /**
     * Avalon component lifecycle method
     */
    @Override
    public void configure(Configuration conf) throws ConfigurationException
    {
        final Configuration xmlPaths = conf.getChild(XML_PATHS, false);

        xmlPathes = new ArrayList<String>();

        if (xmlPaths == null)
        {
            xmlPathes.add(XML_PATH_DEFAULT);
        }
        else
        {
            Configuration[] nameVal = xmlPaths.getChildren();
            for (int i = 0; i < nameVal.length; i++)
            {
                String val = nameVal[i].getValue();
                xmlPathes.add(val);
            }
        }

        serialDataPath = conf.getChild(SERIAL_XML, false).getValue(SERIAL_XML_DEFAULT);

        if (!serialDataPath.equalsIgnoreCase("none"))
        {
            serialDataPath = new File(applicationRoot, serialDataPath).getAbsolutePath();
        }
        else
        {
            serialDataPath = null;
        }

        getLogger().debug("Path for serializing: " + serialDataPath);
    }

    /**
     * Avalon component lifecycle method Initializes the service by loading
     * xml rule files and creating the Intake groups.
     *
     * @throws Exception
     *             if initialization fails.
     */
    @Override
    public void initialize() throws Exception
    {
        Map<AppData, File> appDataElements = null;

        groupNames = new HashMap<String, AppData>();
        groupKeyMap = new HashMap<String, String>();
        groupNameMap = new HashMap<String, String>();
        getterMap = new HashMap<String, Map<String,Method>>();
        setterMap = new HashMap<String, Map<String,Method>>();
        keyedPools = new HashMap<AppData, KeyedObjectPool<String, Group>>();

        Set<File> xmlFiles = new HashSet<File>();

        long timeStamp = 0;

        for (String xmlPath : xmlPathes)
        {
            // Files are webapp.root relative
            File xmlFile = new File(applicationRoot, xmlPath).getAbsoluteFile();

            getLogger().debug("Path for XML File: " + xmlFile);

            if (!xmlFile.canRead())
            {
                String READ_ERR = "Could not read input file with path "
                        + xmlPath + ".  Looking for file " + xmlFile;

                getLogger().error(READ_ERR);
                throw new Exception(READ_ERR);
            }

            xmlFiles.add(xmlFile);

            getLogger().debug("Added " + xmlPath + " as File to parse");

            // Get the timestamp of the youngest file to be compared with
            // a serialized file. If it is younger than the serialized file,
            // then we have to parse the XML anyway.
            timeStamp = (xmlFile.lastModified() > timeStamp) ? xmlFile
                    .lastModified() : timeStamp;
        }

        Map<AppData, File> serializedMap = loadSerialized(serialDataPath, timeStamp);

        if (serializedMap != null)
        {
            // Use the serialized data as XML groups. Don't parse.
            appDataElements = serializedMap;
            getLogger().debug("Using the serialized map");
        }
        else
        {
        	long timer = System.currentTimeMillis();

            // Parse all the given XML files
            JAXBContext jaxb = JAXBContext.newInstance(AppData.class);
            Unmarshaller um = jaxb.createUnmarshaller();

            // Debug mapping
            um.setEventHandler(new DefaultValidationEventHandler());

            // Enable logging
            Listener logEnabledListener = new AvalonLogEnabledListener();
            um.setListener(logEnabledListener);

            URL schemaURL = getClass().getResource("/intake.xsd");
            SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
            um.setSchema(schemaFactory.newSchema(schemaURL));
            appDataElements = new HashMap<AppData, File>();

            for (File xmlFile : xmlFiles)
            {
                getLogger().debug("Now parsing: " + xmlFile);
                FileInputStream fis = null;
                try
                {
                    fis = new FileInputStream(xmlFile);
                    AppData appData = (AppData) um.unmarshal(fis);

                    appDataElements.put(appData, xmlFile);
                    getLogger().debug("Saving AppData for " + xmlFile);
                }
                finally
                {
                    if (fis != null)
                    {
                        try
                        {
                            fis.close();
                        }
                        catch (IOException e)
                        {
                            getLogger().warn("Could not close file " + xmlFile);
                        }
                    }
                }
            }

            getLogger().debug("Parsing took " + (System.currentTimeMillis() - timer));
            saveSerialized(serialDataPath, appDataElements);
        }

        int counter = 0;
        for (AppData appData : appDataElements.keySet())
        {
            int maxPooledGroups = 0;
            List<Group> glist = appData.getGroups();

            String groupPrefix = appData.getGroupPrefix();

            for (ListIterator<Group> i = glist.listIterator(glist.size()); i.hasPrevious();)
            {
                Group g = i.previous();
                String groupName = g.getIntakeGroupName();

                boolean registerUnqualified = registerGroup(groupName, g, appData, true);

                if (!registerUnqualified)
                {
                    getLogger().info(
                            "Ignored redefinition of Group " + groupName
                                    + " or Key " + g.getGID() + " from "
                                    + appDataElements.get(appData));
                }

                if (groupPrefix != null)
                {
                    StringBuilder qualifiedName = new StringBuilder();
                    qualifiedName.append(groupPrefix).append(':').append(groupName);

                    // Add the fully qualified group name. Do _not_ check
                    // for
                    // the existence of the key if the unqualified
                    // registration succeeded
                    // (because then it was added by the registerGroup
                    // above).
                    if (!registerGroup(qualifiedName.toString(), g,
                            appData, !registerUnqualified))
                    {
                        getLogger().error(
                            "Could not register fully qualified name "
                                    + qualifiedName
                                    + ", maybe two XML files have the same prefix. Ignoring it.");
                    }
                }

                // Init fields
                for (Field<?> f : g.getFields())
                {
                    f.initGetterAndSetter();
                }

                maxPooledGroups = Math.max(maxPooledGroups, g.getPoolCapacity());
            }

            KeyedPooledObjectFactory<String, Group> factory =
                new Group.GroupFactory(appData);

            GenericKeyedObjectPoolConfig poolConfig = new GenericKeyedObjectPoolConfig();
            poolConfig.setMaxTotalPerKey(maxPooledGroups);
            poolConfig.setJmxEnabled(true);
            poolConfig.setJmxNamePrefix("fulcrum-intake-pool-" + counter++);

            keyedPools.put(appData,
                new GenericKeyedObjectPool<String, Group>(factory, poolConfig));
        }

        if (getLogger().isInfoEnabled())
        {
            getLogger().info("Intake Service is initialized now.");
        }
    }

    /**
     * Note that the avalon.entry key="urn:avalon:home" 
     * and the type is {@link java.io.File}
     * 
     * {@link org.apache.avalon.framework.context.Contextualizable#contextualize(Context)}
     * 
     * @param context the Context to use
     * @throws ContextException if the context is not found
     */
    @Override
    public void contextualize(Context context) throws ContextException
    {
        this.applicationRoot = context.get("urn:avalon:home").toString();
    }

    /**
     * Avalon component lifecycle method
     *
     * avalon.dependency type="org.apache.fulcrum.localization.LocalizationService"
     * 
     * @param manager the service manager
     * @throws ServiceException generic exception
     */
    @Override
    public void service(ServiceManager manager) throws ServiceException
    {
        IntakeServiceFacade.setIntakeService(this);
    }
}
