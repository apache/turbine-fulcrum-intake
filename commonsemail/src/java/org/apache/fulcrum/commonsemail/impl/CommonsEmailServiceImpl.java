package org.apache.fulcrum.commonsemail.impl;

/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import org.apache.avalon.framework.activity.Disposable;
import org.apache.avalon.framework.activity.Initializable;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.configuration.Reconfigurable;
import org.apache.avalon.framework.context.Context;
import org.apache.avalon.framework.context.ContextException;
import org.apache.avalon.framework.context.Contextualizable;
import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.avalon.framework.thread.ThreadSafe;
import org.apache.commons.mail.DefaultAuthenticator;
import org.apache.commons.mail.Email;
import org.apache.commons.mail.EmailAttachment;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.HtmlEmail;
import org.apache.commons.mail.MultiPartEmail;
import org.apache.commons.mail.SimpleEmail;
import org.apache.fulcrum.commonsemail.CommonsEmailService;
import org.apache.fulcrum.commonsemail.SendDeliveryStatus;

import javax.activation.DataSource;
import javax.mail.Address;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.event.TransportEvent;
import javax.mail.event.TransportListener;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.WeakHashMap;

/**
 * A service taking care of most of the commons-email configuration such as
 *
 * <ul>
 *   <li>authentication</li>
 *   <li>mail session</li>
 *   <li>mail headers</li>
 * </ul>
 *
 * @author <a href="mailto:siegfried.goeschl@it20one.at">Siegfried Goeschl</a>
 */

public class CommonsEmailServiceImpl
    extends AbstractLogEnabled
    implements CommonsEmailService, Contextualizable, Reconfigurable, Initializable, Disposable, ThreadSafe,
        TransportListener, CommonsEmailConstants
{
    /** context key for persistent directory */
    private final static String URN_AVALON_HOME = "context-root";

    /** context key for temporary directory */
    private final static String URN_AVALON_TEMP = "impl.workDir";

    /** the file extension used for a MimeMessage */
    private final static String MIME_MESSAGE_EXTENSION = "eml";

    /** counter for creating a file name */
    private int fileNameCounter;
    
    /** the Avalon home directory */
    private File serviceHomeDir;

    /** the Avalon temp directory */
    private File serviceTempDir;

    /** the name of the default domain */
    private String defaultDomainName;

    /** the available domains */
    private CommonsEmailDomainEntry[] domainList;

    /** date formatter to create timestamps for debug utput */
    private final SimpleDateFormat simpleDateFormat;

    /** is the service instance initialized */
    private volatile boolean isInitialized;

    /** keep track of the incoming TransportEvents using MimeMessage ==> SendDeliveryStatus */
    private Map sendDeliveryStatusMap;

    /**
     * Constructor
     */
    public CommonsEmailServiceImpl()
    {
        this.fileNameCounter = (int) System.currentTimeMillis() % 1000;
        this.simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HHmmssSSS");
        this.sendDeliveryStatusMap = new WeakHashMap();
    }

    /////////////////////////////////////////////////////////////////////////
    // Avalon Lifecycle Implementation
    /////////////////////////////////////////////////////////////////////////

    /**
     * @see org.apache.avalon.framework.context.Contextualizable#contextualize(org.apache.avalon.framework.context.Context)
     */
    public void contextualize(Context context) throws ContextException
    {
        this.serviceHomeDir = (File) context.get(URN_AVALON_HOME);
        this.serviceTempDir = (File) context.get(URN_AVALON_TEMP);
    }

    /**
     * @see org.apache.avalon.framework.configuration.Configurable#configure(org.apache.avalon.framework.configuration.Configuration)
     */
    public void configure(Configuration configuration) throws ConfigurationException
    {
        this.defaultDomainName = configuration.getChild("defaultDomain").getValue(null);

        // load all available domains

        Configuration[] domainListConf = configuration.getChild("domains").getChildren("domain");
        this.domainList = new CommonsEmailDomainEntry[domainListConf.length];

        for( int i=0; i<domainListConf.length; i++ )
        {
            Configuration domainConf = domainListConf[i];
            this.domainList[i] = new CommonsEmailDomainEntry().initialize(domainConf);
            this.getLogger().debug("Adding the following domain : " + this.domainList[i].toString());
        }
    }

    /**
     * @see org.apache.avalon.framework.activity.Initializable#initialize()
     */
    public void initialize() throws Exception
    {
        this.isInitialized = true;
    }

    /**
     * @see org.apache.avalon.framework.configuration.Reconfigurable#reconfigure(org.apache.avalon.framework.configuration.Configuration)
     */
    public void reconfigure(Configuration configuration) throws ConfigurationException
    {
        this.configure(configuration);
    }

    /**
     * @see org.apache.avalon.framework.activity.Disposable#dispose()
     */
    public void dispose()
    {
        this.isInitialized = false;
        this.defaultDomainName = null;
        this.domainList = null;
        this.serviceHomeDir = null;
        this.serviceTempDir = null;
        this.sendDeliveryStatusMap = null;
    }

    /////////////////////////////////////////////////////////////////////////
    // Service Interface Implementation
    /////////////////////////////////////////////////////////////////////////

    /**
     * @see org.apache.fulcrum.commonsemail.CommonsEmailService#isMailDoNotSend(java.lang.String)
     */
    public boolean isMailDoNotSend(String domainName)
    {
        CommonsEmailDomainEntry domain = this.getDomain(domainName);
        return domain.isMailDoNotSend();
    }

    /**
     * @see org.apache.fulcrum.commonsemail.CommonsEmailService#createSmtpSession(java.lang.String)
     */
    public Session createSmtpSession(String domainName)
    {
        CommonsEmailDomainEntry domain = this.getDomain(domainName);

        return this.createSmtpSession(
            domainName,
            domain.getAuthUsername(),
            domain.getAuthPassword()
            );
    }

    /**
     * @see org.apache.fulcrum.commonsemail.CommonsEmailService#createSmtpSession(java.lang.String, java.lang.String, java.lang.String)
     */
    public Session createSmtpSession(String domainName, String username,
        String password)
    {
        Session result;
        CommonsEmailDomainEntry domain = this.getDomain(domainName);
        Properties properties = new Properties(System.getProperties());
        DefaultAuthenticator authenticator = null;

        properties.setProperty(MAIL_DEBUG, Boolean.toString(domain.isMailDebug()));
        properties.setProperty(MAIL_TRANSPORT_PROTOCOL, "smtp");
        properties.setProperty(MAIL_SMTP_HOST, domain.getMailSmtpHost());
        properties.setProperty(MAIL_SMTP_PORT, Integer.toString(domain.getMailSmtpPort()));
        properties.setProperty(MAIL_SMTP_CONNECTIONTIMEOUT, Integer.toString(domain.getMailSmtpConnectionTimeout()));
        properties.setProperty(MAIL_SMTP_TIMEOUT, Integer.toString(domain.getMailSmtpTimeout()));
        properties.setProperty(MAIL_SMTP_SENTPARTIAL, Boolean.toString(domain.isMailSmtpSendPartial()));

        if(domain.getMailBounceAddress() != null)
        {
            properties.setProperty(MAIL_SMTP_FROM,domain.getMailBounceAddress());
        }

        // if SMTP AUTH is enabled create a default authenticator

        if( domain.hasSmtpAuthentication() )
        {
            properties.setProperty(MAIL_SMTP_AUTH, "true");

            authenticator = new DefaultAuthenticator(
                username,
                password
                );
        }

        if( domain.hasSmtpAuthentication() )
        {
            result = Session.getInstance(properties, authenticator);
        }
        else
        {
            result = Session.getInstance(properties);
        }

        return result;
    }
    /**
     * @see org.apache.fulcrum.commonsemail.CommonsEmailService#createHtmlEmail(java.lang.String)
     */
    public HtmlEmail createHtmlEmail(String domainName) throws EmailException
    {
        HtmlEmail result = new HtmlEmail();
        this.configure(domainName,result);
        return result;
    }

    /**
     * @see org.apache.fulcrum.commonsemail.CommonsEmailService#createMultiPartEmail(java.lang.String)
     */
    public MultiPartEmail createMultiPartEmail(String domainName) throws EmailException
    {
        MultiPartEmail result = new MultiPartEmail();
        this.configure(domainName,result);
        return result;
    }

    /**
     * @see org.apache.fulcrum.commonsemail.CommonsEmailService#createSimpleEmail(java.lang.String)
     */
    public SimpleEmail createSimpleEmail(String domainName) throws EmailException
    {
        SimpleEmail result = new SimpleEmail();
        this.configure(domainName,result);
        return result;
    }

    /**
     * @see org.apache.fulcrum.commonsemail.CommonsEmailService#send(org.apache.commons.mail.Email)
     */
    public MimeMessage send(Email email) throws EmailException
    {
        String domainName = email.getFromAddress().getAddress();
        return this.send(domainName,email);
    }

    /**
     * @see org.apache.fulcrum.commonsemail.CommonsEmailService#send(org.apache.commons.mail.Email)
     */
    public MimeMessage send(String domainName, Email email) throws EmailException
    {
        MimeMessage mimeMessage = null;

        try
        {
            // get the configuration of this domain

            CommonsEmailDomainEntry domain = this.getDomain(domainName);

            // build the MimeMessage based on commons-email

            mimeMessage = this.buildMimeMessage(email);

            // update the MimeMessage based on the domain configuration

            mimeMessage = this.updateMimeMessage(domain, mimeMessage);

            // send the MimeMessage

            this.send(
                domain,
                email.getMailSession(),
                mimeMessage,
                mimeMessage.getAllRecipients()
                );

            return mimeMessage;
        }
        catch (EmailException e)
        {
            String msg = "Sending the mail failed";
            this.getLogger().error(msg,e);
            this.dump(mimeMessage,"error");
            throw e;
        }
        catch(MessagingException e)
        {
            // we are repackaging the exception and a few mail exception do not contain a message text
            String msg = "Sending the mail failed due to a messaging problem : [" + e.getClass().getName() + "] " + e.getMessage();
            this.getLogger().error(msg,e);
            this.dump(mimeMessage,"error");
            throw new EmailException(msg,e);
        }
        catch(Throwable t)
        {
            String msg = "Sending the mail failed due to an internal error : [" + t.getClass().getName() + "] " + t.getMessage();
            this.getLogger().error(msg,t);
            this.dump(mimeMessage,"error");
            throw new EmailException(msg,t);
        }
    }

    /**
     * @see org.apache.fulcrum.commonsemail.CommonsEmailService#send(java.lang.String, javax.mail.Session, javax.mail.internet.MimeMessage)
     */
    public MimeMessage send(String domainName, Session session, MimeMessage mimeMessage)
        throws MessagingException
    {
        // get the configuration of this domain

        CommonsEmailDomainEntry domain = this.getDomain(domainName);

        //  update the MimeMessage based on the domain configuration

        MimeMessage result = this.updateMimeMessage(domain, mimeMessage);

        // send the MimeMessage

        this.send(
            domain,
            session,
            mimeMessage,
            mimeMessage.getAllRecipients()
            );

        return result;
    }

    /**
     * @see org.apache.fulcrum.commonsemail.CommonsEmailService#send(java.lang.String, javax.mail.Session, javax.mail.internet.MimeMessage, javax.mail.Address[])
     */
    public MimeMessage send(String domainName, Session session,
        MimeMessage mimeMessage, Address [] recipients)
        throws MessagingException
    {
        // get the configuration of this domain

        CommonsEmailDomainEntry domain = this.getDomain(domainName);

        // update the MimeMessage based on the domain configuration

        MimeMessage result = this.updateMimeMessage(domain, mimeMessage);

        // send the MimeMessage

        this.send(
            domain,
            session,
            mimeMessage,
            recipients
            );

        return result;
    }

    /**
     * @see org.apache.fulcrum.commonsemail.CommonsEmailService#send(javax.mail.Session, javax.mail.internet.MimeMessage)
     */
    public MimeMessage send(Session session, MimeMessage mimeMessage)
        throws MessagingException
    {
         // determine the domain name

         if( ( mimeMessage.getFrom() == null ) || ( mimeMessage.getFrom().length == 0 ) )
         {
             throw new MessagingException("No from address defined - unable to determine a domain configuration");
         }

         InternetAddress fromAddress = (InternetAddress) mimeMessage.getFrom()[0];
         String domainName = fromAddress.getAddress();

         // get the configuration of this domain

         CommonsEmailDomainEntry domain = this.getDomain(domainName);

         // update the MimeMessage based on the domain configuration

         MimeMessage result = this.updateMimeMessage(domain, mimeMessage);

         // send the MimeMessage

         this.send(
             domain,
             session,
             mimeMessage,
             mimeMessage.getAllRecipients()
             );

         return result;
    }

    /**
     * @see org.apache.fulcrum.commonsemail.CommonsEmailService#createHtmlEmail(java.lang.String, java.util.Hashtable)
     */
    public HtmlEmail createHtmlEmail(String domainName, Hashtable content)
        throws EmailException
    {
        HtmlEmail result = this.createHtmlEmail(domainName);
        this.setEmailContent(result,content);
        return result;
    }

    /**
     * @see org.apache.fulcrum.commonsemail.CommonsEmailService#createMultiPartEmail(java.lang.String, java.util.Hashtable)
     */
    public MultiPartEmail createMultiPartEmail(String domainName,
        Hashtable content) throws EmailException
    {
        MultiPartEmail result = this.createMultiPartEmail(domainName);
        this.setEmailContent(result,content);
        return result;
    }

    /**
     * @see org.apache.fulcrum.commonsemail.CommonsEmailService#createSimpleEmail(java.lang.String, java.util.Hashtable)
     */
    public SimpleEmail createSimpleEmail(String domainName, Hashtable content)
        throws EmailException
    {
        SimpleEmail result = this.createSimpleEmail(domainName);
        this.setEmailContent(result,content);
        return result;
    }

    /**
     * @see org.apache.fulcrum.commonsemail.CommonsEmailService#getSendDeliveryStatus(javax.mail.internet.MimeMessage) 
     */
    public SendDeliveryStatus getSendDeliveryStatus( MimeMessage mimeMessage ) throws MessagingException
    {        
        if(mimeMessage == null)
        {
            throw new IllegalArgumentException( "mimeMessage is null");
        }
        
        return (SendDeliveryStatus) this.sendDeliveryStatusMap.get(mimeMessage);
    }

    /////////////////////////////////////////////////////////////////////////
    // Service Implementation
    /////////////////////////////////////////////////////////////////////////


    /**
     * @see javax.mail.event.TransportListener#messageDelivered(javax.mail.event.TransportEvent)
     */
    public void messageDelivered(TransportEvent transportEvent)
    {
        if( this.isInitialized() )
        {
            try
            {
                this.getLogger().info(
                    "The MimeMessage "
                    + this.getMessageId(transportEvent.getMessage())
                    + " was successfully delivered to the following recipients : "
                    + this.toString(transportEvent.getValidSentAddresses())
                    );

                SendDeliveryStatusImpl sendDeliveryStatus = this.getSendDeliveryStatus(transportEvent);

                if(sendDeliveryStatus != null)
                {
                    sendDeliveryStatus.add(transportEvent);
                }
                else
                {
                    MimeMessage mimeMessage = (MimeMessage) transportEvent.getMessage();
                    this.getLogger().error("Found no sendDeliveryStatus for " + mimeMessage.getMessageID());
                }
            }
            catch(Exception e)
            {
                String msg = "Unable to update the delivery status for 'messageDelivered'";
                this.getLogger().error(msg, e);
            }
        }
    }

    /**
     * @see javax.mail.event.TransportListener#messageNotDelivered(javax.mail.event.TransportEvent)
     */
    public void messageNotDelivered(TransportEvent transportEvent)
    {
        if( this.isInitialized() )
        {
            try
            {
                this.getLogger().error(
                    "The MimeMessage "
                    + this.getMessageId(transportEvent.getMessage())
                    + " was not delivered to any recipient due to following invalid addresses : "
                    + this.toString(transportEvent.getInvalidAddresses())
                    );

                SendDeliveryStatusImpl sendDeliveryStatus = this.getSendDeliveryStatus(transportEvent);
                
                if(sendDeliveryStatus != null)
                {
                    sendDeliveryStatus.add(transportEvent);
                }
                else
                {
                    MimeMessage mimeMessage = (MimeMessage) transportEvent.getMessage();
                    this.getLogger().error("Found no sendDeliveryStatus for " + mimeMessage.getMessageID());
                }                
            }
            catch(Exception e)
            {
                String msg = "Unable to update the delivery status for 'messageNotDelivered'";
                this.getLogger().error(msg, e);
            }
        }
    }

    /**
     * @see javax.mail.event.TransportListener#messagePartiallyDelivered(javax.mail.event.TransportEvent)
     */
    public void messagePartiallyDelivered(TransportEvent transportEvent)
    {
        // write to the logfile
        if( this.isInitialized() )
        {
            try
            {
                this.getLogger().warn(
                    "The MimeMessage "
                    + this.getMessageId(transportEvent.getMessage())
                    + "was only partially delivered "
                    + this.toString(transportEvent.getValidUnsentAddresses())
                    );

                SendDeliveryStatusImpl sendDeliveryStatus = this.getSendDeliveryStatus(transportEvent);

                if(sendDeliveryStatus != null)
                {
                    sendDeliveryStatus.add(transportEvent);
                }
                else
                {
                    MimeMessage mimeMessage = (MimeMessage) transportEvent.getMessage();
                    this.getLogger().error("Found no sendDeliveryStatus for " + mimeMessage.getMessageID());
                }                
            }
            catch(Exception e)
            {
                String msg = "Unable to update the delivery status for 'messagePartiallyDelivered'";
                this.getLogger().error(msg, e);
            }
        }
    }

    /**
     * @return Returns the isInitialized.
     */
    protected boolean isInitialized()
    {
        return isInitialized;
    }

    /**
     * @return Returns the serviceHomeDir.
     */
    protected File getServiceHomeDir()
    {
        return serviceHomeDir;
    }

    /**
     * @return Returns the serviceTempDir.
     * @noinspection WeakerAccess
     */
    protected File getServiceTempDir()
    {
        return serviceTempDir;
    }

    /**
     * Application specific hook to modify or create a new MimeMessage. A good
     * example would be S/MIME enabled service which creates a new
     * signed MimeMessage.
     *
     * @param mimeMessage the MimeMessage to be send
     * @return the updated MimeMessage
     * @throws MessagingException the post-processing failed
     * @noinspection WeakerAccess
     */
    protected MimeMessage onPostProcessMimeMessage(MimeMessage mimeMessage)
        throws MessagingException
    {
        return mimeMessage;
    }

    /**
     * Build a MimeMessage from the comons-email.
     *
     * @param email the underlying email for building the MimeMessage
     * @return the resulting MimeMessage
     * @throws EmailException building the message failed
     * @throws MessagingException the post-processing failed
     */
    private MimeMessage buildMimeMessage( Email email )
        throws EmailException, MessagingException
    {
        email.buildMimeMessage();
        return email.getMimeMessage();
    }

    /**
     * Updates the MimeMessage based on the domain settings. The implementation
     * calls the onPostProcessMimeMessage() method which can be overwritten by a
     * derived service implementation.
     *
     * @param domain the domain configuration
     * @param mimeMessage the MimeMessage to be updated
     * @return the resulting MimeMessage
     * @throws MessagingException the operation failed
     */
    private MimeMessage updateMimeMessage( CommonsEmailDomainEntry domain, MimeMessage mimeMessage )
        throws MessagingException
    {
        MimeMessage result;

        // dump the original MimeMessage

        if( domain.isMailDump() )
        {
            this.dump(mimeMessage,"original");
        }

        mimeMessage = this.overwrite(domain,mimeMessage);

        // dump the original MimeMessage

        if( domain.isMailDump() )
        {
            this.dump(mimeMessage,"overwrite");
        }

        result = this.onPostProcessMimeMessage(mimeMessage);

        if( domain.isMailDump() )
        {
            this.dump(result,"post");
        }

        return result;
    }

    /**
     * Sends a MimeMessage. We use a Transport instance to register
     * a transport listener to track invalid email addresses.
     *
     * @param domain the domain configuration
     * @param session the mail sessoin
     * @param mimeMessage the MimeMessage to be sent
     * @param recipients the list of recipients
     * @throws MessagingException sending the MimeMessage failed
     */
    private void send(
        CommonsEmailDomainEntry domain, Session session, MimeMessage mimeMessage, Address[] recipients)
        throws MessagingException
    {
        if( this.getLogger().isDebugEnabled() )
        {
            this.getLogger().debug(
                "Preparing to send the MimeMessage "
                + this.getLogMsg(mimeMessage)
                );
        }

        // get the current recipients for the mail since we allow
        // overwriting the recipients found in the message

        javax.mail.Address[] currRecipients;

        if( (recipients == null) || (recipients.length == 0) )
        {
            currRecipients = mimeMessage.getAllRecipients();
        }
        else
        {
            currRecipients = recipients;
        }

        // dump the MimeMessage to be sent

        if( domain.isMailDump() )
        {
            this.dump(mimeMessage,"send");
        }

        // keep track of the deliver status

        SendDeliveryStatusImpl sendDeliveryStatus = this.createSendDeliveryStatus(mimeMessage);

        if( !domain.isMailDoNotSend() )
        {
            try
            {
                long startTime = System.currentTimeMillis();

                // do the sending keyboard gymnastics

                Transport transport = session.getTransport("smtp");
                transport.addTransportListener(this);
                transport.connect();
                transport.sendMessage(mimeMessage, currRecipients);
                transport.close();

                long endTime = System.currentTimeMillis();
                long durationTime = endTime - startTime;

                if( this.getLogger().isInfoEnabled() )
                {
                    this.getLogger().info(
                        "Successfully sent the MimeMessage within "
                        + durationTime
                        + " ms "
                        + this.getLogMsg(mimeMessage)
                        );
                }

                if( domain.hasOnSuccessHook() )
                {
                    this.onSendSucceeded(domain,session,mimeMessage);
                }
            }
            catch (MessagingException e)
            {
                if( domain.hasOnFailureHook() )
                {
                    this.onSendFailed(domain,session,mimeMessage);
                }

                this.getLogger().error(
                    "Sending failed of the MimeMessage "
                    + this.getLogMsg(mimeMessage)
                    );

                throw e;
            }
            catch (Exception e)
            {
                if( domain.hasOnFailureHook() )
                {
                    this.onSendFailed(domain,session,mimeMessage);
                }

                this.getLogger().error(
                    "Sending failed of the MimeMessage "
                    + this.getLogMsg(mimeMessage)
                    );

                throw new MessagingException("Sending of the MimeMessage failed", e);
            }
        }
        else
        {
            // set a fake message id otherwise it will be null and
            // potentially breaking a client application
            
            String fakeMessageId = "<"
                + this.fileNameCounter
                + "." + System.currentTimeMillis()
                + ".CommonsEmailService."
                + System.getProperty( "user.name", "anonymous" )
                + "@localhost>";

            mimeMessage.setHeader( "Message-ID", fakeMessageId );

            // for some unknown reasons we have to write the MimeMessage
            // to be able to parse it later on

            NullOutputStream nos = new NullOutputStream();

            try
            {
                mimeMessage.writeTo( nos );
                nos.close();
            }
            catch(Exception e)
            {
                this.getLogger().error( "Unable to write the MimeMessage", e );  
            }

            // since we don't send the email assume that the delivery was successful

            sendDeliveryStatus.addSendAddressList(mimeMessage.getAllRecipients());

            if( domain.hasOnNotSendHook() )
            {
                this.onSendSupressed(domain,session,mimeMessage);
            }

            this.getLogger().debug(
                "The mail was not sent since due to"
                + " enforcement of the following domain configuration : "
                + domain.getDomainName()
                );
        }

        // store the message id

        sendDeliveryStatus.setMessageId( mimeMessage.getMessageID() );
    }

    /**
     * Application hook for processing a successful sending of a MimeMessage.
     *
     * @param domain the current domain
     * @param session the current mail session
     * @param mimeMessage the current MimeMessage
     */
    protected void onSendSucceeded(CommonsEmailDomainEntry domain, Session session, MimeMessage mimeMessage)
    {
        Configuration conf = domain.getOnSuccessHookConfiguration();
        String directoryName = conf.getChild("directory").getValue(this.getServiceTempDir().getAbsolutePath());
        this.writeMimeMessage(directoryName, mimeMessage);
    }

    /**
     * Application hook for processing a failed sending of a MimeMessage.
     *
     * @param domain the current domain
     * @param session the current mail session
     * @param mimeMessage the current MimeMessage
     */
    protected void onSendFailed(CommonsEmailDomainEntry domain, Session session, MimeMessage mimeMessage)
    {
        Configuration conf = domain.getOnFailureHookConfiguration();
        String directoryName = conf.getChild("directory").getValue(this.getServiceTempDir().getAbsolutePath());
        this.writeMimeMessage(directoryName, mimeMessage);
    }

    /**
     * Application hook for processing an supressed MimeMessage.
     *
     * @param domain the current domain
     * @param session the current mail session
     * @param mimeMessage the current MimeMessage
     */
    protected void onSendSupressed(CommonsEmailDomainEntry domain, Session session, MimeMessage mimeMessage)
    {
        Configuration conf = domain.getOnNotSendHookConfiguration();
        String directoryName = conf.getChild("directory").getValue(this.getServiceTempDir().getAbsolutePath());
        this.writeMimeMessage(directoryName, mimeMessage);
    }

    /**
     * Locates a domain for the given name using the following
     * policy
     *
     * <ul>
     *   <li>match the user-supplied name with the match field</li>
     *   <li>try to extract a email domain (e.g. apache.org) and match with the match field</li>
     *   <li>revert to the default domain</li>
     * </ul>
     *
     * @param name the user-suplplied name of the domain
     * @return the corresponding domain configuration
     */
    protected CommonsEmailDomainEntry getDomain( String name )
    {
        CommonsEmailDomainEntry result;

        // check if we really have a name

        if( (name != null) && (name.length() > 0) )
        {
            // check the match field of the available domains with the given name

            for( int i=0; i<this.getDomainList().length; i++ )
            {
                result = this.getDomainList()[i];

                if( result.getDomainMatch().equalsIgnoreCase(name) )
                {
                    return result;
                }
            }

            // extract the domain part of the email address and try it again

            if( name.lastIndexOf('@') > 0 )
            {
                String emailDomainPart = name.substring(
                    name.lastIndexOf('@')+1,
                    name.length()
                    );

                for( int i=0; i<this.getDomainList().length; i++ )
                {
                    result = this.getDomainList()[i];

                    if( result.getDomainMatch().equalsIgnoreCase(emailDomainPart) )
                    {
                        return result;
                    }
                }
            }
        }

        // revert to the default domain

        if( this.hasDefaulDomain() )
        {
            for( int i=0; i<this.getDomainList().length; i++ )
            {
                result = this.getDomainList()[i];

                if( result.getDomainMatch().equalsIgnoreCase(this.defaultDomainName) )
                {
                    this.getLogger().debug("Using the default domain : " + this.defaultDomainName );
                    return result;
                }
            }
        }
        else
        {
            throw new IllegalArgumentException("Unable to locate any domain for " + name );
        }

        throw new IllegalArgumentException("Unable to find the default doamin " + this.defaultDomainName );
    }

    /**
     * Configures a newly created email with the domain configuration.
     *
     * @param domainName name to lookup a domain
     * @param email the email to configure
     * @throws EmailException the configuration failed
     */
    private void configure( String domainName, Email email )
        throws EmailException
    {
        CommonsEmailDomainEntry domain = this.getDomain(domainName);

        // 1) set authentication

        // 1.1) set PopBeforeSmtp authentication

        if( domain.hasPopBeforeSmtpAuthentication() )
        {
            email.setPopBeforeSmtp(
                true,
                domain.getAuthPopHost(),
                domain.getAuthUsername(),
                domain.getAuthPassword()
                );
        }

        // 1.2) set SMTP authentication

        if( domain.hasSmtpAuthentication() )
        {
            email.setAuthentication(
                domain.getAuthUsername(),
                domain.getAuthPassword()
                );
        }

        // 2) set the mail host and port

        if( domain.getMailSmtpHost() != null )
        {
            email.setHostName(domain.getMailSmtpHost());
        }

        if( domain.getMailSmtpPort() != 0 )
        {
            email.setSmtpPort(domain.getMailSmtpPort());
        }

        // 3) set the from address if available

        if( domain.getMailFromEmail() != null )
        {
            if( domain.getMailFromName() != null )
            {
                email.setFrom(
                    domain.getMailFromEmail(),
                    domain.getMailFromName()
                    );
            }
            else
            {
                email.setFrom(domain.getMailFromEmail());
            }
        }

        // 4) set the replyTo if available

        if( domain.getMailReplyToEmail() != null )
        {
            if( domain.getMailReplyToName() != null )
            {
                email.addReplyTo(
                    domain.getMailReplyToEmail(),
                    domain.getMailReplyToName()
                    );
            }
            else
            {
                email.setFrom(domain.getMailReplyToEmail());
            }
        }

        // 5) set the bounce address

        if( domain.getMailBounceAddress() != null )
        {
            email.setBounceAddress(domain.getMailBounceAddress());
        }

        // 6) set the debug mode

        email.setDebug(domain.isMailDebug());

        // 7) set the charset

        if( domain.getMailCharset() != null )
        {
            email.setCharset(domain.getMailCharset());
        }

        // 8) set the mail headers

        email.setHeaders(domain.getHeaders());
    }

    /**
     * Overwrites certain fields of the MimeMessage before sending it.
     *
     * @param domain the domain configuration
     * @param mimeMessage the email to configure
     * @return the modified message
     * @throws MessagingException the configuration failed
     */
    private MimeMessage overwrite( CommonsEmailDomainEntry domain, MimeMessage mimeMessage )
        throws MessagingException
    {
        if(  domain.getHasOverwriteTo() )
        {
            if( domain.getOverwriteTo() != null )
            {
                mimeMessage.setRecipient(MimeMessage.RecipientType.TO, domain.getOverwriteTo() );
            }
        }

        if(  domain.getHasOverwriteCc() )
        {
            if( domain.getOverwriteCc() != null )
            {
                mimeMessage.setRecipient(MimeMessage.RecipientType.CC, domain.getOverwriteCc() );
            }
        }

        if(  domain.getHasOverwriteBcc() )
        {
            if( domain.getOverwriteBcc() != null )
            {
                mimeMessage.setRecipient(MimeMessage.RecipientType.BCC, domain.getOverwriteBcc() );
            }
        }

        return mimeMessage;
    }

    /**
     * Dump the email to be sent to allow easy debugging. This method
     * must not throw an exception.
     *
     * @param mimeMessage the MimeMessage to dump
     * @param type the type of action we are currently executing
     */
    private void dump(MimeMessage mimeMessage, String type)
    {
        if( mimeMessage == null )
        {
            return;
        }

        try
        {
            String name = "CommonsEmailService_" + type + ".eml";
            File dumpFile = new File( this.serviceTempDir, name );
            CommonsEmailUtils.writeMimeMessage(dumpFile, mimeMessage);
        }
        catch (Throwable t)
        {
            String msg = "Unable to dump the MimeMessage";
            this.getLogger().warn(msg,t);
        }
    }

    /**
     * @return Returns the domainList.
     */
    private CommonsEmailDomainEntry [] getDomainList()
    {
        return domainList;
    }

    /**
     * Determines the message id for the TansportEvent
     *
     * @param message the message
     * @return the message id
     * @throws MessagingException the operation failed
     */
    private String getMessageId(Message message) throws MessagingException
    {
        String result;

        if( message instanceof MimeMessage )
        {
            result = ((MimeMessage) message).getMessageID();
        }
        else
        {
            throw new IllegalArgumentException("Don't know how to handle : " + message.getClass());
        }

        return result;
    }

    /**
     * Converts an array of Address to a String.
     *
     * @param addressList the list of addresses
     * @return the printable representation
     */
    private String toString( Address[] addressList )
    {
        if( addressList != null )
        {
            int lim = addressList.length;
            StringBuffer result = new StringBuffer();

            for( int i=0; i<lim; i++ )
            {
                if( addressList[i] instanceof InternetAddress )
                {
                    result.append( ((InternetAddress) addressList[i]).getAddress() );
                }
                else
                {
                    result.append(addressList[i]);
                }

                if( i<lim-1)
                {
                    result.append(';');
                }

            }

            return result.toString();
        }
        else
        {
            return "";
        }
    }

    /**
     * @return is a default domain defined?
     */
    private boolean hasDefaulDomain()
    {
        return ( this.defaultDomainName != null );
    }

    /**
     * Configures the email using a Hashtable containing the
     * content of the email
     *
     * @param email the email to configure
     * @param content the content of the email
     * @throws EmailException configuring failed
     */
    private void setEmailContent( Email email, Hashtable content )
        throws EmailException
    {
        try
        {
            // Set the MAILHOST field.

            String mailHost = (String) content.get(Email.MAIL_HOST);

            if( mailHost != null )
            {
                email.setHostName(mailHost);
                this.getLogger().debug("Overriding the SMTP host name with : " + mailHost);
            }

            // Set the MAILPORT field.

            String mailPort = (String) content.get(Email.MAIL_PORT);

            if( mailPort != null )
            {
                int smtpPort = Integer.parseInt(mailPort);
                email.setSmtpPort(smtpPort);
                this.getLogger().debug("Overriding the SMTP port with : " + smtpPort);
            }

            // Set the MAIL_SMTP_FROM field.

            String mailSmtpFrom = (String) content.get(Email.MAIL_SMTP_FROM);

            if( mailSmtpFrom != null )
            {
                email.setFrom(mailSmtpFrom);
                this.getLogger().debug("Overriding the FROM with : " + mailSmtpFrom);
            }

            // Set the FROM field.

            String fromEmail = (String) content.get(Email.SENDER_EMAIL);
            String fromName = (String) content.get(Email.SENDER_NAME);

            if( fromEmail != null )
            {
                if( fromName != null )
                {
                    email.setFrom(fromEmail,fromName);
                }
                else
                {
                    email.setFrom(fromEmail);
                }
            }

            // Set the TO field.

            String toEmail = (String) content.get(Email.RECEIVER_EMAIL);
            String toName = (String) content.get(Email.RECEIVER_NAME);

            if( toEmail != null )
            {
                ArrayList toRecipients = new ArrayList();

                if( toName != null )
                {
                    toRecipients.add( new InternetAddress(toEmail,toName) );
                }
                else
                {
                    toRecipients.add( new InternetAddress(toEmail) );
                }

                email.setTo(toRecipients);
            }

            // Set the SUBJECT field.

            String subject = (String) content.get(Email.EMAIL_SUBJECT);

            if( subject != null )
            {
                email.setSubject(subject);
            }
            else
            {
                email.setSubject( "no subject available" );
            }

            // set the EMAIL BODY.

            String emailBody = (String) content.get(Email.EMAIL_BODY);

            if( emailBody != null )
            {
                email.setMsg(emailBody);
            }
            else
            {
                email.setMsg("NO MESSAGE");
            }


            // Set the ATTACHMENTS.

            if( email instanceof MultiPartEmail )
            {
                Object attachment;
                MultiPartEmail multiPartEmail = (MultiPartEmail) email;
                Collection attachments = (Collection) content.get(Email.ATTACHMENTS);

                if( attachments != null  )
                {
                    Iterator iter = attachments.iterator();

                    while( iter.hasNext() )
                    {
                        attachment = iter.next();

                        if( attachment instanceof EmailAttachment )
                        {
                            EmailAttachment emailAttachment = (EmailAttachment) attachment;
                            multiPartEmail.attach(emailAttachment);
                        }
                        else if( attachment instanceof DataSource )
                        {
                            DataSource dataSource = (DataSource) attachment;
                            String name = dataSource.getName();
                            String description = dataSource.getName();
                            multiPartEmail.attach( dataSource, name, description );
                        }
                        else
                        {
                            String msg = "Don't know how to handle the attachment : "
                                + attachment.getClass().getName();
                            throw new EmailException(msg);
                        }
                    }
                }
            }
        }
        catch (EmailException e)
        {
            throw e;
        }
        catch (Exception e)
        {
            throw new EmailException(e);
        }
    }

    /**
     * Creates a meaningful log message for a MimeMessage.
     *
     * @param mimeMessage the mesage to log
     * @return log message
     */
    private String getLogMsg( MimeMessage mimeMessage )
    {
        StringBuffer result = new StringBuffer();

        try
        {
            result.append("from='");
            result.append(mimeMessage.getFrom()[0]);
            result.append("', ");

            if( mimeMessage.getRecipients(Message.RecipientType.TO).length > 0 )
            {
                result.append("to='");
                result.append(mimeMessage.getRecipients(Message.RecipientType.TO)[0]);
                result.append("', ");
            }

            result.append("messageID='");
            result.append(mimeMessage.getMessageID());
            result.append("', ");
            result.append("subject='");
            result.append(mimeMessage.getSubject());
            result.append("'");

            return result.toString();
        }
        catch (MessagingException e)
        {
            return "<unknown>";
        }
    }

    /**
     * Creates a valid file name based on the system time, e.g
     * "2005-12-28T143216345-414
     *
     * @param messageId the message id
     * @return a file name
     */
    private synchronized String createMessageFileName(String messageId)
    {
        int currCounter = this.fileNameCounter++ % 1000;
        String counterString = StringUtils.leftPad( "" + currCounter, 3, '0');
        return this.simpleDateFormat.format(new Date()) + "-" + counterString + "." + MIME_MESSAGE_EXTENSION;
    }

    /**
     * Creates an absolute path for the given file based on the application
     * root directory.
     *
     * @param file the relative file
     * @return absolute file
     */
    private File makeAbsolutePath( File file )
    {
        File result = file;

        if( !result.isAbsolute() )
        {
            if( file.isDirectory() )
            {
                result = new File( this.getServiceHomeDir(), file.getPath() );
            }
            else
            {
                String temp = file.getPath() + File.separatorChar + file.getName();
                result = new File( this.getServiceHomeDir(), temp );
            }
        }

        return result;
    }

    /**
     * Writes a MimeMessage to the given directory.
     *
     * @param directory the directory
     * @param mimeMessage the MimeMessage
     */
    protected void writeMimeMessage(File directory, MimeMessage mimeMessage)
    {
        File targetDirectory = this.makeAbsolutePath(directory);

        try
        {
            String messageId = mimeMessage.getMessageID();
            String messageFileName = this.createMessageFileName(messageId);
            targetDirectory.mkdirs();
            File file = new File(targetDirectory, messageFileName);
            CommonsEmailUtils.writeMimeMessage(file, mimeMessage);
            this.getLogger().debug( "Stored the MimeMessage as " + file.getAbsolutePath() );
        }
        catch( Throwable t )
        {
            String msg = "Failed to store the MimeMessage in " + targetDirectory.getAbsolutePath();
            this.getLogger().error(msg,t);
        }
    }

    /**
     * Writes a MimeMessage to the given directory.
     *
     * @param directory the directory
     * @param mimeMessage the MimeMessage
     */
    protected void writeMimeMessage(String directory, MimeMessage mimeMessage)
    {
        this.writeMimeMessage(new File(directory), mimeMessage);
    }

    private SendDeliveryStatusImpl createSendDeliveryStatus(MimeMessage mimeMessage)
        throws MessagingException
    {
        SendDeliveryStatusImpl sendDeliveryStatus = new SendDeliveryStatusImpl(mimeMessage.getAllRecipients());
        sendDeliveryStatus.setMessageId(mimeMessage.getMessageID());
        this.sendDeliveryStatusMap.put(mimeMessage, sendDeliveryStatus);
        return sendDeliveryStatus;
    }

    private SendDeliveryStatusImpl getSendDeliveryStatus( TransportEvent transportEvent )
        throws MessagingException
    {
        MimeMessage mimeMessage = (MimeMessage) transportEvent.getMessage();
        return (SendDeliveryStatusImpl) this.getSendDeliveryStatus(mimeMessage);
    }
}