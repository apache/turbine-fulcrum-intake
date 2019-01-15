package org.apache.fulcrum.localization;

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


import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * <p>
 * Provides localization functionality using the interface provided
 * by <code>ResourceBundle</code>, plus leverages a "search path"
 * style traversal of the <code>ResourceBundle</code> objects named by
 * the <code>locale.default.bundles</code> to discover a value for a
 * given key.
 * </p>
 *
 * <p>
 * It is suggested that one handle
 * <a href="http://www.math.fu-berlin.de/~rene/www/java/tutorial/i18n/message/messageFormat.html">dealing with concatenated messages</a>
 * using <code>MessageFormat</code> and properties files.
 * </p>
 *
 * @author <a href="mailto:jon@latchkey.com">Jon S. Stevens</a>
 * @author <a href="mailto:dlr@finemaltcoding.com">Daniel Rall</a>
 * @author <a href="mailto:leonardr@collab.net">Leonard Richardson</a>
 * @author <a href="mailto:mcconnell@apache.org">Stephen McConnell</a>
 * @author <a href="mailto:tv@apache.org">Thomas Vandahl</a>
 * @version $Id: LocalizationService.java 535465 2007-05-05 06:58:06Z tv $
 */
public interface SimpleLocalizationService
{
    String ROLE = SimpleLocalizationService.class.getName();
    String SERVICE_NAME = ROLE;

    /**
     * Retrieves the default language (as specified in the config
     * file).
     * @return the default language
     */
    String getDefaultLanguage();

    /**
     * Retrieves the default country (as specified in the config
     * file).
     * 
     * @return the default country
     */
    String getDefaultCountry();

    /**
     * Retrieves the default Locale (as created from default
     * language and default country).
     * 
     * @return the default locale
     */
    Locale getDefaultLocale();

    /**
     * Retrieves the name of the default bundle (as specified in the
     * config file), or the first in the list if there are more than
     * one.
     * 
     * @return the default bundle name
     */
    String getDefaultBundleName();

    /**
     * Retrieves the list of names of bundles to search by default for
     * <code>ResourceBundle</code> keys (as specified in the config
     * file).
     *
     * @return The list of configured bundle names.
     */
    String[] getBundleNames();

    /**
     * Convenience method to get the default <code>ResourceBundle</code>.
     *
     * @return A localized <code>ResourceBundle</code>.
     */
    ResourceBundle getBundle();

    /**
     * Returns a ResourceBundle given the bundle name and the default
     * locale information supplied by the configuration.
     *
     * @param bundleName Name of bundle.
     * @return A localized ResourceBundle.
     */
    ResourceBundle getBundle(String bundleName);

    /**
     * Convenience method to get a ResourceBundle based on name and
     * Locale.
     *
     * @param bundleName Name of bundle.
     * @param locale A Locale.
     * @return A localized ResourceBundle.
     */
    ResourceBundle getBundle(String bundleName, Locale locale);

    /**
     * Tries very hard to return a value, looking first in the
     * specified bundle, then searching list of default bundles
     * (giving precedence to earlier bundles over later bundles).
     *
     * @param bundleName Name of the bundle to look in first.
     * @param locale Locale to get text for.
     * @param key Name of the text to retrieve.
     * @return Localized text.
     * @throws MissingResourceException if the resource is not found
     */
    String getString(String bundleName, Locale locale, String key) throws MissingResourceException;
    
    /**
     * Returns the value for the key in the default bundle and the default locale.
     * 
     * @param key The key to retrieve the value for.
     * @return The value mapped to the key.
     */
    String getString(String key);

    /**
     * This method sets the name of the defaultBundle.
     *
     * @param defaultBundle Name of default bundle.
     */
    void setBundle(String defaultBundle);

    /**
     * Formats a localized value using the provided object.
     *
     * @param bundleName The bundle in which to look for the localizable text.
     * @param locale The locale for which to format the text.
     * @param key The identifier for the localized text to retrieve,
     * @param arg1 The object to use as {0} when formatting the localized text.
     * @return Formatted localized text.
     * @see #format(String, Locale, String, Object[])
     */
    String format(String bundleName, Locale locale,
                         String key, Object arg1);

    /**
     * Formats a localized value using the provided objects.
     *
     * @param bundleName The bundle in which to look for the localizable text.
     * @param locale The locale for which to format the text.
     * @param key The identifier for the localized text to retrieve,
     * @param arg1 The object to use as {0} when formatting the localized text.
     * @param arg2 The object to use as {1} when formatting the localized text.
     * 
     * @return Formatted localized text.
     * 
     * @see #format(String, Locale, String, Object[])
     */
    String format(String bundleName, Locale locale,
                         String key, Object arg1, Object arg2);

    /**
     * Formats a localized value using the provided objects.
     *
     * @param bundleName The bundle in which to look for the localizable text.
     * @param locale The locale for which to format the text.
     * @param key The identifier for the localized text to retrieve,
     * @param args The objects to use as {0}, {1}, etc. when
     *             formatting the localized text.
     * @return Formatted localized text.
     */
    String format(String bundleName, Locale locale,
                         String key, Object[] args);
}
