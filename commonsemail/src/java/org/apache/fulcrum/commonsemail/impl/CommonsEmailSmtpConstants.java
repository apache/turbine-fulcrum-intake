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


/**
 * Contains all SMTP releated session properties for javamail-1.3.3.
 * 
 * @author <a href="mailto:siegfried.goeschl@it20one.at">Siegfried Goeschl</a>
 */

public interface CommonsEmailSmtpConstants
{
    /** Default user name for SMTP */
    public static final String MAIL_SMTP_USER = "mail.smtp.user";

    /** The SMTP server to connect to */
    public static final String MAIL_SMTP_HOST = "mail.smtp.host";

    /** 	
     * The SMTP server port to connect to, if the connect() method 
     * doesn't explicitly specify one. Defaults to 25 
     */
    public static final String MAIL_SMTP_PORT = "mail.smtp.port";

    /** 
     * Socket connection timeout value in milliseconds. Default is 
     * infinite timeout. 
     */
    public static final String MAIL_SMTP_CONNECTIONTIMEOUT = "mail.smtp.connectiontimeout";

    /** 
     * Socket I/O timeout value in milliseconds. Default is infinite timeout. 
     */
    public static final String MAIL_SMTP_TIMEOUT = "mail.smtp.timeout";
    
    /** Email address to use for SMTP MAIL command */
    public static final String MAIL_SMTP_FROM = "mail.smtp.from";

    /** Local host name used in the SMTP HELO or EHLO command */
    public static final String MAIL_SMTP_LOCALHOST = "mail.smtp.localhost";

    /** Local address (host name) to bind to when creating the SMTP socket */
    public static final String MAIL_SMTP_LOCALADDRESS = "mail.smtp.localaddress";

    /** Local port number to bind to when creating the SMTP socket */
    public static final String MAIL_SMTP_LOCALPORT = "mail.smtp.localport";

    /** If false, do not attempt to sign on with the EHLO command */
    public static final String MAIL_SMTP_EHLO = "mail.smtp.ehlo";

    /** If true, attempt to authenticate the user using the AUTH command */
    public static final String MAIL_SMTP_AUTH = "mail.smtp.auth";

    /**  The submitter to use in the AUTH tag in the MAIL FROM command */
    public static final String MAIL_SMTP_SUBMITTER = "mail.smtp.submitter";

    /** 
     * The NOTIFY option to the RCPT command. Either NEVER, or some combination of 
     * SUCCESS, FAILURE, and DELAY (separated by commas). 
     */
    public static final String MAIL_SMTP_DSN_NOTIFY = "mail.smtp.dsn.notify";

    /** The RET option to the MAIL command. Either FULL or HDRS. */
    public static final String MAIL_SMTP_DSN_RET = "mail.smtp.dsn.ret";

    /**
     * If set to true, and the server supports the 8BITMIME extension, 
     * text parts of messages that use the "quoted-printable" or 
     * "base64" encodings are converted to use "8bit" encoding if they 
     * follow the RFC2045 rules for 8bit text.*/
    public static final String MAIL_SMTP_ALLOW8BITMIME = "mail.smtp.allow8bitmime";

    /** 
     * If set to true, and a message has some valid and some invalid 
     * addresses, send the message anyway, reporting the partial failure
     * with a SendFailedException. If set to false (the default), the 
     * message is not sent to any of the recipients if there is an 
     * invalid recipient address. */
    public static final String MAIL_SMTP_SENTPARTIAL = "mail.smtp.sendpartial";

    /** The realm to use with DIGEST-MD5 authentication. */
    public static final String MAIL_SMTP_SASL_REALM = "mail.smtp.sasl.realm";

    /** If set to true, causes the transport to wait for the response to the QUIT command */
    public static final String MAIL_SMTP_QUITWAIT = "mail.smtp.quitwait";

    /**
     * If set to true, causes the transport to include an SMTPAddressSucceededException 
     * for each address that is successful. Note also that this will cause 
     * a SendFailedException to be thrown from the sendMessage method of 
     * SMTPTransport even if all addresses were correct and the message 
     * was sent successfully.
     */
    public static final String MAIL_SMTP_REPORTSUCCESS = "mail.smtp.reportsuccess";

    /** 
     * If set, specifies the name of a class that implements the 
     * javax.net.SocketFactory interface. This class will be used to create 
     * SMTP sockets. 
     */
    public static final String MAIL_SMTP_SOCKETFACTORY_CLASS = "mail.smtp.socketFactory.class";

    /** 
     *  If set to true, failure to create a socket using the specified
     *  socket factory class will cause the socket to be created 
     *  using the java.net.Socket class. Defaults to true 
     */
    public static final String MAIL_SMTP_SOCKETFACTORY_FALLBACK = "mail.smtp.socketFactory.fallback";

    /** 
     *  Specifies the port to connect to when using the specified socket factory. 
     * If not set, the default port will be used.
     */
    public static final String MAIL_SMTP_SOCKETFACTORY_PORT = "mail.smtp.socketFactory.port";

    /**
     * Extension string to append to the MAIL command. The extension string 
     * can be used to specify standard SMTP service extensions as well 
     * as vendor-specific extensions. Typically the application should use 
     * the SMTPTransport method supportsExtension to verify that the server 
     * supports the desired service extension. See RFC 1869 and other RFCs 
     * that define specific extensions.  
     * */
    public static final String MAIL_SMTP_MAILEXTENSION = "mail.smtp.mailextension";
}