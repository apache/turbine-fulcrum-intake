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

import java.io.File;
import java.io.FileOutputStream;

import javax.mail.internet.MimeMessage;

/**
 * Static helper methods.
 *
 * @author <a href="mailto:siegfried.goeschl@it20one.at">Siegfried Goeschl</a>
 */

public class CommonsEmailUtils
{
    /**
     * Convinience method to write a MimeMessage into a file.
     *
     * @param resultFile the file containing the MimeMessgae
     * @param mimeMessage the MimeMessage to write
     * @throws Exception writing the MimeMessage failed
     */
    public static void writeMimeMessage(File resultFile, MimeMessage mimeMessage) throws Exception
    {

        FileOutputStream fos = null;

        // don't complain about invalid arguments here
        if( mimeMessage == null || resultFile == null)
        {
            return;
        }
        
        try
        {
            fos = new FileOutputStream(resultFile);
            mimeMessage.writeTo(fos);
            fos.flush();
            fos.close();
            fos = null;
        }
        finally
        {
            if(fos != null)
            {
                try
                {
                    fos.close();
                    fos = null;
                }
                catch( Exception e )
                {
                    // ignore
                }
            }
        }
    }
}
