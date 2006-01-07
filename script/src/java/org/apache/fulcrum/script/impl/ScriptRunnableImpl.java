package org.apache.fulcrum.script.impl;

/*
 * Copyright 2005 Apache Software Foundation
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

import java.util.Map;

import javax.script.SimpleNamespace;

import org.apache.fulcrum.script.ScriptRunnable;
import org.apache.fulcrum.script.ScriptService;

/**
 * A Runnable executing scripts
 *
 * @author <a href="mailto:siegfried.goeschl@it20one.at">Siegfried Goeschl</a>
 */
public class ScriptRunnableImpl implements ScriptRunnable
{
    /** the name of the script to execute */
    private String scriptName;

    /** the service to execute the scripts */
    private ScriptService scriptService;

    /** the arguments for executing the script */
    private SimpleNamespace args;

    /** the result of the execution */
    private Object result;

    /** the execption thrown by the scipt if any */
    private Exception exception;

    /**
     * Constructor
     *
     * @param scriptService the service to execute the script 
     * @param scriptName the name of the script to execute
     * @param args the arguments passed to the script
     */
    public ScriptRunnableImpl(
        ScriptService scriptService,
        String scriptName,
        Map args)
    {
        Validate.notEmpty( scriptName, "scriptName" );
        Validate.notNull( scriptService, "scriptService" );

        this.scriptName = scriptName;
        this.scriptService = scriptService;
        this.args = new SimpleNamespace(args);
    }

    /**
     * @see java.lang.Runnable#run()
     */
    public void run()
    {
        try
        {
            this.result = null;
            this.exception = null;
            
            this.result = this.scriptService.eval(
                this.getScriptName(),
                this.args
                );
        }
        catch (Exception e)
        {
            this.exception = e;
        }
        catch (Throwable t)
        {
            this.exception = new Exception(t.getMessage());
        }
    }

    /**
     * @return Returns the result.
     */
    public Object getResult() throws Exception
    {
        if( this.exception != null )
        {
            throw this.exception;
        }
        else
        {
            return result;
        }
    }

    /**
     * @param args The args to set.
     */
    public void setArgs(Map args)
    {
        Validate.notNull( args, "args" );
        this.args = new SimpleNamespace(args);
    }
    
    /**
     * @return Returns the scriptName.
     */
    private String getScriptName()
    {
        return scriptName;
    }
}
