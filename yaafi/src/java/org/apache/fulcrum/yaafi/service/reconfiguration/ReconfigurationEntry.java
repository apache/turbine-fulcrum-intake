package org.apache.fulcrum.yaafi.service.reconfiguration;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.util.Arrays;

import org.apache.avalon.framework.logger.Logger;
import org.apache.commons.io.IOUtils;
import org.apache.fulcrum.yaafi.framework.util.InputStreamLocator;

/**
 * Monitors a resource and checks if it has changed
 *
 * @author <a href="mailto:siegfried.goeschl@it20one.at">Siegfried Goeschl</a>
 */

public class ReconfigurationEntry {

	/** the location to monitor for changes */
	private String location;

	/** the list of services to be reconfigured */
	private String[] serviceList;

	/** the last message digest of the location */
	private byte[] digest;

	/** the locator to load the monitored resource */
	private InputStreamLocator locator;

	/** keep a notice for the very first invocation */
	private boolean isFirstInvocation;

	/** the logger to be used */
	private Logger logger;

	/**
	 * Constructor
	 *
	 * @param logger         the logger to use
	 * @param applicationDir the home directory of the application
	 * @param location       the location to monitor for changes
	 * @param serviceList    the list of services to be reconfigured
	 */
	public ReconfigurationEntry(Logger logger, File applicationDir, String location, String[] serviceList) {
		this.isFirstInvocation = true;
		this.location = location;
		this.locator = new InputStreamLocator(applicationDir);
		this.logger = logger;
		this.serviceList = serviceList;
	}

	/**
	 * @return has the monitored location changed
	 */
	public boolean hasChanged() {
		boolean result = false;
		InputStream is = null;
		byte[] currDigest = null;

		try {
			// get a grip on our resource

			is = this.locate();

			if (is == null) {
				String msg = "Unable to find the following resource : " + this.getLocation();
				this.logger.warn(msg);
			} else {
				// calculate a SHA-1 digest
				currDigest = this.getDigest(is);
				is.close();
				is = null;

				if (this.isFirstInvocation() == true) {
					isFirstInvocation = false;
					this.logger.debug("Storing SHA-1 digest of " + this.getLocation());
					this.setDigest(currDigest);
				} else {
					if (equals(this.digest, currDigest) == false) {
						this.logger.debug("The following resource has changed : " + this.getLocation());
						this.setDigest(currDigest);
						result = true;
					}
				}
			}

			return result;
		} catch (Exception e) {
			String msg = "The ShutdownService encountered an internal error";
			this.logger.error(msg, e);
			return false;
		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (Exception e) {
					String msg = "Can't close the InputStream during error recovery";
					this.logger.error(msg, e);
				}
			}
		}

	}

	/**
	 * @return Returns the serviceList.
	 */
	public String[] getServiceList() {
		return serviceList;
	}

	/**
	 * @return Returns the isFirstInvocation.
	 */
	private boolean isFirstInvocation() {
		return isFirstInvocation;
	}

	/**
	 * @return Returns the location.
	 */
	private String getLocation() {
		return location;
	}

	/**
	 * Creates an InputStream.
	 * 
	 * @return the input stream
	 * @throws IOException the creation failed
	 */
	public InputStream locate() throws IOException {
		return this.locator.locate(this.getLocation());
	}

	/**
	 * Creates a message digest.
	 *
	 * @param is the input stream as input for the message digest
	 * @return the message digest
	 * @throws Exception the creation failed
	 */
	private byte[] getDigest(InputStream is) throws Exception {
		byte[] result = null;
		byte[] content = null;

		// convert to byte array
		content = IOUtils.toByteArray(is);

		MessageDigest sha1 = MessageDigest.getInstance("SHA1");
		sha1.update(content);
		result = sha1.digest();

		return result;
	}

	/**
	 * @param digest The digest to set.
	 */
	private void setDigest(byte[] digest) {
		this.digest = digest;
	}

	/**
	 * Compares two byte[] for equality
	 *
	 * @param lhs the left-hand side
	 * @param rhs the right-hand side
	 * @return true if the byte[] are equal
	 */
	private static boolean equals(byte[] lhs, byte[] rhs) {
		// JDK provided method
		return Arrays.equals(lhs, rhs);
	}

}
