package org.apache.fulcrum.jce.crypto;

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


/**
 * CryptoParameters used for encryption/decrytpion.
 *
 * @author <a href="mailto:siegfried.goeschl@it20one.at">Siegfried Goeschl</a>
 */

public interface CryptoParameters
{
    /** Parameter for PBEParameterSpec */
    int COUNT = 20;

    /**
     * The algorithm being used
     *
     * <ul>
     *   <li>for SunJCE 1.22 (JDK 1.3) :  PBEWithMD5AndDES</li>
     *   <li>for SunJCE 1.42 (JDK 1.4) :  PBEWithMD5AndDES, PBEWithMD5AndTripleDES</li>
     * </ul>
     */
    String ALGORITHM = "PBEWithMD5AndDES";

    /**
     * The JCE provider name known to work. If the value
     * is set to null an appropriate provider will be
     * used.
     *
     * <ul>
     *  <li>SunJCE<li>
     *  <li>BC (Bouncy Castle Provider)<li>
     * </ul>
     */
    String PROVIDERNAME = null;

    /** The password salt */
    byte[] SALT = {
        (byte)0xc7, (byte)0x73, (byte)0x21, (byte)0x8c,
        (byte)0x7e, (byte)0xc8, (byte)0xee, (byte)0x99
        };
}
