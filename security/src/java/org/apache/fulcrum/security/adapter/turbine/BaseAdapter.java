/*
 * Created on Aug 22, 2003
 *
 */
package org.apache.fulcrum.security.adapter.turbine;
import org.apache.commons.beanutils.Converter;
import org.apache.commons.beanutils.converters.IntegerConverter;
import org.apache.fulcrum.security.entity.SecurityEntity;
import org.apache.fulcrum.security.entity.impl.SecurityEntityImpl;
import org.apache.turbine.util.security.TurbineSecurityException;
/**
 * @author Eric Pugh
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class BaseAdapter
{
    SecurityEntity entity = null;
    private Converter converter = new IntegerConverter();
    public BaseAdapter()
    {
        super();
        entity = new SecurityEntityImpl();
        entity.setName("");
    }
    public BaseAdapter(org.apache.fulcrum.security.entity.SecurityEntity entity)
    {
        super();
        this.entity = entity;
    }
    public int getId()
    {
        return getIdAsObj().intValue();
    }
    /* (non-Javadoc)
     * @see org.apache.turbine.om.security.SecurityEntity#getIdAsObj()
     */
    public Integer getIdAsObj()
    {
        return (Integer) converter.convert(Integer.class, entity.getId());
    }
    public String getName()
    {
        return entity.getName();
    }
	public void setName(String name)
	 {
		throw new RuntimeException("Unsupported operation");
	 }
    /* (non-Javadoc)
     * @see org.apache.turbine.om.security.SecurityEntity#setId(int)
     */
    public void setId(int arg0)
    {
        throw new RuntimeException("Unsupported operation");
    }
    /* (non-Javadoc)
    	* @see org.apache.turbine.om.security.Group#save()
    	*/
    public void save() throws TurbineSecurityException
    {
        throw new RuntimeException("Unsupported operation");
    }
    /* (non-Javadoc)
    	* @see org.apache.turbine.om.security.Group#remove()
    	*/
    public void remove() throws TurbineSecurityException
    {
        throw new RuntimeException("Unsupported operation");
    }
    /* (non-Javadoc)
    	* @see org.apache.turbine.om.security.Group#rename(java.lang.String)
    	*/
    public void rename(String arg0) throws TurbineSecurityException
    {
        throw new RuntimeException("Unsupported operation");
    }
}
