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

importPackage(java.util);

testMe(bar);

function testMe(aBar)
{
  // 1) parse the arguments

  var foo = parseInt(aBar);

  // 2) access the avalonContext

  var logger = avalonContext.getLogger();
  var applicationDir = avalonContext.getApplicationDir();
  var tempDir = avalonContext.getTempDir();
  var serviceManager = avalonContext.getServiceManager();
  var parameters = avalonContext.getParameters();
  var configuration = avalonContext.getConfiguration();
  var isDebug = configuration.getChild("isDebug").getValueAsBoolean(false);
  serviceManager.lookup("org.apache.fulcrum.script.ScriptService").exists("Avalon");

  // print("applicationDir = " + applicationDir);
  // print("tempDir = " + tempDir);
  // print("isDebug = " + isDebug);
  logger.info("Logging from within a script ... :-)");

  // 3) create a property instance

  var props = new Properties();
  props.setProperty( "foo", bar );
  if( props.size() != 1 ) throw "setting a property failed";
  props.clear();
  if( props.size() != 0 ) throw "setting a property failed";

  // 4) return a result

  var result = foo*10;

  return result;
}
