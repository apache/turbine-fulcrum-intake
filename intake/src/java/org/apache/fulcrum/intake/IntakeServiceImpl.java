package org.apache.fulcrum.intake;

/* ====================================================================
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2001-2003 The Apache Software Foundation.  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgment:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgment may appear in the software itself,
 *    if and wherever such third-party acknowledgments normally appear.
 *
 * 4. The names "Apache" and "Apache Software Foundation" and
 *    "Apache Turbine" must not be used to endorse or promote products
 *    derived from this software without prior written permission. For
 *    written permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache",
 *    "Apache Turbine", nor may "Apache" appear in their name, without
 *    prior written permission of the Apache Software Foundation.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 */

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import org.apache.avalon.framework.activity.Initializable;
import org.apache.avalon.framework.service.ServiceException;
import org.apache.avalon.framework.service.ServiceManager;
import org.apache.avalon.framework.service.Serviceable;
import org.apache.avalon.framework.configuration.Configurable;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.context.Context;
import org.apache.avalon.framework.context.ContextException;
import org.apache.avalon.framework.context.Contextualizable;
import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.pool.KeyedObjectPool;
import org.apache.commons.pool.KeyedPoolableObjectFactory;
import org.apache.commons.pool.impl.StackKeyedObjectPool;
import org.apache.fulcrum.intake.model.Group;
import org.apache.fulcrum.intake.transform.XmlToAppData;
import org.apache.fulcrum.intake.xmlmodel.AppData;
import org.apache.fulcrum.intake.xmlmodel.XmlGroup;
import org.apache.fulcrum.localization.LocalizationService;
/**
 * This service provides access to input processing objects based
 * on an XML specification.
 *
 * @author <a href="mailto:jmcnally@collab.net">John McNally</a>
 * @author <a href="mailto:hps@intermeta.de">Henning P. Schmiedehausen</a>
 * @author <a href="mailto:quintonm@bellsouth.net">Quinton McCombs</a>
 * @version $Id$
 *
 * @avalon.component name="intake"
 * @avalon.service type="org.apache.fulcrum.intake.IntakeService"
 */
public class IntakeServiceImpl
    extends AbstractLogEnabled
    implements
        IntakeService,
        Configurable,
        Initializable,
        Contextualizable,
        Serviceable {
    /** Map of groupNames -> appData elements */
    private Map groupNames;

    /** The cache of group names. */
    private Map groupNameMap;

    /** The cache of group keys. */
    private Map groupKeyMap;

    /** The cache of property getters. */
    private Map getterMap;

    /** The cache of property setters. */
    private Map setterMap;

    /** AppData -> keyed Pools Map */
    private Map keyedPools;

    /** Used for logging */
    private static Log log = LogFactory.getLog(IntakeServiceImpl.class);

    /** The Avalon Context */
    private Context context = null;

    /** The Avalon ComponentManager */
    private ServiceManager manager = null;

    private String applicationRoot;

    /**
     * The property specifying the location of the xml specification.
     */
    String XML_PATHS = "xmlPaths";

    /**
     * The property specifying the location where a serialized version of
     * the xml specification can be written for faster restarts..
     */
    String SERIAL_XML = "serialDataPath";

    /**
     * Registers a given group name in the system
     *
     * @param groupName The name to register the group under
     * @param group The XML Group to register in
     * @param appData The app Data object where the group can be found
     * @param checkKey Whether to check if the key also exists.
     *
     * @return true if successful, false if not
     */
    private boolean registerGroup(
        String groupName,
        XmlGroup group,
        AppData appData,
        boolean checkKey) {
        if (groupNames.keySet().contains(groupName)) {
            // This name already exists.
            return false;
        }

        boolean keyExists = groupNameMap.keySet().contains(group.getKey());

        if (checkKey && keyExists) {
            // The key for this package is already registered for another group
            return false;
        }

        groupNames.put(groupName, appData);

        groupKeyMap.put(groupName, group.getKey());

        if (!keyExists) {
            // This key does not exist. Add it to the hash.
            groupNameMap.put(group.getKey(), groupName);
        }

        List classNames = group.getMapToObjects();
        for (Iterator iter2 = classNames.iterator(); iter2.hasNext();) {
            String className = (String) iter2.next();
            if (!getterMap.containsKey(className)) {
                getterMap.put(className, new HashMap());
                setterMap.put(className, new HashMap());
            }
        }
        return true;
    }

    /**
     * Tries to load a serialized Intake Group file. This
     * can reduce the startup time of Turbine.
     *
     * @param serialDataPath The path of the File to load.
     *
     * @return A map with appData objects loaded from the file
     *          or null if the map could not be loaded.
     */
    private Map loadSerialized(String serialDataPath, long timeStamp) {
        log.debug(
            "Entered loadSerialized("
                + serialDataPath
                + ", "
                + timeStamp
                + ")");

        if (serialDataPath == null) {
            return null;
        }

        File serialDataFile = new File(serialDataPath);

        if (!serialDataFile.exists()) {
            log.info("No serialized file found, parsing XML");
            return null;
        }

        if (serialDataFile.lastModified() <= timeStamp) {
            log.info("serialized file too old, parsing XML");
            return null;
        }

        InputStream in = null;
        Map serialData = null;

        try {
            in = new FileInputStream(serialDataFile);
            ObjectInputStream p = new ObjectInputStream(in);
            Object o = p.readObject();

            if (o instanceof Map) {
                serialData = (Map) o;
            } else {
                // Maybe an old file from intake. Ignore it and try to delete
                log.info("serialized object is not an intake map, ignoring");
                in.close();
                in = null;
                serialDataFile.delete(); // Try to delete the file lying around
            }
        } catch (Exception e) {
            log.error("Serialized File could not be read.", e);

            // We got a corrupt file for some reason.
            // Null out serialData to be sure
            serialData = null;
        } finally {
            // Could be null if we opened a file, didn't find it to be a
            // Map object and then nuked it away.
            try {
                if (in != null) {
                    in.close();
                }
            } catch (Exception e) {
                log.error("Exception while closing file", e);
            }
        }

        log.info("Loaded serialized map object, ignoring XML");
        return serialData;
    }

    /**
     * Writes a parsed XML map with all the appData groups into a
     * file. This will speed up loading time when you restart the
     * Intake Service because it will only unserialize this file instead
     * of reloading all of the XML files
     *
     * @param serialDataPath  The path of the file to write to
     * @param appDataElements A Map containing all of the XML parsed appdata elements
     */
    private void saveSerialized(String serialDataPath, Map appDataElements) {

        log.debug(
            "Entered saveSerialized(" + serialDataPath + ", appDataElements)");

        if (serialDataPath == null) {
            return;
        }

        File serialData = new File(serialDataPath);

        try {
            serialData.createNewFile();
            serialData.delete();
        } catch (Exception e) {
            log.info(
                "Could not create serialized file "
                    + serialDataPath
                    + ", not serializing the XML data");
            return;
        }

        OutputStream out = null;
        InputStream in = null;

        try {
            // write the appData file out
            out = new FileOutputStream(serialDataPath);
            ObjectOutputStream pout = new ObjectOutputStream(out);
            pout.writeObject(appDataElements);
            pout.flush();

            // read the file back in. for some reason on OSX 10.1
            // this is necessary.
            in = new FileInputStream(serialDataPath);
            ObjectInputStream pin = new ObjectInputStream(in);
            Map dummy = (Map) pin.readObject();

            log.debug("Serializing successful");
        } catch (Exception e) {
            log.info(
                "Could not write serialized file to "
                    + serialDataPath
                    + ", not serializing the XML data");
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
                if (in != null) {
                    in.close();
                }
            } catch (Exception e) {
                log.error("Exception while closing file", e);
            }
        }
    }

    /**
     * Gets an instance of a named group either from the pool
     * or by calling the Factory Service if the pool is empty.
     *
     * @param groupName the name of the group.
     * @return a Group instance.
     * @throws IntakeException if recycling fails.
     */
    public Group getGroup(String groupName) throws IntakeException {
        Group group = null;

        AppData appData = (AppData) groupNames.get(groupName);

        if (groupName == null) {
            throw new IntakeException("Intake IntakeServiceImpl.getGroup(groupName) is null");
        }
        if (appData == null) {
            throw new IntakeException(
                "Intake IntakeServiceImpl.getGroup(groupName): No XML definition for Group "
                    + groupName
                    + " found");
        }
        try {
            group =
                (Group)
                    ((KeyedObjectPool) keyedPools.get(appData)).borrowObject(
                    groupName);
        } catch (Exception e) {
            throw new IntakeException("Could not get group " + groupName, e);
        }
        return group;
    }

    /**
     * Puts a Group back to the pool.
     *
     * @param instance the object instance to recycle.
     *
     * @throws IntakeException The passed group name does not exist.
     */
    public void releaseGroup(Group instance) throws IntakeException {
        if (instance != null) {
            String groupName = instance.getIntakeGroupName();
            AppData appData = (AppData) groupNames.get(groupName);

            if (appData == null) {
                throw new IntakeException(
                    "Intake IntakeServiceImpl.releaseGroup(groupName): "
                        + "No XML definition for Group "
                        + groupName
                        + " found");
            }

            try {
                ((KeyedObjectPool) keyedPools.get(appData)).returnObject(
                    groupName,
                    instance);
            } catch (Exception e) {
                new IntakeException("Could not get group " + groupName, e);
            }
        }
    }

    /**
     * Gets the current size of the pool for a group.
     *
     * @param groupName the name of the group.
     *
     * @throws IntakeException The passed group name does not exist.
     */
    public int getSize(String groupName) throws IntakeException {
        AppData appData = (AppData) groupNames.get(groupName);
        if (appData == null) {
            throw new IntakeException(
                "Intake IntakeServiceImpl.Size(groupName): No XML definition for Group "
                    + groupName
                    + " found");
        }

        KeyedObjectPool kop = (KeyedObjectPool) keyedPools.get(groupName);

        return kop.getNumActive(groupName) + kop.getNumIdle(groupName);
    }

    /**
     * Names of all the defined groups.
     *
     * @return array of names.
     */
    public String[] getGroupNames() {
        return (String[]) groupNames.keySet().toArray(new String[0]);
    }

    /**
     * Gets the key (usually a short identifier) for a group.
     *
     * @param groupName the name of the group.
     * @return the the key.
     */
    public String getGroupKey(String groupName) {
        return (String) groupKeyMap.get(groupName);
    }

    /**
     * Gets the group name given its key.
     *
     * @param groupKey the key.
     * @return groupName the name of the group.
     */
    public String getGroupName(String groupKey) {
        return (String) groupNameMap.get(groupKey);
    }

    /**
     * Gets the Method that can be used to set a property.
     *
     * @param className the name of the object.
     * @param propName the name of the property.
     * @return the setter.
     * @throws ClassNotFoundException
     * @throws IntrospectionException
     */
    public Method getFieldSetter(String className, String propName)
        throws ClassNotFoundException, IntrospectionException {
        Map settersForClassName = (Map) setterMap.get(className);

        if (settersForClassName == null) {
            throw new IntrospectionException(
                "No setter Map for " + className + " available!");
        }

        Method setter = (Method) settersForClassName.get(propName);

        if (setter == null) {
            PropertyDescriptor pd =
                new PropertyDescriptor(propName, Class.forName(className));
            synchronized (setterMap) {
                setter = pd.getWriteMethod();
                settersForClassName.put(propName, setter);
                if (setter == null) {
                    log.error(
                        "Intake: setter for '"
                            + propName
                            + "' in class '"
                            + className
                            + "' could not be found.");
                }
            }
            // we have already completed the reflection on the getter, so
            // save it so we do not have to repeat
            synchronized (getterMap) {
                Map gettersForClassName = (Map) getterMap.get(className);

                if (gettersForClassName != null) {
                    try {
                        Method getter = pd.getReadMethod();
                        if (getter != null) {
                            gettersForClassName.put(propName, getter);
                        }
                    } catch (Exception e) {
                        // Do nothing
                    }
                }
            }
        }
        return setter;
    }

    /**
     * Gets the Method that can be used to get a property value.
     *
     * @param className the name of the object.
     * @param propName the name of the property.
     * @return the getter.
     * @throws ClassNotFoundException
     * @throws IntrospectionException
     */
    public Method getFieldGetter(String className, String propName)
        throws ClassNotFoundException, IntrospectionException {
        Map gettersForClassName = (Map) getterMap.get(className);

        if (gettersForClassName == null) {
            throw new IntrospectionException(
                "No getter Map for " + className + " available!");
        }

        Method getter = (Method) gettersForClassName.get(propName);

        if (getter == null) {
            PropertyDescriptor pd = null;
            synchronized (getterMap) {
                pd = new PropertyDescriptor(propName, Class.forName(className));
                getter = pd.getReadMethod();
                gettersForClassName.put(propName, getter);
                if (getter == null) {
                    log.error(
                        "Intake: getter for '"
                            + propName
                            + "' in class '"
                            + className
                            + "' could not be found.");
                }
            }
            // we have already completed the reflection on the setter, so
            // save it so we do not have to repeat
            synchronized (setterMap) {
                Map settersForClassName = (Map) getterMap.get(className);

                if (settersForClassName != null) {
                    try {
                        Method setter = pd.getWriteMethod();
                        if (setter != null) {
                            settersForClassName.put(propName, setter);
                        }
                    } catch (Exception e) {
                        // Do nothing
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
    public void configure(Configuration conf) throws ConfigurationException {

        Vector defaultXmlPathes = new Vector();
        defaultXmlPathes.add(XML_PATH_DEFAULT);

        final Configuration xmlPaths = conf.getChild(XML_PATHS, false);
        List xmlPathes = new ArrayList();
        if (xmlPaths == null) {
            xmlPathes.add(XML_PATH_DEFAULT);
        } else {
            Configuration[] nameVal = xmlPaths.getChildren();
            for (int i = 0; i < nameVal.length; i++) {
                String key = nameVal[i].getName();
                String val = nameVal[i].getValue();
                // getLogger.debug("Registered " + val 
                //            + " for Crypto Algorithm " + key);
                xmlPathes.add(val);
            }
        }

        Map appDataElements = null;

        String serialDataPath =
            conf.getChild(SERIAL_XML, false).getValue(SERIAL_XML_DEFAULT);

        if (!serialDataPath.equalsIgnoreCase("none")) {
            serialDataPath =
                new File(applicationRoot, serialDataPath).getAbsolutePath();
        } else {
            serialDataPath = null;
        }

        log.debug("Path for serializing: " + serialDataPath);

        groupNames = new HashMap();
        groupKeyMap = new HashMap();
        groupNameMap = new HashMap();
        getterMap = new HashMap();
        setterMap = new HashMap();
        keyedPools = new HashMap();

        if (xmlPathes == null) {
            String LOAD_ERROR =
                "No pathes for XML files were specified. "
                    + "Check that the property exists in "
                    + "TurbineResources.props and were loaded.";

            log.error(LOAD_ERROR);
            throw new ConfigurationException(LOAD_ERROR);
        }

        Set xmlFiles = new HashSet();

        long timeStamp = 0;

        for (Iterator it = xmlPathes.iterator(); it.hasNext();) {
            // Files are webapp.root relative
            String xmlPath = (String) it.next();
            File xmlFile = new File( applicationRoot, xmlPath);

            log.debug("Path for XML File: " + xmlFile);

            if (!xmlFile.canRead()) {
                String READ_ERR = "Could not read input file " + xmlPath;

                log.error(READ_ERR);
                throw new ConfigurationException(READ_ERR);
            }

            xmlFiles.add(xmlFile.toString());

            log.debug("Added " + xmlPath + " as File to parse");

            // Get the timestamp of the youngest file to be compared with
            // a serialized file. If it is younger than the serialized file,
            // then we have to parse the XML anyway.
            timeStamp =
                (xmlFile.lastModified() > timeStamp)
                    ? xmlFile.lastModified()
                    : timeStamp;
        }

        Map serializedMap = loadSerialized(serialDataPath, timeStamp);

        if (serializedMap != null) {
            // Use the serialized data as XML groups. Don't parse.
            appDataElements = serializedMap;
            log.debug("Using the serialized map");
        } else {
            // Parse all the given XML files
            appDataElements = new HashMap();

            for (Iterator it = xmlFiles.iterator(); it.hasNext();) {
                String xmlPath = (String) it.next();
                AppData appData = null;

                log.debug("Now parsing: " + xmlPath);
                try {
                    XmlToAppData xmlApp = new XmlToAppData();
                    appData = xmlApp.parseFile(xmlPath);
                } catch (Exception e) {
                    log.error("Could not parse XML file " + xmlPath, e);

                    throw new ConfigurationException(
                        "Could not parse XML file " + xmlPath,
                        e);
                }

                appDataElements.put(appData, xmlPath);
                log.debug("Saving appData for " + xmlPath);
            }

            saveSerialized(serialDataPath, appDataElements);
        }

        try {
            for (Iterator it = appDataElements.keySet().iterator();
                it.hasNext();
                ) {
                AppData appData = (AppData) it.next();

                int maxPooledGroups = 0;
                List glist = appData.getGroups();

                String groupPrefix = appData.getGroupPrefix();

                for (int i = glist.size() - 1; i >= 0; i--) {
                    XmlGroup g = (XmlGroup) glist.get(i);
                    String groupName = g.getName();

                    boolean registerUnqualified =
                        registerGroup(groupName, g, appData, true);

                    if (!registerUnqualified) {
                        log.info(
                            "Ignored redefinition of Group "
                                + groupName
                                + " or Key "
                                + g.getKey()
                                + " from "
                                + appDataElements.get(appData));
                    }

                    if (groupPrefix != null) {
                        StringBuffer qualifiedName = new StringBuffer();
                        qualifiedName.append(groupPrefix).append(':').append(
                            groupName);

                        // Add the fully qualified group name. Do _not_ check for
                        // the existence of the key if the unqualified registration succeeded
                        // (because then it was added by the registerGroup above).
                        if (!registerGroup(qualifiedName.toString(),
                            g,
                            appData,
                            !registerUnqualified)) {
                            log.error(
                                "Could not register fully qualified name "
                                    + qualifiedName
                                    + ", maybe two XML files have the same prefix. Ignoring it.");
                        }
                    }

                    maxPooledGroups =
                        Math.max(
                            maxPooledGroups,
                            Integer.parseInt(g.getPoolCapacity()));

                }

                KeyedPoolableObjectFactory factory =
                    new Group.GroupFactory(appData);
                keyedPools.put(
                    appData,
                    new StackKeyedObjectPool(factory, maxPooledGroups));
            }

        } catch (Exception e) {
            throw new ConfigurationException(
                "IntakeServiceImpl failed to initialize",
                e);
        }
    }
    /**
     * Avalon component lifecycle method
     * Initializes the service by loading default class loaders
     * and customized object factories.
     *
     * @throws InitializationException if initialization fails.
     */
    public void initialize() throws Exception {
        Intake.setIntakeService(this);
        if (log.isInfoEnabled())
        {
            log.info("Intake Service is Initialized now..");
        }
    }

    public void contextualize(Context context) throws ContextException {
          this.context = context;
          this.applicationRoot = context.get( "urn:avalon:home" ).toString();
    }

    /**
    * Avalon component lifecycle method
      * @avalon.dependency type="org.apache.fulcrum.localization.LocalizationService"
    */
    public void service( ServiceManager manager) throws ServiceException {
        this.manager = manager;
        
        IntakeServiceFacade.setIntakeService(this);
        LocalizationService localizationService = 
              (LocalizationService)manager.lookup(LocalizationService.ROLE);
        IntakeServiceFacade.setLocalizationService(localizationService);
    }

    /**
     * Avalon component lifecycle method
     */
    public void dispose() {

        manager = null;
    }
}
