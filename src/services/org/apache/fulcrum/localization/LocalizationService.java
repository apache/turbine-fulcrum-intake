package org.apache.fulcrum.localization;

/* ====================================================================
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2001 The Apache Software Foundation.  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgment:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgment may appear in the software itself,
 *    if and wherever such third-party acknowledgments normally appear.
 *
 * 4. The names "Apache" and "Apache Software Foundation" and
 *    "Apache Turbine" must not be used to endorse or promote products
 *    derived from this software without prior written permission. For
 *    written permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache",
 *    "Apache Turbine", nor may "Apache" appear in their name, without
 *    prior written permission of the Apache Software Foundation.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 */

import javax.servlet.http.HttpServletRequest;
import java.util.Locale;
import java.util.ResourceBundle;
import org.apache.fulcrum.Service;

/**
 * Implementations of the LocalizationService interface.
 *
 * @author <a href="mailto:jon@latchkey.com">Jon S. Stevens</a>
 * @version $Id$
 */
public interface LocalizationService
    extends Service
{
    public static final String SERVICE_NAME = "LocalizationService";
    public static final String ACCEPT_LANGUAGE = "Accept-Language";

    public String getDefaultLanguage();
    public String getDefaultCountry();
    public String getDefaultBundle();

    /**
     * Convenience method to get a default ResourceBundle.
     *
     * @return A localized ResourceBundle.
     */
    public ResourceBundle getBundle();

    /**
     * Convenience method to get a ResourceBundle based on name.
     *
     * @param bundleName Name of bundle.
     * @return A localized ResourceBundle.
     */
    public ResourceBundle getBundle(String bundleName);

    /**
     * Convenience method to get a ResourceBundle based on name and
     * HTTP <code>Accept-Language</code> header.
     *
     * @param bundleName Name of bundle.
     * @param languageHeader A String with the language header.
     * @return A localized ResourceBundle.
     */
    public ResourceBundle getBundle(String bundleName,
                                    String languageHeader);

    /**
     * Convenience method to get a ResourceBundle based on HTTP
     * Accept-Language header in HttpServletRequest.
     *
     * @param req The HTTP request to parse the
     * <code>Accept-Language</code> of.
     * @return A localized ResourceBundle.
     */
    public ResourceBundle getBundle (HttpServletRequest req);

    /**
     * Convenience method to get a ResourceBundle based on name and
     * HTTP Accept-Language header in HttpServletRequest.
     *
     * @param bundleName Name of bundle.
     * @param req The HTTP request to parse the
     * <code>Accept-Language</code> of.
     * @return A localized ResourceBundle.
     */
    public ResourceBundle getBundle(String bundleName,
                                    HttpServletRequest req);

    /**
     * Convenience method to get a ResourceBundle based on name and
     * Locale.
     *
     * @param bundleName Name of bundle.
     * @param locale A Locale.
     * @return A localized ResourceBundle.
     */
    public ResourceBundle getBundle(String bundleName,
                                    Locale locale);

    /**
     * Attempts to pull the "Accept-Language" header out of the
     * HttpServletRequest object and then parse it.  If the header is
     * not present, it will return a null Locale.
     *
     * @param req HttpServletRequest.
     * @return A Locale.
     */
    public Locale getLocale(HttpServletRequest req);

    /**
     * This method parses the Accept-Language header and attempts to
     * create a Locale out of it.
     *
     * @param languageHeader A String with the language header.
     * @return A Locale.
     */
    public Locale getLocale(String languageHeader);

    /**
     * This method sets the name of the defaultBundle.
     *
     * @param defaultBundle Name of default bundle.
     */
    public void setBundle(String defaultBundle);
}
