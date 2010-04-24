package org.apache.fulcrum.script.impl;

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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import javax.script.Compilable;
import javax.script.CompiledScript;
import javax.script.Invocable;
import javax.script.Bindings;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import org.apache.avalon.framework.activity.Disposable;
import org.apache.avalon.framework.activity.Initializable;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.configuration.Reconfigurable;
import org.apache.avalon.framework.context.Contextualizable;
import org.apache.avalon.framework.service.ServiceException;
import org.apache.avalon.framework.service.ServiceManager;
import org.apache.avalon.framework.service.Serviceable;
import org.apache.avalon.framework.thread.ThreadSafe;
import org.apache.fulcrum.resourcemanager.ResourceManagerService;
import org.apache.fulcrum.script.ScriptAvalonContext;
import org.apache.fulcrum.script.ScriptService;

/**
 * A Fulcrum service to use the JSR-233.
 *
 * @author <a href="mailto:siegfried.goeschl@it20one.at">Siegfried Goeschl</a>
 */
public class ScriptServiceImpl
    extends ScriptBaseService
    implements ScriptService, Contextualizable, Initializable, Disposable, Serviceable, Reconfigurable, ThreadSafe
{
    /** the name of the default scripting engine*/
    private String defaultEngineName;

    /** the script manager */
    private ScriptEngineManager scriptEngineManager;

    /** the mapof scripting engines to use */
    private HashMap scriptEngineMap;

    /** maps from a script name to a script */
    private ScriptCache scriptCache;

    /** a list of excluded services */
    private Set excludedServices;

    /** common script configuration */
    private Configuration scriptConfiguration;

    /////////////////////////////////////////////////////////////////////////
    // Avalon Service Lifecycle Implementation
    /////////////////////////////////////////////////////////////////////////

    /**
     * Constructor
     */
    public ScriptServiceImpl()
    {
        super();
        this.scriptCache = new ScriptCache();
		this.scriptEngineMap = new HashMap();
		this.scriptEngineManager = new ScriptEngineManager();
		this.excludedServices = new HashSet();
    }

    /**
     * @see org.apache.avalon.framework.service.Serviceable#service(org.apache.avalon.framework.service.ServiceManager)
     */
    public void service(ServiceManager serviceManager) throws ServiceException
    {
        super.service( serviceManager );
        this.getServiceManager().lookup( ResourceManagerService.class.getName() );
    }

    /**
     * @see org.apache.avalon.framework.configuration.Configurable#configure(org.apache.avalon.framework.configuration.Configuration)
     */
    public void configure(Configuration configuration) throws ConfigurationException
    {
        super.configure( configuration );

        // extract the common script configuration

        this.scriptConfiguration = configuration.getChild("scriptConfiguration");

        // create all scripting engines

        Configuration[] scriptEngineConfigurationList = configuration.getChild("scriptEngines").getChildren("scriptEngine");

        for (int i=0; i<scriptEngineConfigurationList.length; i++)
        {
            ScriptEngineEntry scriptEngineEntry = this.createScriptEngineEntry(scriptEngineConfigurationList[i]);
            this.getScriptEngineMap().put(scriptEngineEntry.getName(), scriptEngineEntry);
            this.getLogger().debug("Added ScriptEngine for " + scriptEngineEntry.getName());

            if( this.defaultEngineName == null)
            {
                this.defaultEngineName = scriptEngineEntry.getName();
            }
        }

        // verify that at least one scripting engine is available

        if( this.getScriptEngineMap().size() == 0 )
        {
            String msg = "At least one scripting engine must be defined";
            this.getLogger().error(msg);
            throw new ConfigurationException(msg);
        }

        // evaluate the scripts defined in the onLoad section

        this.preloadScripts();
    }

    /**
     * @see org.apache.avalon.framework.activity.Initializable#initialize()
     */
    public void initialize() throws Exception
    {
        ScriptAvalonContext avalonContext = this.createScriptAvalonContext();
        this.scriptEngineManager.put("avalonContext", avalonContext);
    }

    /**
     * @see org.apache.avalon.framework.configuration.Reconfigurable#reconfigure(org.apache.avalon.framework.configuration.Configuration)
     */
    public void reconfigure(Configuration cfg) throws ConfigurationException
    {
        super.reconfigure(cfg);
        this.defaultEngineName = null;
        this.scriptConfiguration = null;
        this.getScriptEngineMap().clear();
        this.getScriptCache().clear();
        this.configure(cfg);
    }

    /**
     * @see org.apache.avalon.framework.activity.Disposable#dispose()
     */
    public void dispose()
    {
        super.dispose();
        this.scriptCache.clear();
        this.scriptCache = null;
        this.excludedServices = null;
    }

    /////////////////////////////////////////////////////////////////////////
    // Service interface implementation
    /////////////////////////////////////////////////////////////////////////

    /**
     * @see org.apache.fulcrum.script.ScriptService#getScriptEngineManager()
     */
    public ScriptEngineManager getScriptEngineManager()
    {
        return this.scriptEngineManager;
    }

    /**
     * @see org.apache.fulcrum.script.ScriptService#getScriptEngine()
     */
    public ScriptEngine getScriptEngine()
    {
        return this.getScriptEngine(this.getDefaultEngineName());
    }

    /**
     * @see org.apache.fulcrum.script.ScriptService#eval(java.lang.String, javax.script.Bindings)
     */
    public Object eval(String scriptName, Bindings binding)
        throws IOException, ScriptException
    {
        Validate.notEmpty(scriptName, "scriptName");
        Validate.notNull(binding, "binding");
        String currScriptName = this.makeScriptName(scriptName);
        ScriptCacheEntry scriptCacheEntry = this.createScriptCacheEntry(currScriptName);
        Object result = this.doEval( scriptCacheEntry, binding, null );
        return result;

    }

    /* (non-Javadoc)
     * @see org.apache.fulcrum.script.ScriptService#eval(java.lang.String, javax.script.ScriptContext)
     */
    public Object eval(String scriptName, ScriptContext context)
        throws IOException, ScriptException
    {
        Validate.notEmpty(scriptName, "scriptName");
        Validate.notNull(context, "context");
        String currScriptName = this.makeScriptName(scriptName);
        ScriptCacheEntry scriptCacheEntry = this.createScriptCacheEntry(currScriptName);
        ScriptAvalonContext avalonContext = this.createScriptAvalonContext();
        context.setAttribute("avalonContext", avalonContext, ScriptContext.ENGINE_SCOPE);
        Object result = this.doEval( scriptCacheEntry, null, context );
        return result;
    }

    /**
     * @see org.apache.fulcrum.script.ScriptService#eval(java.lang.String)
     */
    public Object eval(String scriptName) throws IOException, ScriptException
    {
        Validate.notEmpty(scriptName, "scriptName");
        String currScriptName = this.makeScriptName(scriptName);
        ScriptCacheEntry scriptCacheEntry = this.createScriptCacheEntry(currScriptName);
        Object result = this.doEval( scriptCacheEntry, null, null );
        return result;
    }

    /**
     * @see org.apache.fulcrum.script.ScriptService#call(java.lang.String, java.lang.Object[])
     */
    public Object call(String name, Object [] args) throws ScriptException,
        NoSuchMethodException
    {
        Validate.notEmpty(name, "name");
        return this.call(this.getDefaultEngineName(), name, args);

    }

    /**
     * @see org.apache.fulcrum.script.ScriptService#getInterface(java.lang.Class)
     */
    public Object getInterface(Class clazz)
    {
        return this.getInterface(this.getDefaultEngineName(), clazz);
    }

    /**
     * @see org.apache.fulcrum.script.ScriptService#exists(java.lang.String)
     */
    public boolean exists(String name)
    {
        Validate.notEmpty(name, "name");

        String scriptName = this.makeScriptName(name);
        String engineName = this.getEngineName(scriptName);
        ScriptEngineEntry scriptEngineEntry = this.getScriptEngineEntry(engineName);
        String location = scriptEngineEntry.getLocation();

        if( this.getResourceManagerService().exists(location) &&
            this.getResourceManagerService().exists(location, scriptName) )
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    /////////////////////////////////////////////////////////////////////////
    // Service Implementation
    /////////////////////////////////////////////////////////////////////////

    private ScriptEngine getScriptEngine(String engineName)
    {
        ScriptEngineEntry scriptEngineEntry = this.getScriptEngineEntry(engineName);
        return scriptEngineEntry.getScriptEngine();
    }

    /**
     * Invokes a global script method.
     *
     * @param engineName the engine name
     * @param name the script method to invoke
     * @param args the arguments for the script method
     * @return a scripting object implementing the given interface
     * @throws ScriptException script execution failed
     * @throws NoSuchMethodException the script method was nout found
     */
    private Object call(String engineName, String name, Object[] args)
    	throws ScriptException, NoSuchMethodException
    {
        Validate.notEmpty(engineName, "engineName");

        Object result = this.doCall(engineName, name, args);
        return result;
    }

    /**
     * @param engineName the engine name
     * @param clazz the inteface to implement
     * @return a scripting object implementing the given interface
     */
    private Object getInterface(String engineName, Class clazz)
    {
        Validate.notEmpty(engineName, "engineName");
        Validate.notNull(clazz, "clazz");

        Object result = this.doGetInterface(engineName, clazz);
        return result;
    }

    /**
     * @return an instance of the ResourceService
     */
    private ResourceManagerService getResourceManagerService()
    {
        try
        {
            return (ResourceManagerService) this.getServiceManager().lookup(
                ResourceManagerService.class.getName()
                );
        }
        catch (ServiceException e)
        {
            throw new RuntimeException( e.getMessage() );
        }
    }

    /**
     * Load a script from the persistent storage.
     *
     * @param engineName the script engine for executing the script
     * @param scriptName the name of the script
     * @return the plain content of the script
     * @throws IOException loading the script failed
     */
    private String loadScript( String engineName, String scriptName )
        throws IOException
    {
        ScriptEngineEntry scriptEngineEntry = this.getScriptEngineEntry(engineName);
        String location = scriptEngineEntry.getLocation();

        String result = null;
        byte[] scriptContent = null;
        String[] scriptContext = {};

        scriptContent = this.getResourceManagerService().read(
            location,
            scriptContext,
            scriptName
            );

        result = new String( scriptContent );
        return result;
    }

    /**
     * Creates a compiled script.
     *
     * @param engineName the script engine to use
     * @param scriptName the name of the scipt to compile
     * @param scriptContent the name  scipt to compile
     * @return the compiled script
     * @throws ScriptException failed to compile the script
     */
    private CompiledScript compileScript( String engineName, String scriptName, String scriptContent )
        throws ScriptException
    {
        CompiledScript result = null;

        try
        {
            ScriptEngineEntry scriptEngineEntry = this.getScriptEngineEntry(engineName);
            Compilable compilable = (Compilable) scriptEngineEntry.getScriptEngine();
            long startTime = System.currentTimeMillis();
            result = compilable.compile(scriptContent);
            long endTime = System.currentTimeMillis();

            if( this.getLogger().isDebugEnabled() )
            {
	            this.getLogger().debug(
	                "Compiling of " + scriptName + " took "
	                + (endTime - startTime) + " ms"
	                );
            }

            return result;
        }
        catch (ScriptException e)
        {
            String msg = "Compiling the script failed : " + scriptName;
            this.getLogger().error( msg, e );
            throw new RuntimeException( msg ,e );
        }
    }

    /**
     * Evaluates a script.
     *
     * @param scriptCacheEntry the script to run
     * @param binding the parameters to pass to the script
     * @param scriptContext the script context
     * @return the result of the executed script
     * @throws ScriptException executing the script failed
     */
    private Object doEval( ScriptCacheEntry scriptCacheEntry, Bindings binding, ScriptContext scriptContext )
        throws ScriptException
    {
        Object result = null;
        String scriptName = scriptCacheEntry.getScriptName();
        ScriptEngineEntry scriptEngineEntry = this.getScriptEngineEntry(scriptCacheEntry.getEngineName());
        ScriptEngine scriptEngine = scriptEngineEntry.getScriptEngine();

        try
        {
            long startTime = System.currentTimeMillis();

            if( scriptCacheEntry.isCompiled() )
            {
                if( binding != null )
                {
                    result = scriptCacheEntry.getCompiledScript().eval(binding);
                }
                else if( scriptContext != null )
                {
                    result = scriptCacheEntry.getCompiledScript().eval(scriptContext);
                }
                else
                {
                    result = scriptCacheEntry.getCompiledScript().eval();
                }
            }
            else
            {
                if( binding != null )
                {
                    result = scriptEngine.eval(scriptCacheEntry.getPlainScript(), binding);
                }
                else if( scriptContext != null )
                {
                    result = scriptEngine.eval(scriptCacheEntry.getPlainScript(), scriptContext);
                }
                else
                {
                    result = scriptEngine.eval(scriptCacheEntry.getPlainScript());
                }
            }

            long endTime = System.currentTimeMillis();

            if( this.getLogger().isDebugEnabled() )
            {
	            this.getLogger().debug(
	                "Execution of " + scriptName + " took "
	                + (endTime - startTime) + " ms"
	                );
            }

            return result;
        }
        catch( ScriptException e )
        {
            String msg= "Execution of the script failed : " + scriptName;
            this.getLogger().error( msg, e );
            throw e;
        }
    }

    /**
     * Invokes a global script method.
     *
     * @param engineName the name of the script engine to use
     * @param name the name of the script method
     * @param args the script method parameters
     * @return the result of the script invocation
     * @throws ScriptException if an error occurrs during invocation of the method.
     * @throws NoSuchMethodException if method with given name or matching argument types cannot be found.
     */
    private Object doCall( String engineName, String name, Object[] args )
    	throws ScriptException, NoSuchMethodException
    {
        Object result = null;
        ScriptEngineEntry scriptEngineEntry = this.getScriptEngineEntry(engineName);
        ScriptEngine scriptEngine = scriptEngineEntry.getScriptEngine();
        Invocable invocable = (Invocable) scriptEngine;

        try
        {
            long startTime = System.currentTimeMillis();
            result = invocable.invokeFunction(name, args);
            long endTime = System.currentTimeMillis();

            if( this.getLogger().isDebugEnabled() )
            {
	            this.getLogger().debug(
	                "Calling the method " + name + " took "
	                + (endTime - startTime) + " ms"
	                );
            }

            return result;
        }
        catch( ScriptException e )
        {
            String msg= "Calling the following method failed : " + name;
            this.getLogger().error( msg, e );
            throw e;
        }
        catch( NoSuchMethodException e )
        {
            String msg= "Calling the following method failed : " + name;
            this.getLogger().error( msg, e );
            throw e;
        }
    }

    /**
     * Creates a script object implementing the requested interface.
     *
     * @param engineName the name of the scripting engine
     * @param clazz the requested interface
     * @return the result of the executed script
     */
    private Object doGetInterface( String engineName, Class clazz )
    {
        Object result = null;
        ScriptEngineEntry scriptEngineEntry = this.getScriptEngineEntry(engineName);
        ScriptEngine scriptEngine = scriptEngineEntry.getScriptEngine();
        Invocable invocable = (Invocable) scriptEngine;

        try
        {
            long startTime = System.currentTimeMillis();
            result = invocable.getInterface(clazz);
            long endTime = System.currentTimeMillis();

            if( this.getLogger().isDebugEnabled() )
            {
	            this.getLogger().debug(
	                "Creating the interface for " + clazz.getName() + " took "
	                + (endTime - startTime) + " ms"
	                );
            }

            return result;
        }
        catch( RuntimeException e )
        {
            String msg= "Creation of the interface failed : " + clazz.getName();
            this.getLogger().error( msg, e );
            throw e;
        }
    }

    /**
     * Create the Avalon specific context.
     *
     * @return the Avalon context for the script
     */
    private ScriptAvalonContext createScriptAvalonContext()
    {
        ScriptAvalonContextImpl result = null;

        ScriptServiceManagerImpl scriptServiceManager = new ScriptServiceManagerImpl(
            this.getServiceManager(),
            this.excludedServices
            );

        result = new ScriptAvalonContextImpl(
            this.getLogger().getChildLogger("script"),
            scriptServiceManager,
            this.getContext(),
            this.scriptConfiguration,
            this.getParameters()
            );

        return result;
    }

    /**
     * @return the name of the default engine
     */
    private String getDefaultEngineName()
    {
        return this.defaultEngineName;
    }

    /**
     * @return Returns the scriptCache.
     */
    private ScriptCache getScriptCache()
    {
        return scriptCache;
    }

    /**
     * Creates a script to be evaluates.
     *
     * @param scriptName the name of the script to execute
     * @return ScriptCacheEntry
     * @throws IOException the script could not be loaded
     * @throws ScriptException the script could not be parsed by the script engine
     */
    private synchronized ScriptCacheEntry createScriptCacheEntry(String scriptName)
    	throws IOException, ScriptException
    {
        String engineName = this.getEngineName(scriptName);
        ScriptCacheEntry scriptCacheEntry = null;
        ScriptEngineEntry scriptEngineEntry = this.getScriptEngineEntry(engineName);
        String plainScript = null;
        CompiledScript compiledScript = null;
        boolean isInCache = false;

        // try to load the script from the cache or from the resource manager

        if(scriptEngineEntry.isCached())
        {
            scriptCacheEntry = this.getScriptCache().get(scriptName);

            if( scriptCacheEntry == null )
            {
                plainScript = this.loadScript(engineName, scriptName);
            }
            else
            {
                plainScript = scriptCacheEntry.getPlainScript();
                isInCache = true;
            }
        }
        else
        {
            plainScript = this.loadScript(engineName, scriptName);
        }

        if(!isInCache)
        {
	        // compile the script if required

	        if( scriptEngineEntry.isCompiled())
	        {
	            compiledScript = this.compileScript(engineName, scriptName, plainScript);
	        }

            scriptCacheEntry = new ScriptCacheEntry(
	            engineName,
	            scriptName,
	            plainScript,
	            compiledScript
	            );

	        // update the cache

	        if( scriptEngineEntry.isCached() )
	        {
	            this.getScriptCache().put(scriptCacheEntry);
	        }
        }

        return scriptCacheEntry;
    }

    /**
     * Create a ScriptEngineEntry by parsing the configuration.
     *
     * @param configuration the configuration
     * @return ScriptEngineEntry
     * @throws ConfigurationException parsing the configuration failed
     */
    private ScriptEngineEntry createScriptEngineEntry(Configuration configuration)
    	throws ConfigurationException
    {
        String name = configuration.getChild("name").getValue();
        String extension = configuration.getChild("extension").getValue(name);
        boolean isCached = configuration.getChild("isCached").getValueAsBoolean(true);
        boolean isCompiled = configuration.getChild("isCompiled").getValueAsBoolean(true);
        String location = configuration.getChild("location").getValue(name);
        ScriptEngine scriptEngine = this.getScriptEngineManager().getEngineByName(name);

        if(!(scriptEngine instanceof Compilable))
        {
            isCompiled = false;
        }

        ScriptEngineEntry result = new ScriptEngineEntry(name, extension, isCached, isCompiled, location, scriptEngine);

        // evaluate the given scripts

        ArrayList scriptList = new ArrayList();
        Configuration[] scriptListConfiguration = configuration.getChild("preLoad").getChildren("script");
        for( int i=0; i<scriptListConfiguration.length; i++ )
        {
            scriptList.add(scriptListConfiguration[i].getValue());
        }

        result.setScriptList(scriptList);

        return result;
    }

    /**
     * @param engineName the name of the scriptng engine
     * @return the ScriptEngineEntry
     */
    private ScriptEngineEntry getScriptEngineEntry( String engineName)
    {
        ScriptEngineEntry result = (ScriptEngineEntry) this.getScriptEngineMap().get(engineName);

        if( result == null )
        {
            String msg = "Unable to handle scripts for the following script engine : " + engineName;
            this.getLogger().error(msg);
            throw new IllegalArgumentException(msg);
        }

        return result;
    }

    /**
     * Determines the name of the scripting engine by looking at the extension
     *
     * @param scriptName the script name
     * @return name of the scripting engine
     */
    private String getEngineName( String scriptName )
    {
        int pos = scriptName.lastIndexOf('.');

        if( pos > 0 )
        {
            String extension = scriptName.substring(pos+1, scriptName.length());
            return this.getEngineNameForExtension(extension);
        }
        else
        {
            return this.getDefaultEngineName();
        }
    }

    /**
     * Determines the scripting engine based on the extension
     *
     * @param extension the script name extension
     * @return name of the scripting engine
     */
    private String getEngineNameForExtension( String extension )
    {
        Iterator iter = this.scriptEngineMap.keySet().iterator();

        while( iter.hasNext() )
        {
            String engineName = (String) iter.next();
            ScriptEngineEntry scriptEngineEntry = this.getScriptEngineEntry(engineName);
            if(scriptEngineEntry.getExtension().equalsIgnoreCase(extension))
            {
                return engineName;
            }
        }

        throw new IllegalArgumentException("Unknow script extension : " + extension);
    }

    /**
     * @return Returns the scriptEngineMap.
     */
    private HashMap getScriptEngineMap()
    {
        return scriptEngineMap;
    }

    /**
     * Ensure that the name has a proper extension.
     *
     * @param name the name of the script
     * @return script name
     */
    private String makeScriptName( String name )
    {
        String engineName = this.getEngineName(name);
        ScriptEngineEntry scriptEngineEntry = this.getScriptEngineEntry(engineName);
        String extension = '.' + scriptEngineEntry.getExtension();

        if( name.endsWith(extension) )
        {
            return name;
        }
        else
        {
            return name + extension;
        }
    }

    /**
     * Preload the scripts defined in the onLoad section
     */
    private void preloadScripts()
    {
        Iterator iter = this.getScriptEngineMap().keySet().iterator();

        while( iter.hasNext() )
        {
            String engineName = (String) iter.next();
            ScriptEngineEntry scriptEngineEntry = this.getScriptEngineEntry(engineName);
            ScriptEngine scriptEngine = scriptEngineEntry.getScriptEngine();
            Object[] scriptList = scriptEngineEntry.getScriptList().toArray();

            for( int i=0; i<scriptList.length; i++ )
            {
                String scriptName = scriptList[i].toString();

                try
                {
                    String plainScript = this.loadScript(engineName, scriptName);
                    scriptEngine.eval(plainScript);
                }
                catch (Exception e)
                {
                    String msg = "Unable to load the following script : " + scriptName;
                    this.getLogger().error(msg, e);
                    throw new RuntimeException(msg);
                }
            }
        }
    }
}
