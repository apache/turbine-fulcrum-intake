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

import org.apache.commons.lang3.StringUtils;

/**
 * <p>This class is the single point of access to all localization
 * resources.  It caches different ResourceBundles for different
 * Locales.</p>
 *
 * <p>Usage example:</p>
 *
 * <blockquote><code><pre>
 * LocalizationService ls = (LocalizationService) TurbineServices
 *     .getInstance().getService(LocalizationService.SERVICE_NAME);
 * </pre></code></blockquote>
 *
 * <p>Then call {@link #getString(String, Locale, String)}, or one of
 * four methods to retrieve a ResourceBundle:
 *
 * <ul>
 * <li>getBundle("MyBundleName")</li>
 * <li>getBundle("MyBundleName", httpAcceptLanguageHeader)</li>
 * <li>etBundle("MyBundleName", HttpServletRequest)</li>
 * <li>getBundle("MyBundleName", Locale)</li>
 * <li>etc.</li>
 * </ul></p>
 *
 * @author <a href="mailto:jm@mediaphil.de">Jonas Maurus</a>
 * @author <a href="mailto:jon@latchkey.com">Jon S. Stevens</a>
 * @author <a href="mailto:novalidemail@foo.com">Frank Y. Kim</a>
 * @author <a href="mailto:dlr@finemaltcoding.com">Daniel Rall</a>
 * @author <a href="mailto:leonardr@collab.net">Leonard Richardson</a>
 * @author <a href="mailto:mcconnell@apache.org">Stephen McConnell</a>
 * @author <a href="mailto:tv@apache.org">Thomas Vandahl</a>
 * @version $Id$
 * @avalon.component name="localization" lifestyle="singleton"
 * @avalon.service type="org.apache.fulcrum.localization.LocalizationService"
 */
public class DefaultLocalizationService
    extends SimpleLocalizationServiceImpl
    implements LocalizationService
{
    /**
     * Creates a new instance.
     */
    public DefaultLocalizationService()
    {
        super();
    }

    /**
     * This method returns a ResourceBundle given the bundle name and
     * the Locale information supplied in the HTTP "Accept-Language"
     * header.
     *
     * @param bundleName Name of bundle.
     * @param languageHeader A String with the language header.
     * @return A localized ResourceBundle.
     */
    public ResourceBundle getBundle(String bundleName, String languageHeader)
    {
        return getBundle(bundleName, getLocale(languageHeader));
    }

    /**
     * This method returns a ResourceBundle given the Locale
     * information supplied in the HTTP "Accept-Language" header which
     * is stored in HttpServletRequest.
     *
     * @param req HttpServletRequest.
     * @return A localized ResourceBundle.
     */
    public ResourceBundle getBundle(HttpServletRequest req)
    {
        return getBundle(getDefaultBundleName(), getLocale(req));
    }

    /**
     * This method returns a ResourceBundle given the bundle name and
     * the Locale information supplied in the HTTP "Accept-Language"
     * header which is stored in HttpServletRequest.
     *
     * @param bundleName Name of the bundle to use if the request's
     * locale cannot be resolved.
     * @param req HttpServletRequest.
     * @return A localized ResourceBundle.
     */
    public ResourceBundle getBundle(String bundleName, HttpServletRequest req)
    {
        return getBundle(bundleName, getLocale(req));
    }

    /**
     * @see org.apache.fulcrum.localization.LocalizationService#getLocale(HttpServletRequest)
     */
    public Locale getLocale(HttpServletRequest req)
    {
        return getLocale(req.getHeader(ACCEPT_LANGUAGE));
        // (JSS) Backed out this change because Tomcat seems to be returning
        //       the wrong result and things just are not working.
        //        Locale l = req.getLocale();
        //        return (l != null ? l : getLocale(req.getHeader(ACCEPT_LANGUAGE)));
    }

    /**
     * @see org.apache.fulcrum.localization.LocalizationService#getLocale(String)
     */
    public Locale getLocale(String header)
    {
        if (!StringUtils.isEmpty(header))
        {
            LocaleTokenizer tok = new LocaleTokenizer(header);
            if (tok.hasNext())
            {
                return (Locale) tok.next();
            }
        }
        
        // Couldn't parse locale.
        return getDefaultLocale();
    }
}
