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
import java.util.HashMap;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.StringTokenizer;
import org.apache.commons.util.StringUtils;
import org.apache.fulcrum.BaseService;
import org.apache.fulcrum.InitializationException;

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
 * <p>Then call one of four methods to retrieve a ResourceBundle:
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
 * @version $Id$
 */
public class TurbineLocalizationService
    extends BaseService
    implements LocalizationService
{
    /**
     * Bundle name keys a HashMap of the ResourceBundles in this
     * service (which is in turn keyed by Locale).
     */
    private static HashMap bundles = null;

    /** The name of the default bundle to use. */
    private static String defaultBundle = null;

    /** The name of the default language to use. */
    private static String defaultLanguage = null;

    /** The name of the default country to use. */
    private static String defaultCountry = null;

    /**
     * Constructor.
     */
    public TurbineLocalizationService()
    {
    }

    /**
     * Called the first time the Service is used.
     */
    public void init()
        throws InitializationException
    {
        bundles = new HashMap();
        defaultBundle = getConfiguration().getString("locale.default.bundle");
        defaultLanguage = getConfiguration()
            .getString("locale.default.language", "en").trim();
        defaultCountry = getConfiguration()
            .getString("locale.default.country", "US").trim();
        setInit(true);
    }

    public String getDefaultLanguage()
    {
        return defaultLanguage;
    }

    public String getDefaultCountry()
    {
        return defaultCountry;
    }

    public String getDefaultBundle()
    {
        return defaultBundle;
    }

    /**
     * This method returns a ResourceBundle given the bundle named by
     * the value of this service's <code>locale.default.bundle</code>
     * property (generally <code>"DEFAULT"</code>), and the default
     * Locale information supplied in TurbineProperties.
     *
     * @return A localized ResourceBundle.
     */
    public ResourceBundle getBundle()
    {
        return getBundle( defaultBundle );
    }

    /**
     * This method returns a ResourceBundle given the bundle name and
     * the default Locale information supplied in TurbineProperties.
     *
     * @param bundleName Name of bundle.
     * @return A localized ResourceBundle.
     */
    public ResourceBundle getBundle(String bundleName)
    {
        return getBundle( bundleName, 
            new Locale(defaultLanguage, defaultCountry) );
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
    public ResourceBundle getBundle(String bundleName,
                                    String languageHeader)
    {
        return getBundle( bundleName,
                          getLocale(languageHeader) );
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
        Locale locale = getLocale(req);
        if (locale == null)
        {
            return getBundle();
        }
        return getBundle( defaultBundle, locale );
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
    public ResourceBundle getBundle(String bundleName,
                                    HttpServletRequest req)
    {
        Locale locale = getLocale(req);
        if (locale == null)
        {
            return getBundle(bundleName);
        }
        return getBundle( bundleName, locale );
    }

    /**
     * This method returns a ResourceBundle for the given bundle name
     * and the given Locale.
     *
     * @param bundleName Name of bundle.
     * @param locale A Locale.
     * @return A localized ResourceBundle.
     */
    public ResourceBundle getBundle(String bundleName,
                                    Locale locale)
    {
        ResourceBundle rb = null;
        bundleName = bundleName.trim();
        HashMap bundlesByLocale = (HashMap) bundles.get(bundleName);

        if (bundlesByLocale != null)
        {
            rb = (ResourceBundle) bundlesByLocale.get(locale);

            if (rb == null)
            {
                rb = cacheBundle(bundleName, locale);
            }
        }
        else
        {
            rb = cacheBundle(bundleName, locale);
        }
        return rb;
    }

    /**
     * Caches the named bundle for fast lookups.  This operation is
     * relatively expesive in terms of memory use, but is optimized
     * for run-time speed.
     */
    private synchronized ResourceBundle cacheBundle(String bundleName,
                                                    Locale locale)
    {
        HashMap bundlesByLocale = (HashMap) bundles.get(bundleName);
        ResourceBundle rb = (bundlesByLocale == null ? null :
                             (ResourceBundle) bundlesByLocale.get(bundleName));

        if (rb == null)
        {
            HashMap bundlesByName = new HashMap(bundles);
            bundlesByLocale = (bundlesByLocale == null ? new HashMap(3) :
                               new HashMap(bundlesByLocale));
            rb = ResourceBundle.getBundle(bundleName, locale);

            // Can't call getLocale(), because that is jdk2.  This
            // needs to be changed back, since the above approach
            // caches extra Locale and Bundle objects.
            // bundlesByLocale.put( rb.getLocale(), rb );
            bundlesByLocale.put(locale, rb);

            bundlesByName.put(bundleName, bundlesByLocale);
            this.bundles = bundlesByName;
        }
        return rb;
    }

    /**
     * This method sets the name of the defaultBundle.
     *
     * @param defaultBundle Name of default bundle.
     */
    public void setBundle(String defaultBundle)
    {
        this.defaultBundle = defaultBundle;
    }

    /**
     * Attempts to pull the "Accept-Language" header out of the
     * HttpServletRequest object and then parse it.  If the header is
     * not present, it will return a null Locale.
     *
     * @param req HttpServletRequest.
     * @return A Locale.
     */
    public Locale getLocale(HttpServletRequest req)
    {
        String header = req.getHeader(ACCEPT_LANGUAGE);
        return (StringUtils.isValid(header) ? getLocale(header) : null);
    }

    /**
     * This method parses the Accept-Language header and attempts to
     * create a Locale out of it.
     *
     * @param languageHeader A String with the language header.
     * @return A Locale.
     */
    public Locale getLocale(String languageHeader)
    {
        Locale locale = null;

        if (StringUtils.isEmpty(languageHeader))
        {
            return new Locale(defaultLanguage, defaultCountry);
        }

        // The HTTP Accept-Header is something like
        //
        // "en, es;q=0.8, zh-TW;q=0.1"
        StringTokenizer tokenizer = new StringTokenizer(languageHeader, ",");

        // while ( tokenizer.hasMoreTokens() )
        // {
        String language = tokenizer.nextToken();
        // This should never be true but just in case
        // if ( !language.trim().equals("") )
        return getLocaleForLanguage(language.trim());
        // }
    }

    /**
     * This method creates a Locale from the language.
     *
     * @param language A String with the language.
     * @return A Locale.
     */
    private static Locale getLocaleForLanguage(String language)
    {
        Locale locale = null;
        int index;

        // Cut off any q-value that comes after a semi-colon.
        if ( (index = language.indexOf(';')) != -1 )
        {
            language = language.substring(0, index);
        }

        language = language.trim();

        // Create a Locale from the language.  A dash may separate the
        // language from the country.
        if ( (index = language.indexOf('-')) == -1 )
        {
            // No dash means no country.
            locale = new Locale(language, "");
        }
        else
        {
            locale = new Locale(language.substring(0, index),
                                language.substring(index + 1));
        }

        return locale;
    }
}
