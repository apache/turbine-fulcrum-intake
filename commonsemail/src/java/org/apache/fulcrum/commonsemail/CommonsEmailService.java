package org.apache.fulcrum.commonsemail;

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

import java.util.Hashtable;

import javax.mail.Address;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.MimeMessage;

import org.apache.commons.mail.Email;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.HtmlEmail;
import org.apache.commons.mail.MultiPartEmail;
import org.apache.commons.mail.SimpleEmail;

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

public interface CommonsEmailService
{
    /**
     * Determines if any email for the given domain name will
     * be sent or silently consumed by the service without
     * delivering it.
     *
     * @param domainName the domain name
     * @return true if the email will not be sent by the service
     */
    boolean isMailDoNotSend(String domainName);

    /**
     * Factory method to create a mail session based on the domain configuration.
     *
     * @param domainName the domain name
     * @return a mail session
     */
    Session createSmtpSession(String domainName);

    /**
     * Factory method to create a mail session based on the domain configuration
     * and a user-supplied username and password. We assume that SMTP AUTH is
     * used.
     *
     * @param domainName the domain name
     * @param username the user name used for SMTP authentication
     * @param password the password used for SMTP authentication
     * @return a mail session
     */
    Session createSmtpSession(String domainName, String username, String password);

    /**
     * Factory method for creating a SimpleEmail with fully
     * configured mail session based on the domain configuration.
     *
     * @param domainName the sender of the email
     * @return a SimpleEmail
     * @throws EmailException creation failed
     */
    SimpleEmail createSimpleEmail(String domainName)
    	throws EmailException;

    /**
     * Factory method for creating a SimpleEmail
     *
     * @param domainName the sender of the email
     * @param content the content of the email
     * @return a SimpleEmail
     * @throws EmailException creation failed
     */
    SimpleEmail createSimpleEmail(String domainName, Hashtable content)
    	throws EmailException;

    /**
     * Factory method for creating a MultiPartEmail with fully
     * configured mail session based on the domain configuration.
     *
     * @param domainName the sender of the email
     * @return a MultiPartEmail
     * @throws EmailException creation failed
     */
    MultiPartEmail createMultiPartEmail(String domainName)
    	throws EmailException;

    /**
     * Factory method for creating a MultiPartEmail
     *
     * @param domainName the sender of the email
     * @param content the content of the email
     * @return a MultiPartEmail
     * @throws EmailException creation failed
     */
    MultiPartEmail createMultiPartEmail(String domainName, Hashtable content)
    	throws EmailException;

    /**
     * Factory method for creating a HtmlEmail with fully
     * configured mail session based on the domain configuration.
     *
     * @param domainName the sender of the email
     * @return a MultiPartEmail
     * @throws EmailException creation failed
     */
    HtmlEmail createHtmlEmail(String domainName)
    	throws EmailException;

    /**
     * Factory method for creating a HtmlEmail
     *
     * @param domainName the sender of the email
     * @param content the content of the email
     * @return a MultiPartEmail
     * @throws EmailException creation failed
     */
    HtmlEmail createHtmlEmail(String domainName, Hashtable content)
    	throws EmailException;

    /**
     * Sends an email using the service instead of calling send()
     * directly on the Email. This allows to overwrite the receivers
     * of the email as an additional security measure for sending
     * thousands of emails using real-world email addresses.
     *
     * @param domainName the sender of the email
     * @param email the email to be sent
     * @return the MimeMessage being sent
     * @throws EmailException sending the email failed
     */
    MimeMessage send(String domainName, Email email)
    	throws EmailException;

    /**
     * Sends an email using the service instead of calling send()
     * directly on the Email. The implementation uses the
     * the from email address as domain name.
     *
     * @param email the email to be sent
     * @return the MimeMessage being sent
     * @throws EmailException sending the email failed
     */
    MimeMessage send(Email email) throws EmailException;

    /**
     * Sends a MimeMessage using the service instead of calling
     * Transport.send(). This allows to overwrite the receivers
     * of the email as an additional security measure for sending
     * thousands of emails using real-world email addresses.
     *
     * @param domainName the sender of the email
     * @param session the email session
     * @param mimeMessage the email to be sent
     * @return the MimeMessage being sent
     * @throws MessagingException sending the email failed
     */
    MimeMessage send(String domainName, Session session, MimeMessage mimeMessage)
    	throws MessagingException;

    /**
     * Sends a MimeMessage using the service instead of calling
     * Transport.send(). This allows to overwrite the receivers
     * of the email as an additional security measure for sending
     * thousands of emails using real-world email addresses.
     *
     * @param domainName the sender of the email
     * @param session the email session
     * @param mimeMessage the email to be sent
     * @param recipients the list of recipients
     * @return the MimeMessage being sent
     * @throws MessagingException sending the email failed
     */
    MimeMessage send(String domainName, Session session, MimeMessage mimeMessage, Address[] recipients)
    	throws MessagingException;

    /**
     * Sends a MimeMessage using the service instead of calling
     * Transport.send(). This allows to overwrite the receivers
     * of the email as an additional security measure for sending
     * thousands of emails using real-world email addresses.
     *
     * @param session the email session
     * @param mimeMessage the email to be sent
     * @return the MimeMessage being sent
     * @throws MessagingException sending the email failed
     */
    MimeMessage send(Session session, MimeMessage mimeMessage)
    	throws MessagingException;
}
