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
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.avalon.framework.activity.Initializable;
import org.apache.avalon.framework.configuration.Configurable;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.context.Context;
import org.apache.avalon.framework.context.ContextException;
import org.apache.avalon.framework.context.Contextualizable;
import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.avalon.framework.service.ServiceException;
import org.apache.avalon.framework.service.ServiceManager;
import org.apache.avalon.framework.service.Serviceable;
import org.apache.commons.pool.KeyedObjectPool;
import org.apache.commons.pool.KeyedPoolableObjectFactory;
import org.apache.commons.pool.impl.StackKeyedObjectPool;
import org.apache.fulcrum.intake.model.Group;
import org.apache.fulcrum.intake.transform.XmlToAppData;
import org.apache.fulcrum.intake.xmlmodel.AppData;
import org.apache.fulcrum.intake.xmlmodel.XmlGroup;

/**
 * This service provides access to input processing objects based on an XML
 * specification.
 *
 * @author <a href="mailto:jmcnally@collab.net">John McNally</a>
 * @author <a href="mailto:hps@intermeta.de">Henning P. Schmiedehausen</a>
 * @author <a href="mailto:quintonm@bellsouth.net">Quinton McCombs</a>
 * @version $Id$
 *
 * @avalon.component name="intake"
 * @avalon.service type="org.apache.fulcrum.intake.IntakeService"
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
    private Map<AppData, KeyedObjectPool> keyedPools;

    /** The Avalon Container root directory */
    private String applicationRoot;

    /** List of configured xml specification files */
    private List<String> xmlPathes = null;

    /** Configured location of the serialization file */
    private String serialDataPath = null;

    /**
     * Registers a given group name in the system
     *
     * @param groupName
     *            The name to register the group under
     * @param group
     *            The XML Group to register in
     * @param appData
     *            The app Data object where the group can be found
     * @param checkKey
     *            Whether to check if the key also exists.
     *
     * @return true if successful, false if not
     */
    private boolean registerGroup(String groupName, XmlGroup group,
            AppData appData, boolean checkKey)
    {
        if (groupNames.keySet().contains(groupName))
        {
            // This name already exists.
            return false;
        }

        boolean keyExists = groupNameMap.keySet().contains(group.getKey());

        if (checkKey && keyExists)
        {
            // The key for this package is already registered for another group
            return false;
        }

        groupNames.put(groupName, appData);

        groupKeyMap.put(groupName, group.getKey());

        if (!keyExists)
        {
            // This key does not exist. Add it to the hash.
            groupNameMap.put(group.getKey(), groupName);
        }

        List<String> classNames = group.getMapToObjects();
        for (String className : classNames)
        {
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

        InputStream in = null;
        Map<AppData, File> serialData = null;

        try
        {
            in = new FileInputStream(serialDataFile);
            ObjectInputStream p = new ObjectInputStream(in);
            Object o = p.readObject();

            if (o instanceof Map)
            {
                serialData = (Map<AppData, File>) o;
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

        getLogger().info("Loaded serialized map object, ignoring XML");
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
                            + ", not serializing the XML data");
            return;
        }

        OutputStream out = null;
        InputStream in = null;

        try
        {
            // write the appData file out
            out = new FileOutputStream(serialDataPath);
            ObjectOutputStream pout = new ObjectOutputStream(out);
            pout.writeObject(appDataElements);
            pout.flush();

            // read the file back in. for some reason on OSX 10.1
            // this is necessary.
            in = new FileInputStream(serialDataPath);
            ObjectInputStream pin = new ObjectInputStream(in);
            /* Map dummy = (Map) */ pin.readObject();

            getLogger().debug("Serializing successful");
        }
        catch (IOException e)
        {
            getLogger().info(
                    "Could not write serialized file to " + serialDataPath
                            + ", not serializing the XML data");
        }
        catch (ClassNotFoundException e)
        {
            getLogger().info(
                    "Could not re-read serialized file from " + serialDataPath);
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
            group = (Group) keyedPools.get(appData).borrowObject(groupName);
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
                new IntakeException("Could not get group " + groupName, e);
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
    public int getSize(String groupName) throws IntakeException
    {
        AppData appData = groupNames.get(groupName);
        if (appData == null)
        {
            throw new IntakeException(
                    "Intake IntakeServiceImpl.Size(groupName): No XML definition for Group "
                            + groupName + " found");
        }

        KeyedObjectPool kop = keyedPools.get(groupName);

        return kop.getNumActive(groupName) + kop.getNumIdle(groupName);
    }

    /**
     * Names of all the defined groups.
     *
     * @return array of names.
     */
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
     * @throws ClassNotFoundException
     * @throws IntrospectionException
     */
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
     * @throws ClassNotFoundException
     * @throws IntrospectionException
     */
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
     * default class loaders and customized object factories.
     *
     * @throws Exception
     *             if initialization fails.
     */
    public void initialize() throws Exception
    {
        Map<AppData, File> appDataElements = null;

        groupNames = new HashMap<String, AppData>();
        groupKeyMap = new HashMap<String, String>();
        groupNameMap = new HashMap<String, String>();
        getterMap = new HashMap<String, Map<String,Method>>();
        setterMap = new HashMap<String, Map<String,Method>>();
        keyedPools = new HashMap<AppData, KeyedObjectPool>();

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
            // Parse all the given XML files
            appDataElements = new HashMap<AppData, File>();

            for (File xmlFile : xmlFiles)
            {
                AppData appData = null;

                getLogger().debug("Now parsing: " + xmlFile);

                XmlToAppData xmlApp = new XmlToAppData();
                xmlApp.enableLogging(getLogger());
                appData = xmlApp.parseFile(xmlFile);

                appDataElements.put(appData, xmlFile);
                getLogger().debug("Saving appData for " + xmlFile);
            }

            saveSerialized(serialDataPath, appDataElements);
        }

        for (AppData appData : appDataElements.keySet())
        {
            int maxPooledGroups = 0;
            List<XmlGroup> glist = appData.getGroups();

            String groupPrefix = appData.getGroupPrefix();

            for (int i = glist.size() - 1; i >= 0; i--)
            {
                XmlGroup g = glist.get(i);
                String groupName = g.getName();

                boolean registerUnqualified = registerGroup(groupName, g,
                        appData, true);

                if (!registerUnqualified)
                {
                    getLogger().info(
                            "Ignored redefinition of Group " + groupName
                                    + " or Key " + g.getKey() + " from "
                                    + appDataElements.get(appData));
                }

                if (groupPrefix != null)
                {
                    StringBuffer qualifiedName = new StringBuffer();
                    qualifiedName.append(groupPrefix).append(':').append(
                            groupName);

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

                maxPooledGroups = Math.max(maxPooledGroups, Integer
                        .parseInt(g.getPoolCapacity()));

            }

            KeyedPoolableObjectFactory factory = new Group.GroupFactory(
                    appData);
            keyedPools.put(appData, new StackKeyedObjectPool(factory,
                    maxPooledGroups));
        }

        if (getLogger().isInfoEnabled())
        {
            getLogger().info("Intake Service is initialized now.");
        }
    }

    /**
     * @see org.apache.avalon.framework.context.Contextualizable
     * @avalon.entry key="urn:avalon:home" type="java.io.File"
     */
    public void contextualize(Context context) throws ContextException
    {
        this.applicationRoot = context.get("urn:avalon:home").toString();
    }

    /**
     * Avalon component lifecycle method
     *
     * @avalon.dependency type="org.apache.fulcrum.localization.LocalizationService"
     */
    public void service(ServiceManager manager) throws ServiceException
    {
        IntakeServiceFacade.setIntakeService(this);
    }
}
