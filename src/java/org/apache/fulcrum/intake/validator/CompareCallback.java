package org.apache.fulcrum.intake.validator;

import org.apache.fulcrum.intake.validator.FieldReference.Comparison;

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
 * Interface to define the compare operation betwen two field values
 *
 * @author <a href="mailto:tv@apache.org">Thomas Vandahl</a>
 * @version $Id$
 */
public interface CompareCallback<T>
{
    /**
     * Compare the given values using the compare operation provided
     *
     * @param compare type of compare operation
     * @param thisValue value of this field
     * @param refValue value of the reference field
     *
     * @return the result of the comparison
     */
    boolean compareValues(Comparison compare, T thisValue, T refValue);
}
