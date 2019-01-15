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
import java.util.ResourceBundle;

import javax.servlet.http.HttpServletRequest;

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
 * @version $Id$
 */
public interface LocalizationService extends SimpleLocalizationService
{
    String ROLE = LocalizationService.class.getName();
    String SERVICE_NAME = ROLE;

    /**
     * A constant for the HTTP <code>Accept-Language</code> header.
     */
    String ACCEPT_LANGUAGE = "Accept-Language";

    /**
     * Convenience method to get a ResourceBundle based on name and
     * HTTP <code>Accept-Language</code> header.
     *
     * @param bundleName Name of bundle.
     * @param languageHeader A String with the language header.
     * @return A localized ResourceBundle.
     */
    ResourceBundle getBundle(String bundleName, String languageHeader);

    /**
     * Convenience method to get a ResourceBundle based on HTTP
     * Accept-Language header in HttpServletRequest.
     *
     * @param req The HTTP request to parse the
     * <code>Accept-Language</code> of.
     * @return A localized ResourceBundle.
     */
    ResourceBundle getBundle(HttpServletRequest req);

    /**
     * Convenience method to get a <code>ResourceBundle</code> based
     * on name and HTTP <code>Accept-Language</code> header from a
     * <code>HttpServletRequest</code>.
     *
     * @param bundleName Name of bundle.
     * @param req The HTTP request to parse the
     * <code>Accept-Language</code> of.
     * @return A localized ResourceBundle.
     */
    ResourceBundle getBundle(String bundleName, HttpServletRequest req);

    /**
     * Attempts to pull the <code>Accept-Language</code> header out of
     * the <code>HttpServletRequest</code> object and then parse it.
     * If the header is not present, it will return a
     * <code>null</code> <code>Locale</code>.
     *
     * @param req The HTTP request to parse the
     * <code>Accept-Language</code> of.
     * @return The parsed locale.
     */
    Locale getLocale(HttpServletRequest req);

    /**
     * Parses the <code>Accept-Language</code> header and attempts to
     * create a <code>Locale</code> from it.
     *
     * @param languageHeader The language header (i.e. <code>en, es;q=0.8,
     * zh-TW;q=0.1</code>), or <code>null</code> for the locale
     * corresponding to the default language and country.
     * @return The parsed locale, or a locale corresponding to the
     * language and country defaults.
     */
    Locale getLocale(String languageHeader);
}
