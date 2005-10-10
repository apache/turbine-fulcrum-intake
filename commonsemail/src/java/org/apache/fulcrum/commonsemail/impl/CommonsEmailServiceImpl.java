package org.apache.fulcrum.commonsemail.impl;

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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Properties;

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

import org.apache.avalon.framework.activity.Disposable;
import org.apache.avalon.framework.activity.Initializable;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.configuration.Reconfigurable;
import org.apache.avalon.framework.context.Context;
import org.apache.avalon.framework.context.ContextException;
import org.apache.avalon.framework.context.Contextualizable;
import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.commons.mail.DefaultAuthenticator;
import org.apache.commons.mail.Email;
import org.apache.commons.mail.EmailAttachment;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.HtmlEmail;
import org.apache.commons.mail.MultiPartEmail;
import org.apache.commons.mail.SimpleEmail;
import org.apache.fulcrum.commonsemail.CommonsEmailService;

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
    implements CommonsEmailService, Contextualizable, Reconfigurable, Initializable, Disposable, 
    	TransportListener, CommonsEmailConstants
{    
    /** the Avalon home directory */
    private File serviceHomeDir;

    /** the Avalon temp directory */
    private File serviceTempDir;

    /** the name of the default domain */
    private String defaultDomainName;
    
    /** the available domains */
    private CommonsEmailDomainEntry[] domainList;
    
    /** is the service instance initialized */
    private volatile boolean isInitialized;
    
    /**
     * Constructor
     */
    public CommonsEmailServiceImpl()
    {       
        // nothing to do
    }

    /////////////////////////////////////////////////////////////////////////
    // Avalon Lifecycle Implementation
    /////////////////////////////////////////////////////////////////////////

    /**
     * @see org.apache.avalon.framework.context.Contextualizable#contextualize(org.apache.avalon.framework.context.Context)
     */
    public void contextualize(Context context) throws ContextException
    {
        this.serviceHomeDir = (File) context.get("urn:avalon:home");
        this.serviceTempDir = (File) context.get("urn:avalon:temp");
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
        Session result = null;
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
        
        properties.setProperty(MAIL_SMTP_FROM,domain.getMailBounceAddress());
        
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
            String msg = "Sending the mail failed";
            this.getLogger().error(msg,e);
            this.dump(mimeMessage,"error");
            throw new EmailException(msg,e);            
        }
        catch(Throwable t)
        {
            String msg = "An internal error occured";
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
        MimeMessage result = null;
        
        // get the configuration of this domain
        
        CommonsEmailDomainEntry domain = this.getDomain(domainName);
        
        //	update the MimeMessage based on the domain configuration
        
        result = this.updateMimeMessage(domain, mimeMessage);
        
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
        MimeMessage result = null;
        
        // get the configuration of this domain
        
        CommonsEmailDomainEntry domain = this.getDomain(domainName);
        
        //	update the MimeMessage based on the domain configuration
        
        result = this.updateMimeMessage(domain, mimeMessage);
        
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
         MimeMessage result = null;
         
         // determine the domain name
         
         if( ( mimeMessage.getFrom() == null ) || ( mimeMessage.getFrom().length == 0 ) )
         {
             throw new MessagingException("No from address defined - unable to determine a domain configuration");
         }
         
         InternetAddress fromAddress = (InternetAddress) mimeMessage.getFrom()[0];
         String domainName = fromAddress.getAddress();
         
         // get the configuration of this domain
         
         CommonsEmailDomainEntry domain = this.getDomain(domainName);
         
         //	update the MimeMessage based on the domain configuration
         
         result = this.updateMimeMessage(domain, mimeMessage);
         
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
	        this.getLogger().info(
	            "The MimeMessage "
	            + this.getMessageID(transportEvent)
	            + " was successfully delivered to the following recipients : "
	            + this.toString(transportEvent.getValidSentAddresses())
	            );
        }
    }
    
    /**
     * @see javax.mail.event.TransportListener#messageNotDelivered(javax.mail.event.TransportEvent)
     */
    public void messageNotDelivered(TransportEvent transportEvent)
    {
        if( this.isInitialized() )
        {
	        this.getLogger().error(
	            "The MimeMessage "
	            + this.getMessageID(transportEvent)
	            + " was not delivered to any recipient due to following invalid addresses : "
	            + this.toString(transportEvent.getInvalidAddresses())
	            );
        }
    }
    
    /**
     * @see javax.mail.event.TransportListener#messagePartiallyDelivered(javax.mail.event.TransportEvent)
     */
    public void messagePartiallyDelivered(TransportEvent transportEvent)
    {
        if( this.isInitialized() )
        {
            this.getLogger().warn(
                "The MimeMessage "
                + this.getMessageID(transportEvent)
                + "was only partially delivered "
                );
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
     */
    private MimeMessage buildMimeMessage( Email email )
    	throws EmailException, MessagingException
    {
        MimeMessage result = null;
        
        email.buildMimeMessage();
        result = email.getMimeMessage();
                
        return result;            
    }

    /**
     * Updates the MimeMessage based on the domain settings. The implementation 
     * calls the onPostProcessMimeMessage() method which can be overwritten by a 
     * derived service implementation.
     * 
     * @param domain the domain configuration
     * @param mimeMessage the MimeMessage to be updated
     * @return the resulting MimeMessage
     */
    private MimeMessage updateMimeMessage( CommonsEmailDomainEntry domain, MimeMessage mimeMessage )
    	throws MessagingException
    {
        MimeMessage result = null;

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
     * @param sesssion the mail sessoin
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
        
        // dump the MimeMessage to be sent
        
        if( domain.isMailDump() )
        {
            this.dump(mimeMessage,"send");
        }
 
        if( domain.isMailDoNotSend() == false )
        {        
            try
            {
                long startTime = System.currentTimeMillis();
                
                Transport transport = session.getTransport("smtp");
                
                transport.addTransportListener(this);
                transport.connect();	      
                
                if( (recipients == null) || (recipients.length == 0) )
                {
                    transport.sendMessage(mimeMessage, mimeMessage.getAllRecipients());
                }
                else
                {
                    transport.sendMessage(mimeMessage, recipients);
                }
                
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
            catch (RuntimeException e)
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
        }
        else
        {
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
        try
        {
            Configuration conf = domain.getOnSuccessHookConfiguration();
            String directoryName = conf.getChild("directory").getValue(this.getServiceTempDir().getAbsolutePath());
            
            String messageId = mimeMessage.getMessageID();
            String messageFileName = this.createMessageFileName(messageId);            
            File directory = this.makeAbsolutePath(directoryName);
            directory.mkdirs();
            
            File file = new File( directory, messageFileName);                      
            FileOutputStream fos = new FileOutputStream(file);
            mimeMessage.writeTo(fos);
            fos.flush();
            fos.close();            
         
            this.getLogger().info( "Stored the MimeMessage as " + file.getAbsolutePath() );
        }
        catch( Throwable t )
        {            
            String msg = "Failed to store the MimeMessage in " + this.serviceTempDir.getAbsolutePath();
            this.getLogger().error(msg,t);
        }            
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
        try
        {
            Configuration conf = domain.getOnFailureHookConfiguration();
            String directoryName = conf.getChild("directory").getValue(this.getServiceTempDir().getAbsolutePath());
                
            String messageId = mimeMessage.getMessageID();
            String messageFileName = this.createMessageFileName(messageId);
            File directory = this.makeAbsolutePath(directoryName);     
            directory.mkdirs();
            
            File file = new File( directory, messageFileName);            
            FileOutputStream fos = new FileOutputStream(file);
            mimeMessage.writeTo(fos);
            fos.flush();
            fos.close();            
         
            this.getLogger().info( "Stored the MimeMessage as " + file.getAbsolutePath() );
        }
        catch( Throwable t )
        {            
            String msg = "Failed to store the MimeMessage in " + this.serviceTempDir.getAbsolutePath();
            this.getLogger().error(msg,t);
        }            
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
        try
        {
            Configuration conf = domain.getOnNotSendHookConfiguration();
            String directoryName = conf.getChild("directory").getValue(this.getServiceTempDir().getAbsolutePath());
                
            String messageId = mimeMessage.getMessageID();
            String messageFileName = this.createMessageFileName(messageId);
            File directory = this.makeAbsolutePath(directoryName);         
            directory.mkdirs();
            
            File file = new File( directory, messageFileName);            
            FileOutputStream fos = new FileOutputStream(file);
            mimeMessage.writeTo(fos);
            fos.flush();
            fos.close();            
         
            this.getLogger().info( "Stored the MimeMessage as " + file.getAbsolutePath() );
        }
        catch( Throwable t )
        {            
            String msg = "Failed to store the MimeMessage in " + this.serviceTempDir.getAbsolutePath();
            this.getLogger().error(msg,t);
        }            
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
        CommonsEmailDomainEntry result = null;
        
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
     * @param email the email to configure
     * @throws EmailException the configuration failed
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
        FileOutputStream fos = null;        
        
        if( mimeMessage == null )
        {
            return;
        }
                
        try
        {
            String name = "CommonsEmailService_" + type + ".eml";
            File dumpFile = new File( this.serviceTempDir, name );
            fos = new FileOutputStream(dumpFile);
            mimeMessage.writeTo(fos);
            fos.flush();
        }
        catch (Throwable t)
        {
            String msg = "Unable to dump the MimeMessage";
            this.getLogger().warn(msg,t);
        }
        finally
        {
            if( fos != null )
            {
                try
                {
                    fos.close();
                }
                catch (IOException ioe)
                {
                    String msg = "Closing the FileOutputStream failed";
                    this.getLogger().warn(msg,ioe);
                }
            }
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
     * @param transportEvent the transport event
     * @return the message id 
     */
    private String getMessageID( TransportEvent transportEvent)
    {
        String result = "<unknown>";
        
        if( transportEvent.getMessage() instanceof MimeMessage )
        {
            try
            {
                result = ((MimeMessage) transportEvent.getMessage()).getMessageID();
            }
            catch (MessagingException e)
            {
                String msg = "Unable to retrieve messageID";
                this.getLogger().warn(msg,e);
            }
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
        return (this.defaultDomainName != null ? true : false );
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
                MultiPartEmail multiPartEmail = (MultiPartEmail) email;
                Object attachment = null;
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
     * Creates a meaningful log message for a MimeMessage 
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
     * Creates a valid file name based on messageId.
     * 
     * @param messageId the message id
     * @return a file name
     */
    private String createMessageFileName(String messageId)
    {
        String result = messageId;
        
        // skip leading '<' and terminating '>' of a JavaMail message id
        
        result = result.replace('<',' ');
        result = result.replace('>',' ');
        result = result.trim();
       
        // append ".eml"
        
        result = result + ".eml";
        
        return result;
    }
    
    /**
     * Creates an absolute path for the given filename based on the application
     * root directory.
     * 
     * @param fileName the file name
     * @return abolsute file
     */
    private File makeAbsolutePath( String fileName )
    {
        return this.makeAbsolutePath( new File(fileName) );
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
        
        if( result.isAbsolute() == false )
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
}