package org.apache.fulcrum.yaafi.framework.role;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import org.apache.fulcrum.yaafi.framework.util.ToStringBuilder;
import org.apache.fulcrum.yaafi.framework.util.Validate;

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


/**
 * Interface exposed by the ServiceContainerImpl
 * 
 * @author <a href="mailto:siegfried.goeschl@it20one.at">Siegfried Goeschl</a>
 */

public class RoleEntryImpl implements RoleEntry
{    	
    /** the name of the service component to be used for the service lookup */
    private String name;

    /** the name of the implementation class of the service component */
    private String implementationClazzName;

    /** the short name of the service component to lookup the configuration */
    private String shorthand;
         
    /** do we incarnate the instance of the service component during start-up? */
    private boolean isEarlyInit;
    
    /** a description for the service component if any */
    private String description;
    
    /** the type of service component, e.g. "avalon" */
    private String componentType;
    
    /** the type of service component if any, e.g. "merlin", "phoenix" or "fortress*/
    private String componentFlavour;
    
    /** do we use a dynamic proxy when invoking the service */
    private boolean hasDynamicProxy;
    
    /** the list of interceptors to be invoked when using a dynamic proxy */
    private ArrayList interceptorList;
    
    /** the optional category for creating a logger */
    private String logCategory;
    
    /**
     * YAAFI role entry
     * 
     * @param name the name of the service component to be used for the service lookup
     * @param defaultClass the name of the implementation class of the service component 
     * @param shorthand the short name of the service component
     * @param earlyInit do we incarnate the instance of the service component during start-up? 
     * @param description a description for the service component if any
     * @param componentType the type of service component
     * @param componentFlavour the flavour of the gicen component type
     * @param hasDynamicProxy create a dynamic proxy
     * @param interceptorList the list of service interceptor to be invoked
     * @param logCategory the category for creating the logger
     */
    public RoleEntryImpl( String name,
        String defaultClass,
        String shorthand,
        boolean earlyInit,
        String description,
        String componentType,
        String componentFlavour,
        boolean hasProxy,
        ArrayList interceptorList,
        String logCategory
        )
    {
        Validate.notEmpty(name,"name");
        Validate.notEmpty(defaultClass,"defaultClass");
        Validate.notEmpty(shorthand,"shorthand");
        Validate.notEmpty(componentType,"componentType");
        Validate.notEmpty(componentFlavour,"componentFlavour");
        Validate.notNull(interceptorList,"interceptorList");
        Validate.notEmpty(logCategory,"logCategory");
        
        this.name = name;
        this.implementationClazzName = defaultClass;
        this.shorthand = shorthand;
        this.isEarlyInit = earlyInit;
        this.description = description;
        this.componentType = componentType;
        this.componentFlavour = componentFlavour;
        this.hasDynamicProxy = hasProxy;
        this.interceptorList = interceptorList;
        this.logCategory = logCategory;
    }
            
    /**
     * @return Returns the componentType.
     */
    public String getComponentType()
    {
        return componentType;
    }
    
    /**
     * @return Returns the description.
     */
    public String getDescription()
    {
        return description;
    }
    
    /**
     * @return Returns the implementationClazzName.
     */
    public String getImplementationClazzName()
    {
        return implementationClazzName;
    }
    
    /**
     * @return Returns the isEarlyInit.
     */
    public boolean isEarlyInit()
    {
        return isEarlyInit;
    }
    
    /**
     * @return Returns the name.
     */
    public String getName()
    {
        return name;
    }
    
    /**
     * @return Returns the shorthand.
     */
    public String getShorthand()
    {
        return shorthand;
    }
    
    /**
     * @return Returns the componentFlavour.
     */
    public String getComponentFlavour()
    {
        return componentFlavour;
    }
    
    /**
     * @return Returns the hasDynamicProxy.
     */
    public boolean hasDynamicProxy()
    {
        return hasDynamicProxy;
    }
    
    /**
     * @param hasDynamicProxy The hasDynamicProxy to set.
     */
    public void setHasDynamicProxy(boolean hasProxy)
    {
        this.hasDynamicProxy = hasProxy;
    }
    
    /**
     * Determines if the given name of the interceptor is already defined.
     * 
     * @param interceptorName the name of the interceptor
     * @return true if it is already defined
     */
    public boolean hasInterceptor( String interceptorName )
    {
        String currInterceptorName = null;
        Iterator iterator = this.interceptorList.iterator();
        
        while( iterator.hasNext() )
        {
            currInterceptorName = (String) iterator.next();
            
            if( currInterceptorName.equals(interceptorName) )
            {
                return true;
            }
        }
        
        return false;
    }
    
    /**
     * Adds all given interceptors but avoiding duplicates.
     * 
     * @param collection the interceptors to be added
     */
    public void addInterceptors( Collection collection )
    {
        String currInterceptorName = null;
        Iterator iterator = collection.iterator();
        
        while( iterator.hasNext() )
        {
            currInterceptorName = (String) iterator.next();
            
            if( this.hasInterceptor(currInterceptorName) == false )
            {
                this.interceptorList.add(currInterceptorName);
            }
        }
    }
    
    /**
     * @return Returns the interceptorList.
     */
    public String[] getInterceptorList()
    {
        return (String[]) interceptorList.toArray(
            new String[interceptorList.size()]
            );
    }
    
    /**
     * @return Returns the logCategory.
     */
    public String getLogCategory()
    {
        return logCategory;
    }
    
    /**
     * @see java.lang.Object#toString()
     */
    public String toString()
    {
        ToStringBuilder toStringBuilder = new ToStringBuilder(this);
        toStringBuilder.append("name",this.name);
        toStringBuilder.append("shorthand",this.shorthand);
        toStringBuilder.append("implementationClazzName",this.implementationClazzName);
        toStringBuilder.append("isEarlyInit",this.isEarlyInit);
        toStringBuilder.append("hasDynamicProxy",this.hasDynamicProxy);
        toStringBuilder.append("componentType",this.componentType);
        toStringBuilder.append("componentFlavour",this.componentFlavour);
        toStringBuilder.append("interceptorList",this.interceptorList);
        toStringBuilder.append("logCategory",this.logCategory);
        toStringBuilder.append("description",this.description);
        return toStringBuilder.toString();
    }
}