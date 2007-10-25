package org.apache.fulcrum.intake;

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

import org.apache.commons.lang.exception.NestableError;

/**
 * Base exception thrown by the Intake service.
 *
 * @author <a href="mailto:quintonm@bellsouth.net">Quinton McCombs</a>
 * @version $Id$
 */
public class IntakeError extends NestableError
{
    /**
     * Serial version id
     */
    private static final long serialVersionUID = 7594936180163472231L;

    /**
     * Constructs a new <code>IntakeError</code> without specified
     * detail message.
     */
    public IntakeError()
    {
    }

    /**
     * Constructs a new <code>IntakeError</code> with specified
     * detail message.
     *
     * @param msg The error message.
     */
    public IntakeError(String msg)
    {
        super(msg);
    }

    /**
     * Constructs a new <code>IntakeError</code> with specified
     * nested <code>Throwable</code>.
     *
     * @param nested The exception or error that caused this exception
     *               to be thrown.
     */
    public IntakeError(Throwable nested)
    {
        super(nested);
    }

    /**
     * Constructs a new <code>IntakeError</code> with specified
     * detail message and nested <code>Throwable</code>.
     *
     * @param msg    The error message.
     * @param nested The exception or error that caused this exception
     *               to be thrown.
     */
    public IntakeError(String msg, Throwable nested)
    {
        super(msg, nested);
    }

}
