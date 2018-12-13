package org.apache.fulcrum.yaafi.framework.util;

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
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.avalon.framework.logger.Logger;
import org.apache.avalon.framework.logger.NullLogger;

/**
 * Helper for locating a file name and returning an input stream.
 *
 * @author <a href="mailto:siegfried.goeschl@it20one.at">Siegfried Goeschl</a>
 */

public class InputStreamLocator {
	
	/** the root directory of our search */
	private File rootDir;

	/** the logger to be used */
	private Logger logger;

	/**
	 * Constructor
	 */
	public InputStreamLocator() {
		this.rootDir = new File(new File("").getAbsolutePath());
		this.logger = new NullLogger();
	}

	/**
	 * Constructor
	 *
	 * @param rootDir the root directory to start the search
	 */
	public InputStreamLocator(File rootDir) {
		this(rootDir, new NullLogger());
	}

	/**
	 * Constructor
	 *
	 * @param rootDir the root directory to start the search
	 * @param logger  the logger to be used
	 */
	public InputStreamLocator(File rootDir, Logger logger) {
		this.rootDir = rootDir;
		this.logger = logger;
	}

	/**
	 * Locate the file with the given position using the following steps
	 *
	 * @param location the location of the source to be loaded
	 * @return input stream of the source
	 * @throws IOException if source is not found
	 */
	public InputStream locate(String location) throws IOException {
		if (location == null || location.length() == 0) {
			return null;
		}

		String baseName = null;
		File file = null;
		InputStream is = null;

		// try to load a relative location with the given root dir
		// e.g. "componentRoles.xml" located in the current working directory
		if (is == null) {
			file = new File(this.rootDir, location);

			this.logger.debug("Looking for " + location + " in the root directory");

			if (file.exists()) {
				is = new FileInputStream(file);
				this.logger.debug("Found " + location + " as " + file.getAbsolutePath());
			}
		}

		// try to load an absolute location as file
		// e.g. "/foo/componentRoles.xml" from the root of the file system
		if (is == null) {
			file = new File(location);

			this.logger.debug("Looking for " + location + " as absolute file location");

			if (file.isAbsolute() && file.exists()) {
				is = new FileInputStream(file);
				this.logger.debug("Found " + location + " as " + file.getAbsolutePath());
			}
		}

		// try to load an absolute location through the classpath
		// e.g. "/componentRoles.xml" located in the classpath
		if (is == null && location.startsWith("/") == true) {
			this.logger.debug("Looking for " + location + " using the class loader");
			is = getClass().getResourceAsStream(location);

			if (is != null) {
				this.logger.debug("Successfully located " + location);
			}
		}

		// try to load the last part of the file name using the classloader
		// e.g. "conf/componentRoles.xml" as "/componentRoles.xml" located in
		// the classpath.

		if (is == null && location.startsWith("/") == false) {
			baseName = '/' + new File(location).getName();
			this.logger.debug("Looking for " + baseName + " using the class loader");
			is = getClass().getResourceAsStream(baseName);
			if (is != null) {
				this.logger.debug("Successfully located " + baseName);
			}
		}

		if (is == null) {
			this.logger.info("Unable to find any resource with the name '" + location + "'");
		}

		return is;
	}

	/**
	 * @return Returns the rootDir.
	 */
	protected File getRootDir() {
		return rootDir;
	}

}
