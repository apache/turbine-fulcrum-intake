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

import javax.mail.Address;

/**
 * Allows to access the send delivery status of a previously sent
 * email.
 */
public interface SendDeliveryStatus
{
    /**
     * Returns the message of of the corresponding email.
     *
     * @return mime message id
     */
    String getMessageId();

    /**
     * Returns true only if ALL recipients were accepted by the
     * mailserver, i.e. everything is okay.
     *
     * @return true if message sending was successful
     */
    boolean hasSucceeded();

    /**
     * Get all of the original recipients from the mime message.
     *
     * @return list of email addresses
     */
    Address[] getAllRecipients();

    /**
     * The list of mail addresses accepted by the mailserver.
     *
     * @return list of email addresses
     */
    Address[] getValidSentAddresses();

    /**
     * The list of mail addresses rejected by the mailserver.
     *
     * @return list of email addresses
     */
    Address[] getValidUnsentAddresses();

    /**
     * The list of invalid mail addresses.
     *
     * @return list of email addresses
     */        
    Address[] getInvalidAddresses();

    /**
     * Return the number of all recipients.
     *
     * @return overall number of recipients
     */
    int size();
}