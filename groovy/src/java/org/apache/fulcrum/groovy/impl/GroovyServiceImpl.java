package org.apache.fulcrum.groovy.impl;

/*
 * Copyright 2004 Apache Software Foundation
 * Licensed  under the  Apache License,  Version 2.0  (the "License");
 * you may not use  this file  except in  compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed  under the  License is distributed on an "AS IS" BASIS,
 * WITHOUT  WARRANTIES OR CONDITIONS  OF ANY KIND, either  express  or
 * implied.
 *
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import groovy.lang.Binding;
import groovy.lang.GroovyShell;
import groovy.lang.Script;

import java.io.IOException;

import org.apache.avalon.framework.activity.Disposable;
import org.apache.avalon.framework.activity.Initializable;
import org.apache.avalon.framework.configuration.Configurable;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.configuration.Reconfigurable;
import org.apache.avalon.framework.context.Contextualizable;
import org.apache.avalon.framework.service.ServiceException;
import org.apache.avalon.framework.service.ServiceManager;
import org.apache.avalon.framework.service.Serviceable;
import org.apache.commons.lang.Validate;
import org.apache.commons.lang.exception.NestableRuntimeException;
import org.apache.fulcrum.groovy.GroovyRunnable;
import org.apache.fulcrum.groovy.GroovyService;
import org.apache.fulcrum.resourcemanager.ResourceManagerService;
import org.codehaus.groovy.control.CompilationFailedException;

/**
 * Base implementation of the Avalon Groovy Service. Can be subclassed to support
 * script repository using the file system or database.
 *
 * @author <a href="mailto:siegfried.goeschl@it20one.at">Siegfried Goeschl</a>
 */
public class GroovyServiceImpl
    extends GroovyBaseService
    implements GroovyService, Configurable, Initializable,
        Contextualizable, Disposable, Serviceable, Reconfigurable
{
    /** use precompiled and cached Groovy scripts */
    public final String CONFIG_USECACHE = "useCache";

    /** the resource domain to look up Groovy scripts */
    public final String CONFIG_DOMAIN = "domain";

    /** maps from a script name to a script */
    private GroovyScriptCache scriptCache;

    /** use precompiled and cached Groovy scripts */
    private boolean useCache;

    /** maximumline lengthfor dumping arguments */
    private final int MAX_LINE_LENGTH = 1000;

    /** the resource domain to look up Groovy scripts */
    private String domain;

    /////////////////////////////////////////////////////////////////////////
    // Avalon Service Lifecycle Implementation
    /////////////////////////////////////////////////////////////////////////

    /**
     * Constructor
     */
    public GroovyServiceImpl()
    {
        super();

        this.useCache = true;
        this.domain = "groovy";
    }

    /**
     * @see org.apache.avalon.framework.configuration.Configurable#configure(org.apache.avalon.framework.configuration.Configuration)
     */
    public void configure(Configuration cfg) throws ConfigurationException
    {
        super.configure( cfg );

        // do we cache the Groovy scripts to improve performance ?

        this.useCache = cfg.getAttributeAsBoolean(CONFIG_USECACHE);

        // get the domain for the Groovy Scrips

        this.domain = cfg.getAttribute( CONFIG_DOMAIN, "groovy" );
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
     * @see org.apache.avalon.framework.activity.Initializable#initialize()
     */
    public void initialize() throws Exception
    {
        this.scriptCache = new GroovyScriptCache( this.getLogger() );
    }

    /**
     * @see org.apache.avalon.framework.configuration.Reconfigurable#reconfigure(org.apache.avalon.framework.configuration.Configuration)
     */
    public void reconfigure(Configuration cfg) throws ConfigurationException
    {
        super.reconfigure(cfg);
        this.scriptCache.clear();
    }

    /**
     * @see org.apache.avalon.framework.activity.Disposable#dispose()
     */
    public void dispose()
    {
        super.dispose();
        this.scriptCache.clear();
        this.scriptCache = null;
    }


    /////////////////////////////////////////////////////////////////////////
    // Service interface implementation
    /////////////////////////////////////////////////////////////////////////

    /**
     * Executes a Groovy script. If caching is enable we compile
     * the script and store it in the script cache.
     *
     * @param scriptName the name of the script
     * @param args the arguments passed to the script
     * @return the result of the Groovy script
     */
    public Object execute( String scriptName, Object[] args  )
        throws CompilationFailedException, IOException
    {
        Validate.notEmpty( scriptName, "scriptName" );
        Validate.notNull( args, "args" );

        String scriptContent = null;
        Script script = null;
        Object result = null;
        boolean isInCache = false;

        // dump the script signature for debugging

        if( this.getLogger().isDebugEnabled() )
        {
            this.getLogger().debug( this.dumpSignature(scriptName,args) );
        }

        // synchronize here to avoid parsing the scripts multiple
        // times due to race conditions

        synchronized( this.getScriptCache() )
        {
            if( this.isUseCache() == true )
            {
                // cache the script if it is not already in the cache

                if( this.contains(scriptName) == false )
                {
                    scriptContent = this.loadScript(scriptName);

                    this.getScriptCache().put(
                        scriptName,
                        this.compile2script( scriptName, scriptContent )
                        );
                }

                isInCache = true;
            }
            else
            {
                scriptContent = this.loadScript(scriptName);
                script = this.compile2script( scriptName, scriptContent );
            }
        }

        // create the binding

        Binding binding = this.createBinding( scriptName, args );


        if( isInCache )
        {
            // take the script from the cache
            result = this.run( scriptName, binding );
        }
        else
        {
            // use the newly created script
            result = this.run( scriptName, script, binding );
        }

        // dump the result for debugging

        if( this.getLogger().isDebugEnabled() )
        {
            this.getLogger().debug( this.dumpResult(scriptName,result) );
        }
        
        return result;
    }

    /**
     * @see org.apache.fulcrum.groovy.GroovyService#createGroovyRunnable(java.lang.String)
     */
    public GroovyRunnable createGroovyRunnable(String scriptName)
    {
        Validate.notEmpty( scriptName, "scriptName" );

        return new GroovyRunnableImpl(
            scriptName,
            this
            );
    }

    /**
     * @see org.apache.fulcrum.groovy.GroovyService#exists(java.lang.String)
     */
    public boolean exists(String scriptName)
    {
        return this.getResourceManagerService().exists(
            this.getDomain(),
            scriptName 
            );
    }

    /**
     * Compiles a Groovy script.
     *
     * @param scriptName the name of tje scipt to compile
     * @param scriptContent the name  scipt to compile
     * @throws IOException error parsing the script file
     * @throws CompilationFailedException failed to compile the script
     */
    public void compile( String scriptName, String scriptContent )
        throws IOException, CompilationFailedException
    {
        this.compile2script( scriptName, scriptContent );
    }

    /////////////////////////////////////////////////////////////////////////
    // Service Implementation
    /////////////////////////////////////////////////////////////////////////

    /**
     * @return an instance of the ResourceService
     */
    protected ResourceManagerService getResourceManagerService()
    {
        try
        {
            return (ResourceManagerService) this.getServiceManager().lookup(
                ResourceManagerService.class.getName()
                );
        }
        catch (ServiceException e)
        {
            throw new NestableRuntimeException( e );
        }
    }

    /**
     * Is the script already loaded and cached?
     */
    protected boolean contains(String scriptName)
    {
        return ( this.getScriptCache().contains(scriptName) );
    }

    /**
     * Load a script from the persistent storage.
     *
     * @param scriptName the name of the Groovy script
     */
    protected String loadScript( String scriptName )
        throws IOException
    {
        String result = null;
        byte[] scriptContent = null;
        String[] sriptContext = {};

        scriptContent = this.getResourceManagerService().read(
            this.getDomain(),
            sriptContext,
            scriptName
            );

        result = new String( scriptContent );
        return result;
    }

    /**
     * Create the binding for the Groovy script.
     * @param scriptName the name of the Groovy script
     * @param args the arguments
     */
    protected Binding createBinding( String scriptName, Object[] args )
    {
        Binding result = new Binding();

        GroovyAvalonContextImpl groovyAvalonContext = this.createGroovyAvalonContext(
            scriptName
            );

        result.setVariable("args", args );
        result.setVariable("avalonContext", groovyAvalonContext );

        return result;
    }

    /**
     * Create the Avalon specific context
     * @param scriptName the name of the Groovy script
     */
    protected GroovyAvalonContextImpl createGroovyAvalonContext( String scriptName )
    {
        GroovyAvalonContextImpl result = null;

        result = new GroovyAvalonContextImpl(
            this.getLogger().getChildLogger(scriptName),
            this.getServiceManager(),
            this.getContext(),
            this.getConfiguration(),
            this.getParameters()
            );

        return result;
    }

    /**
     * Runs a cached Groovy script.
     * @param scriptName the name of the Groovy script
     * @param binding the binding to be used for the Groovy script
     * @return the result of the executed script
     */
    protected Object run( String scriptName, Binding binding )
    {
        Object result = null;
        Script script = null;

        try
        {
            script = this.getScriptCache().aquire( scriptName );

            Validate.notNull( script, "There must be a cached script" );

            long startTime = System.currentTimeMillis();
            script.setBinding( binding );
            result = script.run();
            long endTime = System.currentTimeMillis();

            this.getLogger().debug(
                "Execution of " + scriptName + " took "
                + (endTime - startTime) + " ms"
                );

            return result;
        }
        catch( Exception e )
        {
            String msg= "Execution of the script failed : " + scriptName;
            this.getLogger().error( msg, e );
            throw new NestableRuntimeException( msg ,e );
        }
        finally
        {
            this.getScriptCache().release( scriptName );
        }
    }

    /**
     * Runs a non-cached Groovy script.
     * @param scriptName the name of the Groovy script
     * @param script the script to execute
     * @param binding the binding to be used for the Groovy script
     * @return the result of the executed script
     */
    protected Object run( String scriptName, Script script, Binding binding )
    {
        Object result = null;

        try
        {
            long startTime = System.currentTimeMillis();
            script.setBinding( binding );
            result = script.run();
            long endTime = System.currentTimeMillis();

            this.getLogger().debug(
                "Execution of " + scriptName + " took "
                + (endTime - startTime) + " ms"
                );

            return result;
        }
        catch( Exception e )
        {
            String msg= "Execution of the script failed : " + scriptName;
            this.getLogger().error( msg, e );
            throw new NestableRuntimeException( msg ,e );
        }
    }

    /**
     * @return Returns the scriptCache.
     */
    public GroovyScriptCache getScriptCache()
    {
        return scriptCache;
    }

    /**
     * Creates a compiled Groovy script.
     *
     * @param scriptName the name of tje scipt to compile
     * @param scriptContent the name  scipt to compile
     * @return the compiled groovy script
     * @throws IOException error accessing the script file
     * @throws CompilationFailedException failed to compile the script
     */
    protected Script compile2script( String scriptName, String scriptContent )
        throws IOException, CompilationFailedException
    {
        try
        {
            long startTime = System.currentTimeMillis();
            GroovyShell shell = new GroovyShell();
            Script result = shell.parse(scriptContent, scriptName );
            long endTime = System.currentTimeMillis();

            this.getLogger().debug(
                "Compiling of " + scriptName + " took "
                + (endTime - startTime) + " ms"
                );

            return result;
        }
        catch (CompilationFailedException e)
        {
            String msg = "Compilation failed : " + scriptName;
            this.getLogger().error( msg, e );
            throw e;
        }
        catch (IOException e)
        {
            String msg = "Parsing the script failed : " + scriptName;
            this.getLogger().error( msg, e );
            throw e;
        }
        catch (Exception e)
        {
            String msg = "Parsing the script failed : " + scriptName;
            this.getLogger().error( msg, e );
            throw new NestableRuntimeException( msg ,e );
        }
    }

    /**
     * Create a signature dump of the script invocation to ease debugging.
     * 
     * @param scriptName the name of the script
     * @param args the parameters passed to the script
     * @return the debug output
     */
    protected String dumpSignature( String scriptName, Object[] args )
    {
        StringBuffer scriptSignature = new StringBuffer();

        scriptSignature.append( "Groovy" );
        scriptSignature.append( "#" );
        scriptSignature.append( scriptName );
        scriptSignature.append( "(" );

        for( int i=0; i<args.length; i++ )
        {
            if( args[i] != null )
            {
                scriptSignature.append( "arg["+i+"]=" );

                scriptSignature.append( args[i].getClass().getName() );

                if( this.getLogger().isDebugEnabled() )
                {
                    scriptSignature.append( "[" );

                    String temp = args[i].toString();

                    if( temp.length() > MAX_LINE_LENGTH )
                    {
                        temp = temp.substring(0,MAX_LINE_LENGTH-1);
                        scriptSignature.append( temp );
                        scriptSignature.append( "..." );
                    }
                    else
                    {
                        scriptSignature.append( temp );
                    }

                    scriptSignature.append( "]" );
                }
            }
            else
            {
                scriptSignature.append( "<null>" );
            }

            if( i<args.length-1 )
            {
                scriptSignature.append( ";" );
            }
        }
        scriptSignature.append( ")" );

        return scriptSignature.toString();
    }

    /**
     * Create a result dump of the script invocation to ease debugging.
     * 
     * @param scriptName the name of the script
     * @param result the result of the script execution
     * @return the debug output
     */

    protected String dumpResult( String scriptName, Object result )
    {
        StringBuffer resultSignature = new StringBuffer();
        
        resultSignature.append( "Groovy" );
        resultSignature.append( "#" );
        resultSignature.append( scriptName );
        resultSignature.append( "(" );
        
        if( result == null )
        {
            resultSignature.append( "<null>" );
        }
        else
        {            
            resultSignature.append( "result=" );

            resultSignature.append( result.getClass().getName() );
            resultSignature.append( "[" );
            
            String temp = result.toString();

            if( temp.length() > MAX_LINE_LENGTH )
            {
                temp = temp.substring(0,MAX_LINE_LENGTH-1);
                resultSignature.append( temp );
                resultSignature.append( "..." );
            }
            else
            {
                resultSignature.append( temp );
            }
        }
        
        resultSignature.append( "]" );
        resultSignature.append( ")" );

        
        return resultSignature.toString();
    }
    
    /////////////////////////////////////////////////////////////////////////
    // Generated getters/setters
    /////////////////////////////////////////////////////////////////////////

    /**
     * @return Returns the useCache.
     */
    protected boolean isUseCache()
    {
        return useCache;
    }

    /**
     * @return Returns the domain.
     */
    protected String getDomain()
    {
        return domain;
    }
}
