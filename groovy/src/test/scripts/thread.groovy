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
*
* Simple Groovy script to
*
* +) access the Avalon runtime properties
* +) create and manipulate Java objects
* +) create and delete a file
* +) return a result
*
* @author <a href="mailto:siegfried.goeschl@it20one.at">Siegfried Goeschl</a>
*/


Integer foo = (Integer) args[0];

avalonContext.getLogger().debug( Thread.currentThread().getName() + " - Starting to run" );
Thread.sleep(1000);
avalonContext.getLogger().debug( Thread.currentThread().getName() + " - Finished running" );

return foo;
