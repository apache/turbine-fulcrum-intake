/*
 * Created on 21.02.2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.apache.fulcrum.yaafi.framework.role;

/**
 * @author Sigi
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public interface RoleEntry
{
    /**
     * @return Returns the componentType.
     */
    public abstract String getComponentType();

    /**
     * @return Returns the description.
     */
    public abstract String getDescription();

    /**
     * @return Returns the implementationClazzName.
     */
    public abstract String getImplementationClazzName();

    /**
     * @return Returns the isEarlyInit.
     */
    public abstract boolean isEarlyInit();

    /**
     * @return Returns the name.
     */
    public abstract String getName();

    /**
     * @return Returns the shorthand.
     */
    public abstract String getShorthand();

    /**
     * @return Returns the componentFlavour.
     */
    public abstract String getComponentFlavour();
}