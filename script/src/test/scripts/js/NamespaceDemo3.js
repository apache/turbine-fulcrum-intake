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

print("\n-----Starting script");

var initial = parseInt(state);
print("In script....Initial state is " + initial);
var loop = 1;
while (loop == 1) {
	print("Script continues at state = " + state);
	if (parseInt(state) != initial) {
		print("State changed (state = " + state + "). Exit script.");
		loop = 0;
	}
	//it may take the script engine too long to start
	if (parseInt(state)==2) {
		loop = 0;
	}
}
print("-----End of script-----\n");

