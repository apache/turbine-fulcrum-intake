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

import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import java.util.Hashtable;
import java.util.Properties;

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

public class CommonsEmailDomainEntry
{
    /** use SMTPAUTH */
    public static final String AUTH_SMTP = "smtpauth";
    
    /** use PopBeforeSmtp */
    public static final String AUTH_POPBEFORESMTP = "popbeforesmtp";
    
    /** use no authentication at all */
    public static final String AUTH_NONE = "none";
    
    /** the name of the domain */
    private String domainName;

    /** the match field of the the domain */
    private String domainMatch;

    /** turn the diagnostic features on */
    private boolean mailDump;
    
    /** the bounce address */
    private String mailBounceAddress;
    
    /** debug mode for mail session */
    private boolean mailDebug;
    
    /** the charset being used for the mail */
    private String mailCharset;
    
    /** the address of the mail server */
    private String mailSmtpHost;

    /** the port of the mail server */
    private int mailSmtpPort;
    
    /** socket connection timeout value in milliseconds */
    private int mailSmtpConnectionTimeout;
    
	/** socket I/O timeout value in millisecond */
    private int mailSmtpTimeout;
    
    /** if the message has some valid and some invalid addresses, send the message anyway */
    private boolean mailSmtpSendPartial;
    
    /** the email address of the sender */
    private String mailFromEmail;

    /** the email address of the sender */
    private String mailFromName;

    /** the email address of the replyTo */
    private String mailReplyToEmail;

    /** the email address of the replyTo */
    private String mailReplyToName;
    
    /** the type of authentication */
    private String authType;
    
    /** the username for authentication */
    private String authUsername;
    
    /** the password for authentication */
    private String authPassword;
    
    /** the POP server being used for popbeforesmtp */    
    private String authPopHost;
    
    /** the mail headers being used */
    private Properties headers;
    
    /** are we overwriting the TO recipients */
    private boolean hasOverwriteTo;
    
    /** the to address to be overwritten during send() */
    private InternetAddress overwriteTo;

    /** are we overwriting the CC recipients */
    private boolean hasOverwriteCc;

    /** the cc address to be overwritten during send() */
    private InternetAddress overwriteCc;

    /** are we overwriting the BCC recipients */
    private boolean hasOverwriteBcc;

    /** the bcc address to be overwritten during send() */
    private InternetAddress overwriteBcc;
        
    /** do we skip the sending */
    private boolean mailDoNotSend;
    
    /** is an application hook for onSuccess defined */
    private boolean hasOnSuccessHook;

    /** is an application hook for onFailure defined */
    private boolean hasOnFailureHook;

    /** is an application hook for onNotSend defined */
    private boolean hasOnNotSendHook;

    /** the configuration of the success hook */
    private Configuration onSuccessHookConfiguration;
    
    /** the configuration of the success hook */
    private Configuration onFailureHookConfiguration;    

    /** the configuration of the notSend hook */
    private Configuration onNotSendHookConfiguration;    

    /**
     * Constructor. 
     */    
    public CommonsEmailDomainEntry()
    {
        this.headers = new Properties();
        this.hasOverwriteTo = false;
        this.hasOverwriteCc = false;
        this.hasOverwriteBcc = false;
    }
    
    /**
     * Initialize this instance.
     * 
     * @param conf the domain configuration 
     * @return the fully configured instance
     * @throws ConfigurationException the configuration failed
     */
    public CommonsEmailDomainEntry initialize( Configuration conf )
    	throws ConfigurationException
    {        
        // read the basic parameters
        
        this.domainName = conf.getChild("domainName").getValue();     
        this.domainMatch = conf.getChild("domainName").getValue(this.domainName);
        
        this.mailBounceAddress = conf.getChild("mailBounceAddress").getValue(null);
        this.mailDebug = conf.getChild("mailDebug").getValueAsBoolean(false);
        this.mailCharset = conf.getChild("mailCharset").getValue(null);
        
        this.mailSmtpHost = conf.getChild("mailSmtpHost").getValue(
            System.getProperty("mail.smtp.host","localhost")
            );
        
        // determine SMTP port either from the configuration or from the system properties
        
        this.mailSmtpPort = conf.getChild("mailSmtpPort").getValueAsInteger(0);    
        
        if( this.mailSmtpPort == 0 )
        {
            this.mailSmtpPort = Integer.parseInt(
                System.getProperty("mail.smtp.port","25")
                );
        }
        
        this.mailSmtpConnectionTimeout = conf.getChild("mailSmtpConnectionTimeout").getValueAsInteger(Integer.MAX_VALUE);
        this.mailSmtpTimeout = conf.getChild("mailSmtpConnectionTimeout").getValueAsInteger(Integer.MAX_VALUE);
        this.mailSmtpSendPartial = conf.getChild("mailSmtpSendPartial").getValueAsBoolean(false);
        this.mailFromEmail = conf.getChild("mailFromEmail").getValue(null);
        this.mailFromName = conf.getChild("mailFromName").getValue(null);
        this.mailReplyToEmail = conf.getChild("mailReplyToEmail").getValue(this.mailFromEmail);
        this.mailReplyToName = conf.getChild("mailReplyToName").getValue(this.mailFromName);         
        this.mailDump = conf.getChild("mailDump").getValueAsBoolean(false);
        this.mailDoNotSend = conf.getChild("mailDoNotSend").getValueAsBoolean(false);
        
        // parse the authentication related parameters
        
        this.authType = conf.getChild("authentication").getChild("type").getValue(AUTH_NONE);
        
        if( this.hasAuthentication() )
        {
	        this.authPopHost = conf.getChild("authentication").getChild("popHost").getValue(this.mailSmtpHost);
	        this.authUsername = conf.getChild("authentication").getChild("username").getValue();
	        this.authPassword = conf.getChild("authentication").getChild("password").getValue();
        }
        
        // parse the email headers
        
        Configuration[] headersConf = conf.getChild("headers").getChildren("property");
        
        for( int i=0; i<headersConf.length; i++ )
        {
            String name = headersConf[i].getAttribute("name");
            String value = headersConf[i].getValue();
            this.headers.setProperty(name,value);            
        }
                
        // parse the overwrites
        
        String internetAddress = null;
        Configuration overwritesConf = conf.getChild("overwrites");
        
        try
        {
            if( overwritesConf.getChild("mailToEmail",false) != null )
            {
                this.hasOverwriteTo = true;
                internetAddress = overwritesConf.getChild("mailToEmail").getValue(null);
                if( internetAddress != null )
                {
                    this.overwriteTo = new InternetAddress(internetAddress);
                }
            }

            if( overwritesConf.getChild("mailCcEmail",false) != null )
            {
                this.hasOverwriteCc = true;
                internetAddress = overwritesConf.getChild("mailCcEmail").getValue(null);
                if( internetAddress != null )
                {
                    this.overwriteCc = new InternetAddress(internetAddress);
                }
            }

            if( overwritesConf.getChild("mailBccEmail",false) != null )
            {
                this.hasOverwriteBcc = true;
                internetAddress = overwritesConf.getChild("mailBccEmail").getValue(null);
                if( internetAddress != null )
                {
                    this.overwriteBcc = new InternetAddress(internetAddress);
                }
            }
        }
        catch (AddressException e)
        {
            String msg = "Unable to parse " + internetAddress;            
            throw new ConfigurationException(msg,e);
        }
        
        // parse the application hooks
        
        Configuration hookConf = conf.getChild("hooks");
        
        this.hasOnSuccessHook = hookConf.getChild("onSuccess").getAttributeAsBoolean("enabled", false);
        
        if( this.hasOnSuccessHook() )
        {
            this.onSuccessHookConfiguration = hookConf.getChild("onSuccess");
        }
        
        this.hasOnFailureHook = hookConf.getChild("onFailure").getAttributeAsBoolean("enabled", false);

        if( this.hasOnFailureHook())
        {
            this.onFailureHookConfiguration = hookConf.getChild("onFailure");
        }
        
        this.hasOnNotSendHook = hookConf.getChild("onNotSend").getAttributeAsBoolean("enabled", false);

        if( this.hasOnNotSendHook())
        {
            this.onNotSendHookConfiguration = hookConf.getChild("onNotSend");
        }
        
        return this;
    }
 
    /**
     * @see java.lang.Object#toString()
     */
    public String toString()
    {
        StringBuffer result = new StringBuffer();
        
        result.append(getClass().getName() + "@" + Integer.toHexString(hashCode()));
        
        result.append('[');
        result.append("authPopHost=" + this.getAuthPopHost());
        result.append(',');
        result.append("authType=" + this.getAuthType());
        result.append(',');
        result.append("authUsername=" + this.getAuthUsername());
        result.append(',');
        result.append("domainMatch=" + this.getDomainMatch());
        result.append(',');
        result.append("domainName=" + this.getDomainName());
        result.append(',');
        result.append("hasOverwriteCc=" + this.getHasOverwriteBcc());                
        result.append(',');
        result.append("hasOverwriteBcc=" + this.getHasOverwriteBcc());                
        result.append(',');
        result.append("hasOverwriteTo=" + this.getHasOverwriteTo());                
        result.append(',');
        result.append("headers=" + this.getHeaders());                
        result.append(',');
        result.append("mailBounceAddress=" + this.getMailBounceAddress());                
        result.append(',');
        result.append("mailCharset=" + this.getMailCharset());                
        result.append(',');
        result.append("mailDebug=" + this.mailDebug);                
        result.append(',');
        result.append("mailDump=" + this.mailDump);                
        result.append(',');
        result.append("mailFromEmail=" + this.getMailFromEmail());                
        result.append(',');
        result.append("mailFromName=" + this.getMailFromName());                
        result.append(',');
        result.append("mailReplyToEmail=" + this.getMailReplyToEmail());                
        result.append(',');
        result.append("mailReplyToName=" + this.getMailReplyToName());                
        result.append(',');
        result.append("mailSmtpHost=" + this.getMailSmtpHost());                
        result.append(',');
        result.append("mailSmtpPort=" + this.getMailSmtpPort());                
        result.append(',');
        result.append("mailSmtpConnectionTimeout=" + this.getMailSmtpConnectionTimeout());                
        result.append(',');
        result.append("mailSmtpTimeout=" + this.getMailSmtpTimeout());                
        result.append(',');
        result.append("mailSmtpSendPartial=" + this.isMailSmtpSendPartial());                
        result.append(',');
        result.append("overwriteBcc=" + this.getOverwriteBcc());                
        result.append(',');
        result.append("overwriteCc=" + this.getOverwriteCc());                
        result.append(',');
        result.append("overwriteTo=" + this.getOverwriteTo());                                
        result.append(']');
        
        return result.toString();                

    }
    
    /**
     * @return Returns the authPassword.
     */
    public String getAuthPassword()
    {
        return authPassword;
    }
    
    /**
     * @return Returns the authPopHost.
     */
    public String getAuthPopHost()
    {
        return authPopHost;
    }
    
    /**
     * @return Returns the authType.
     */
    public String getAuthType()
    {
        return authType;
    }
    
    /**
     * @return Returns the authUsername.
     */
    public String getAuthUsername()
    {
        return authUsername;
    }
    
    /**
     * @return Returns the mailBounceAddress.
     */
    public String getMailBounceAddress()
    {
        return mailBounceAddress;
    }
    
    /**
     * @return Returns the domainName.
     */
    public String getDomainName()
    {
        return domainName;
    }
        
    /**
     * @return Returns the domainMatch.
     */
    public String getDomainMatch()
    {
        return domainMatch;
    }
    
    /**
     * @return Returns the headers.
     */
    public Hashtable getHeaders()
    {
        return headers;
    }
        
    /**
     * @return Returns the overwriteBcc.
     */
    public InternetAddress getOverwriteBcc()
    {
        return overwriteBcc;
    }
    
    /**
     * @return Returns the overwriteCc.
     */
    public InternetAddress getOverwriteCc()
    {
        return overwriteCc;
    }
    
    /**
     * @return Returns the overwriteTo.
     */
    public InternetAddress getOverwriteTo()
    {
        return overwriteTo;
    }
        
    /**
     * @return Returns the mailCharset.
     */
    public String getMailCharset()
    {
        return mailCharset;
    }
    
    /**
     * @return Returns the mailDebug.
     */
    public boolean isMailDebug()
    {
        return mailDebug;
    }
    
    /**
     * @return Returns the mailFromEmail.
     */
    public String getMailFromEmail()
    {
        return mailFromEmail;
    }
    
    /**
     * @return Returns the mailFromName.
     */
    public String getMailFromName()
    {
        return mailFromName;
    }
        
    /**
     * @return Returns the mailReplyToEmail.
     */
    public String getMailReplyToEmail()
    {
        return mailReplyToEmail;
    }
    
    /**
     * @return Returns the mailReplyToName.
     */
    public String getMailReplyToName()
    {
        return mailReplyToName;
    }
    
    /**
     * @return Returns the mailSmtpHost.
     */
    public String getMailSmtpHost()
    {
        return mailSmtpHost;
    }
    
    /**
     * @return Returns the mailSmtpPort.
     */
    public int getMailSmtpPort()
    {
        return mailSmtpPort;
    }
    
    /**
     * @return Returns the mailSmtpConnectionTimeout.
     */
    public int getMailSmtpConnectionTimeout()
    {
        return mailSmtpConnectionTimeout;
    }
    
    /**
     * @return Returns the mailSmtpTimeout.
     */
    public int getMailSmtpTimeout()
    {
        return mailSmtpTimeout;
    }
    
    /**
     * @return Returns the mailSmtpSendPartial.
     */
    public boolean isMailSmtpSendPartial()
    {
        return mailSmtpSendPartial;
    }
    
    /**
     * @return Is any type of authentication used for this domain?
     */
    public boolean hasAuthentication()
    {
        return !this.authType.equals( AUTH_NONE );
    }

    /**
     * @return Is SMTP authentication used for this domain?
     */
    public boolean hasSmtpAuthentication()
    {
        return this.authType.equals( AUTH_SMTP );
    }

    /**
     * @return Is PopBeforeSMTP authentication used for this domain?
     */
    public boolean hasPopBeforeSmtpAuthentication()
    {
        return this.authType.equals( AUTH_POPBEFORESMTP );
    }
    
    /**
     * @return Returns the mailDump.
     */
    public boolean isMailDump()
    {
        return mailDump;
    }
        
    /**
     * @return Returns the hasOverwriteBcc.
     */
    public boolean getHasOverwriteBcc()
    {
        return hasOverwriteBcc;
    }
    
    /**
     * @return Returns the hasOverwriteCc.
     */
    public boolean getHasOverwriteCc()
    {
        return hasOverwriteCc;
    }
    /**
     * @return Returns the hasOverwriteTo.
     */
    public boolean getHasOverwriteTo()
    {
        return hasOverwriteTo;
    }
    
    /**
     * @return Returns the mailDoNotSend.
     */
    public boolean isMailDoNotSend()
    {
        return mailDoNotSend;
    } 
        
    /**
     * @return Returns the hasOnFailureHook.
     */
    public boolean hasOnFailureHook()
    {
        return hasOnFailureHook;
    }
    
    /**
     * @return Returns the hasOnSuccessHook.
     */
    public boolean hasOnSuccessHook()
    {
        return hasOnSuccessHook;
    }
    
    /**
     * @return Returns the onFailureHookConfiguration.
     */
    public Configuration getOnFailureHookConfiguration()
    {
        return onFailureHookConfiguration;
    }
    
    /**
     * @return Returns the onSuccessHookConfiguration.
     */
    public Configuration getOnSuccessHookConfiguration()
    {
        return onSuccessHookConfiguration;
    }
        
    /**
     * @return Returns the hasOnNotSendHook.
     */
    public boolean hasOnNotSendHook()
    {
        return hasOnNotSendHook;
    }
    
    /**
     * @return Returns the onNotSendHookConfiguration.
     */
    public Configuration getOnNotSendHookConfiguration()
    {
        return onNotSendHookConfiguration;
    }
}