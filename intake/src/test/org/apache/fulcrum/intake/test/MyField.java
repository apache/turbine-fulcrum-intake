package org.apache.fulcrum.intake.test;

import org.apache.fulcrum.intake.IntakeException;
import org.apache.fulcrum.intake.model.Group;
import org.apache.fulcrum.intake.model.StringField;
import org.apache.fulcrum.intake.xmlmodel.XmlField;

public class MyField extends StringField
{

    public MyField(XmlField field, Group group) throws IntakeException
    {
        super(field, group);
        log.info("Instance of MyField created.");
    }

}
