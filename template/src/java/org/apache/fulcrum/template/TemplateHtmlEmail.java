package org.apache.fulcrum.template;

/* ====================================================================
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2001 The Apache Software Foundation.  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgment:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgment may appear in the software itself,
 *    if and wherever such third-party acknowledgments normally appear.
 *
 * 4. The names "Apache" and "Apache Software Foundation" and 
 *    "Apache Turbine" must not be used to endorse or promote products 
 *    derived from this software without prior written permission. For 
 *    written permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache",
 *    "Apache Turbine", nor may "Apache" appear in their name, without 
 *    prior written permission of the Apache Software Foundation.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
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
                htmlbody = TemplateServiceFacade.handleRequest(
                    context, htmlTemplate);
            }
            
            if(textTemplate != null)
            {
                textbody = TemplateServiceFacade.handleRequest(
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
}
 