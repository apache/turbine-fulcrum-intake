package org.apache.fulcrum.template;


/*
 * Copyright 2001-2004 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License")
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


import java.net.URL;
import java.util.Hashtable;
import javax.mail.MessagingException;

import org.apache.commons.mail.HtmlEmail;

/**
 * This is a simple class for sending html email from within the TemplateService.
 * Essentially, the bodies (text and html) of the email are a TemplateService
 * TemplateContext objects.  The beauty of this is that you can send email
 * from within your TemplateService template or from your business logic in
 * your Java code.  The body of the email is just a TemplateService template
 * so you can use all the template functionality of your TemplateService within
 * your emails!
 *
 * <p>This class allows you to send HTML email with embedded content
 * and/or with attachments.  You can access the TemplateHtmlEmail
 * instance within your templates trough the <code>$mail</code>
 * Velocity variable.
 * <p><code>TemplateHtmlEmail	myEmail= new TemplateHtmlEmail(context);<br>
 *                              context.put("mail", theMessage);</code>
 *
 *
 * <p>The templates should be located under your TemplateService template
 * directory.
 *
 * <p>This class extends the HtmlEmail class.  Thus, it uses the
 * JavaMail API and also depends on having the mail.host property
 * set in the System.getProperties().
 *
 * @author <a href="mailto:A.Schild@aarboard.ch">Andre Schild</a>
 * @author <a href="mailto:jon@latchkey.com">Jon S. Stevens</a>
 * @version $Id$
 */
public class TemplateHtmlEmail
    extends HtmlEmail
{
    /**
     * The html template to process, relative to VM's template
     * directory.
     */
    private String htmlTemplate = null;

    /**
     * A Context object which stores the information
     * needed to construct the email.
     */
    private TemplateContext context = null;

    /**
     * The text template to process, relative to VM's template
     * directory.
     */
    private String textTemplate = null;

    /** The map of embedded files. */
    private Hashtable embmap = null;
    
    /**
     * The templateService to use in generating text
     *
     */
    private TemplateService templateService;    

    /**
     * Constructor, sets the TemplateContext object.
     *
     * @param data A TemplateContext object.
     * @exception MessagingException.
     */
    public TemplateHtmlEmail(TemplateContext context)
        throws MessagingException
    {
        this.context = context;
        embmap = new Hashtable();
    }

    /**
     * Set the HTML template for the mail.  This is the TemplateService
     * template to execute for the HTML part.  Path is relative to the
     * TemplateService templates directory.
     *
     * @param template A String.
     * @return A TemplateHtmlEmail (self).
     */
    public TemplateHtmlEmail setHtmlTemplate(String template)
    {
        this.htmlTemplate = template;
        return this;
    }

    /**
     * Set the text template for the mail.  This is the TemplateService
     * template to execute for the text part.  Path is relative to the
     * TemplateService templates directory
     *
     * @param template A String.
     * @return A TemplateHtmlEmail (self).
     */
    public TemplateHtmlEmail setTextTemplate(String template)
    {
        this.textTemplate = template;
        return this;
    }

    /**
     * Actually send the mail.
     *
     * @exception MessagingException.
     */
    public void send()
        throws MessagingException
    {
        context.put("mail",this);

        String htmlbody = "";
        String textbody = "";

        // Process the templates.
        try
        {
            if(htmlTemplate != null)
            {
                htmlbody = templateService.handleRequest(
                    context, htmlTemplate);
            }
            
            if(textTemplate != null)
            {
                textbody = templateService.handleRequest(
                    context, textTemplate);
            }                    
        }
        catch( Exception e)
        {
            throw new MessagingException("Cannot parse template", e);
        }

        setHtmlMsg(htmlbody);
        setTextMsg(textbody);

        super.send();
    }

    /**
     * Embed a file in the mail.  The file can be referenced through
     * its Content-ID.  This function also registers the CID in an
     * internal map, so the embedded file can be referenced more than
     * once by using the getCid() function.  This may be useful in a
     * template.
     *
     * <p>Example of template:
     *
     * <code><pre width="80">
     * &lt;html&gt;
     * &lt;!-- $mail.embed("http://server/border.gif","border.gif"); --&gt;
     * &lt;img src=$mail.getCid("border.gif")&gt;
     * &lt;p&gt;This is your content
     * &lt;img src=$mail.getCid("border.gif")&gt;
     * &lt;/html&gt;
     * </pre></code>
     *
     * @param surl A String.
     * @param name A String.
     * @return A String with the cid of the embedded file.
     * @exception MessagingException.
     * @see HtmlEmail#embed(URL surl, String name) embed.
     */
    public String embed(String surl,
                        String name)
        throws MessagingException
    {
        String cid ="";
        try
        {
            URL url = new URL(surl);
            cid = super.embed(url, name);
            embmap.put(name,cid);
        }
        catch( Exception e )
        {
//            Log.error("cannot embed "+surl+": ", e);
        }
        return cid;
    }

    /**
     * Get the cid of an embedded file.
     *
     * @param filename A String.
     * @return A String with the cid of the embedded file.
     * @see #embed(String surl, String name) embed.
     */
    public String getCid(String filename)
    {
        String cid = (String)embmap.get(filename);
        return "cid:"+cid;
    }
    
    /**
     * A javabean style setter for passing in manually a templateservice
     * @param templateService The templateService to set.
     */
    public void setTemplateService(TemplateService templateService) {
        this.templateService = templateService;
    }    
}
 
