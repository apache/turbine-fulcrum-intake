package org.apache.fulcrum.yaafi.framework.role;

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
     */
    public RoleEntryImpl( String name,
        String defaultClass,
        String shorthand,
        boolean earlyInit,
        String description,
        String componentType,
        String componentFlavour )
    {
        this.name = name;
        this.implementationClazzName = defaultClass;
        this.shorthand = shorthand;
        this.isEarlyInit = earlyInit;
        this.description = description;
        this.componentType = componentType;
        this.componentFlavour = componentFlavour;
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
     * @see java.lang.Object#toString()
     */
    public String toString()
    {
        StringBuffer result = new StringBuffer();

        final String newLine = System.getProperty("line.separator");

        result.append(this.getClass().getName() + " Object {");
        result.append(newLine);
        result.append(this.getName());
        result.append(";");

        result.append(" class: ");
        result.append(this.getImplementationClazzName());
        result.append(";");

        result.append(" componentType: ");
        result.append(this.getComponentType());
        result.append(";");

        result.append(" componentFlavour: ");
        result.append(this.getComponentFlavour());
        result.append(";");

        result.append(" shorthand: ");
        result.append(this.getShorthand());
        result.append(";");

        result.append(" description: ");
        result.append(this.getDescription());
        result.append(";");

        result.append("}");
        
        return result.toString();
    }

}