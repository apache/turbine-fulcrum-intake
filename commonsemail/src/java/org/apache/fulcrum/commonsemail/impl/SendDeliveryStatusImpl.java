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

import org.apache.fulcrum.commonsemail.SendDeliveryStatus;

import javax.mail.Address;
import javax.mail.event.TransportEvent;
import java.util.HashSet;
import java.util.Set;

/**
 * Keep track of the delivery status of a single email. Please note
 * that using a set is a deliberate decision - assuming that the same
 * address shows up in 'to' and 'cc' the email would be sent only
 * once.
 *
 * @author <a href="mailto:siegfried.goeschl@it20one.at">Siegfried Goeschl</a>
 */

public class SendDeliveryStatusImpl implements SendDeliveryStatus
{
    /** the message id from the email */
    private String messageId;

    /** the original list of recipients */
    private HashSet allRecipients;

    /** the list of valid addresses where an email is going to be delivered */
    private Set validSentAddressList;

    /** the list of valid addresses where an email is NOT going to be delivered */
    private Set validUnsentAddressList;

    /** the list of invalid addresses */
    private Set invalidAddressList;

    /**
     * Constructor.
     *
     * @param allRecipients the list of all recipients
     */
    SendDeliveryStatusImpl(Address[] allRecipients)
    {
        this.allRecipients = new HashSet();
        this.addAddresses( this.allRecipients, allRecipients );
        
        this.validSentAddressList = new HashSet();
        this.validUnsentAddressList = new HashSet();
        this.invalidAddressList = new HashSet();
    }

    /** @see org.apache.fulcrum.commonsemail.SendDeliveryStatus#hasSucceeded()  */
    public boolean hasSucceeded()
    {
        if(this.allRecipients.equals(this.validSentAddressList))
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    /** @see org.apache.fulcrum.commonsemail.SendDeliveryStatus#getValidSentAddresses()  */
    public Address[] getValidSentAddresses()
    {
        Set currList = this.validSentAddressList;
        return (Address[]) currList.toArray(new Address[currList.size()]);
    }

    /** @see org.apache.fulcrum.commonsemail.SendDeliveryStatus#getValidUnsentAddresses()  */
    public Address[] getValidUnsentAddresses()
    {
        Set currList = this.validUnsentAddressList;
        return (Address[]) currList.toArray(new Address[currList.size()]);
    }

    /** @see org.apache.fulcrum.commonsemail.SendDeliveryStatus#getInvalidAddresses()  */
    public Address[] getInvalidAddresses()
    {
        Set currList = this.invalidAddressList; 
        return (Address[]) currList.toArray(new Address[currList.size()]);
    }

    /** @see org.apache.fulcrum.commonsemail.SendDeliveryStatus#getAllRecipients() */
    public Address[] getAllRecipients()
    {
        return (Address[]) this.allRecipients.toArray(new Address[this.allRecipients.size()]);
    }

    /** @see org.apache.fulcrum.commonsemail.SendDeliveryStatus#size()  */
    public int size()
    {
        return this.allRecipients.size();
    }

    /** @see org.apache.fulcrum.commonsemail.SendDeliveryStatus#getMessageId()  */    
    public String getMessageId()
    {
        return messageId;
    }

    /** @see Object#toString() */
    public String toString()
    {
        StringBuffer result = new StringBuffer(128);
        result.append(this.getClass().getName());
        result.append('@');
        result.append(Integer.toHexString(this.hashCode()));
        result.append(' ');
        result.append("allRecipients:");
        result.append(this.allRecipients.size());
        result.append(',');
        result.append("validSentAddresses:");
        result.append(this.validSentAddressList.size());
        result.append(',');
        result.append("validUnsentAddresses:");
        result.append(this.validUnsentAddressList.size());
        result.append(',');
        result.append("invalidAddresses:");
        result.append(this.invalidAddressList.size());
        return result.toString();
    }

    /**
     * Store the addresses of the incoming TransportEvents.
     *
     * @param transportEvent the transport event
     */
    void add( TransportEvent transportEvent )
    {
        this.addAddresses(this.validSentAddressList, transportEvent.getValidSentAddresses());
        this.addAddresses(this.validUnsentAddressList, transportEvent.getValidUnsentAddresses() );
        this.addAddresses(this.invalidAddressList, transportEvent.getInvalidAddresses());
    }

    /**
     * Store the addresses.
     *
     * @param sentAddresses of email addresses
     */
    void addSendAddressList( Address[] sentAddresses )
    {
        this.addAddresses(this.validSentAddressList, sentAddresses);
    }

    /**
     * The message id of the corresponding email
     *
     * @param messageId the message id
     */
    void setMessageId(String messageId)
    {
        this.messageId = messageId;    
    }

    /**
     * Add a list of addresses to the given target list
     *
     * @param target the target list
     * @param addressList the address list
     */
    private void addAddresses(Set target, Address[] addressList)
    {
        if((addressList != null) && (addressList.length > 0))
        {
            for(int i=0; i<addressList.length; i++)
            {
                target.add( addressList[i]);
            }
        }
    }
}
