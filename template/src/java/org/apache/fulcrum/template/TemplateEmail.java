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


import java.util.ArrayList;
import java.util.List;
import java.util.Iterator;
import java.io.StringWriter;
import javax.mail.internet.InternetAddress;
import org.apache.commons.mail.SimpleEmail;
import org.apache.fulcrum.util.WordWrapUtils;

/**
 * This is a simple class for sending email from within the TemplateService.
 * Essentially, the body of the email is processed with a 
 * TemplateContext object.
 * The beauty of this is that you can send email from within your
 * template layer or from your business logic in your Java code.
 * The body of the email is just a TemplateService template so you can use
 * all the template functionality of your TemplateService within your emails!
 *
 * <p>Example Usage (This all needs to be on one line in your
 * template):
 *
 * <p>Setup your imports:
 *
 * <p>import org.apache.fulcrum.template.TemplateEmail;
 * <p>import org.apache.turbine.modules.ContextAdapter;
 *
 * <p>Setup your context:
 *
 * <p>context.put ("TemplateEmail", new TemplateEmail() );
 * <p>context.put ("contextAdapter", new ContextAdapter(context) );
 *
 * <p>Then, in your template (Velocity Example):
 *
 * <pre>
 * $TemplateEmail.setTo("Jon Stevens", "jon@latchkey.com")
 *     .setFrom("Mom", "mom@mom.com").setSubject("Eat dinner")
 *     .setTemplate("email/momEmail.vm")
 *     .setContext($contextAdapter)
 * </pre>
 *
 * The email/momEmail.vm template will then be parsed with the
 * Context that was defined with setContext().
 *
 * <p>If you want to use this class from within your Java code all you
 * have to do is something like this:
 *
 * <p>import org.apache.fulcrum.template.TemplateEmail;
 * <p>import org.apache.turbine.modules.ContextAdapter;
 *
 * <pre>
 * TemplateEmail ve = new TemplateEmail();
 * ve.setTo("Jon Stevens", "jon@latchkey.com");
 * ve.setFrom("Mom", "mom@mom.com").setSubject("Eat dinner");
 * ve.setContext(new ContextAdapter(context));
 * ve.setTemplate("email/momEmail.vm")
 * ve.send();
 * </pre>
 *
 * <p>(Note that when used within a Velocity template, the send method
 * will be called for you when Velocity tries to convert the
 * TemplateEmail to a string by calling toString()).</p>
 *
 * <p>If you need your email to be word-wrapped, you can add the 
 * following call to those above:
 *
 * <pre>
 * ve.setWordWrap (60);
 * </pre>
 *
 * <p>This class is just a wrapper around the SimpleEmail class.
 * Thus, it uses the JavaMail API and also depends on having the
 * mail.host property set in the System.properties().
 *
 * @author <a href="mailto:jon@latchkey.com">Jon S. Stevens</a>
 * @author <a href="mailto:gcoladonato@yahoo.com">Greg Coladonato</a>
 * @author <a href="mailto:elicia@collab.net">Elicia David</a>
 * @version $Id$
 */
public class TemplateEmail
{
    /** The to name field. */
    private String toName = null;

    /** The to email field. */
    private String toEmail = null;

    /** The from name field. */
    private String fromName = null;

    /** The from email field. */
    private String fromEmail = null;

    /** The cc name field. */
    private String ccName = null;

    /** The cc email field. */
    private String ccEmail = null;

    /** The subject of the message. */
    private String subject = null;

    /** The to email list. */
    private List toList = null;

    /** The cc list. */
    private List ccList = null;

    /** The cc list. */
    private List replyToList = null;

    /** The column to word-wrap at.  <code>0</code> indicates no wrap. */
    private int wordWrap = 0;

    /**
     * The template to process, relative to the TemplateService template
     * directory.
     */
    private String template = null;

    /**
     * A Context
     */
    private TemplateContext context = null;

    /**
     * The charset
     */
    private String charset = null;
    
    /**
     * The templateService to use in generating text
     *
     */
    private TemplateService templateService;

    /**
     * Constructor
     */
    public TemplateEmail()
    {
        this(null);
    }

    /**
     * Constructor
     */
    public TemplateEmail(TemplateContext context)
    {
        this.context = context;
    }

    /** Add a recipient TO to the email.
     *
     * @param email A String.
     * @param name A String.
     */
    public void addTo(String email, String name) 
        throws Exception
    {
        try
        {
            if ((name == null) || (name.trim().equals("")))
            {
                name = email;
            }

            if (toList == null)
            {
                toList = new ArrayList();
            }

            toList.add(new InternetAddress(email, name));
        }
        catch (Exception e)
        {
            throw new Exception("Cannot add 'To' recipient: " + e);
        }
    }
 
    /**
     * Add a recipient CC to the email.
     *
     * @param email A String.
     * @param name A String.
     */
    public void addCc(String email, String name) 
        throws Exception
    {
        try
        {
            if ((name == null) || (name.trim().equals("")))
            {
                name = email;
            }

            if (ccList == null)
            {
                ccList = new ArrayList();
            }

            ccList.add(new InternetAddress(email, name));
        }
        catch (Exception e)
        {
            throw new Exception("Cannot add 'CC' recipient: " + e);
        }
    }


    /**
     * The given Unicode string will be charset-encoded using the specified 
     * charset. The charset is also used to set the "charset" parameter.
     *
     * @param charset a <code>String</code> value
     */
    public void setCharset(String charset)
    {
        this.charset = charset;
    }

    /**
     * To: name, email
     *
     * @param to A String with the TO name.
     * @param email A String with the TO email.
     * @return A TemplateEmail (self).
     */
    public TemplateEmail setTo(String to,
                               String email)
    {
        this.toName = to;
        this.toEmail = email;
        return (this);
    }

    /**
     * From: name, email.
     *
     * @param from A String with the FROM name.
     * @param email A String with the FROM email.
     * @return A TemplateEmail (self).
     */
    public TemplateEmail setFrom(String from,
                                 String email)
    {
        this.fromName = from;
        this.fromEmail = email;
        return (this);
    }

    /**
     * CC: name, email.
     *
     * @param from A String with the CC name.
     * @param email A String with the CC email.
     * @return A TemplateEmail (self).
     */
    public TemplateEmail setCC(String cc,
                                 String email)
    {
        this.ccName = cc;
        this.ccEmail = email;
        return (this);
    }


    /**
     * Add a reply to address to the email.
     *
     * @param email A String.
     * @param name A String.
     * @return An Email.
     * @exception MessagingException.
     */
    public TemplateEmail addReplyTo( String name, String email) 
    {
        String[] emailName = new String[2];
        emailName[0] = email;
        emailName[1] = name;
        if (replyToList == null) 
        {
            replyToList = new ArrayList(3);
        }        
        replyToList.add(emailName);
        return this;
    }

    private List headersList;
    public TemplateEmail addHeader(String name, String value)
    {
        String[] pair = new String[2];
        pair[0] = name;
        pair[1] = value;
        if (headersList == null) 
        {
            headersList = new ArrayList(3);
        }        
        headersList.add(pair);
        return this;
    }

    /**
     * Subject.
     *
     * @param subject A String with the subject.
     * @return A TemplateEmail (self).
     */
    public TemplateEmail setSubject(String subject)
    {
        if (subject == null)
        {
            this.subject = "";
        }
        else
        {
            this.subject = subject;
        }
        return (this);
    }

    /**
     * TemplateService template to execute. Path is relative to the
     * TemplateService templates directory.
     *
     * @param template A String with the template.
     * @return A TemplateEmail (self).
     */
    public TemplateEmail setTemplate(String template)
    {
        this.template = template;
        return (this);
    }

    /**
     * Set the column at which long lines of text should be word-
     * wrapped. Setting to zero turns off word-wrap (default).
     *
     * NOTE: don't use tabs in your email template document,
     * or your word-wrapping will be off for the lines with tabs
     * in them.
     *
     * @param wordWrap The column at which to wrap long lines.
     * @return A TemplateEmail (self).
     */
    public TemplateEmail setWordWrap(int wordWrap)
    {
        this.wordWrap = wordWrap;
        return (this);
    }

    /**
     * Set the context object that will be merged with the 
     * template.
     *
     * @param context A TemplateContext context object.
     * @return A TemplateEmail (self).
     */
    public TemplateEmail setContext(TemplateContext context)
    {
        this.context = context;
        return (this);
    }

    /**
     * Get the context object that will be merged with the 
     * template.
     *
     * @return A TemplateContext.
     */
    public TemplateContext getContext()
    {
        return this.context;
    }

    /**
     * This method sends the email. It will throw an exception
     * if the To name or To Email values are null.
     */
    public void send()
        throws Exception
    {
        if (toEmail == null || toName == null)
        {
            throw new Exception ("Must set a To:");
        }

        // this method is only supposed to send to one user (additional cc:
        // users are ok.)
        toList = null;
        addTo(toEmail, toName);
        sendMultiple();
    }

    /**
     * This method sends the email to multiple addresses.
     */
    public void sendMultiple()
        throws Exception
    {
        if (toList == null || toList.isEmpty())
        {
            throw new Exception ("Must set a To:");
        }

        // Process the template.
        StringWriter sw = new StringWriter();
        templateService.handleRequest(context,template, sw);
        String body = sw.toString();

        // If the caller desires word-wrapping, do it here
        if (wordWrap > 0)
        {
            body = WordWrapUtils.wrapText (body,
                                     System.getProperty("line.separator"),
                                     wordWrap);
        }

        SimpleEmail se = new SimpleEmail();
        se.setFrom(fromEmail, fromName);
        se.setTo(toList);
        if (ccList != null && !ccList.isEmpty())
        {
            se.setCc(ccList);
        }
        addReplyTo(se);
        if (charset != null) 
        {
            se.setCharset(charset);            
        }
        se.setSubject(subject);
        se.setMsg(body);

        if (headersList != null) 
        {
            Iterator i = headersList.iterator();
            while (i.hasNext()) 
            {
                String[] pair = (String[])i.next();
                se.addHeader(pair[0], pair[1]);
            }
        }
        
        se.send();
    }
    
    

    /**
     * A javabean style setter for passing in manually a templateservice
     * @param templateService The templateService to set.
     */
    public void setTemplateService(TemplateService templateService) {
        this.templateService = templateService;
    }
    /**
     * if any reply-to email addresses exist, add them to the SimpleEmail
     *
     * @param se a <code>SimpleEmail</code> value
     * @exception Exception if an error occurs
     */
    private void addReplyTo(SimpleEmail se)
        throws Exception
    {
        if (replyToList != null) 
        {
            Iterator i = replyToList.iterator();
            while (i.hasNext()) 
            {
                String[] emailName = (String[])i.next();
                se.addReplyTo(emailName[0], emailName[1]);
            }
        }
    }

    /**
     * The method toString() calls send() for ease of use within a
     * TemplateService template (see example usage above).
     *
     * @return An empty string.
     */
    public String toString()
    {
        try
        {
            send();
        }
        catch (Exception e)
        {
//            Log.error ("TemplateEmail error", e);
        }
        return "";
    }
}
