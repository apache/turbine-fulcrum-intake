package org.apache.fulcrum.intake.model;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import org.apache.fulcrum.intake.IntakeException;

/**
 * Creates Field objects.
 *
 * @author <a href="mailto:tv@apache.org">Thomas Vandahl</a>
 * @version $Id: FieldFactory.java 1200653 2011-11-11 00:05:28Z tv $
 */
public class FieldAdapter extends XmlAdapter<XmlField, Field<?>>
{
    /**
     * Creates a Field object appropriate for the type specified
     * in the xml file.
     *
     * @see javax.xml.bind.annotation.adapters.XmlAdapter#unmarshal(java.lang.Object)
     */
    @Override
    public Field<?> unmarshal(XmlField xmlField) throws Exception
    {
        Field<?> field = null;
        FieldType type = xmlField.getType();

        if (type == null)
        {
            throw new IntakeException("An unsupported type has been specified for " +
                    xmlField.getName() + " in group " + xmlField.getGroup().getIntakeGroupName());
        }
        else
        {
            field = type.getInstance(xmlField, xmlField.getGroup());
        }

        return field;
    }

    /**
     * @see javax.xml.bind.annotation.adapters.XmlAdapter#marshal(java.lang.Object)
     */
    @Override
    public XmlField marshal(Field<?> field) throws Exception
    {
        // This is never used in this context
        return new XmlField();
    }
}
