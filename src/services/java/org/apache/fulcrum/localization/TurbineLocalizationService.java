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

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.StringTokenizer;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.util.StringUtils;
import org.apache.log4j.Category;
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
    private HashMap bundles = null;

    /** The name of the default bundle to use. */
    private String defaultBundle = null;

    /**
     * The name of the default locale to use (includes language and
     * country).
     */
    private Locale defaultLocale = null;

    /** The name of the default language to use. */
    private String defaultLanguage = null;

    /** The name of the default country to use. */
    private String defaultCountry = null;

    /**
     * Log4J logging category.
     */
    private Category category = Category.getInstance(getClass().getName());

    /**
     * Creates a new instance.
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
        Locale jvmDefault = Locale.getDefault();
        defaultLanguage = getConfiguration()
            .getString("locale.default.language",
                       jvmDefault.getLanguage()).trim();
        defaultCountry = getConfiguration()
            .getString("locale.default.country",
                       jvmDefault.getCountry()).trim();
        defaultLocale = new Locale(defaultLanguage, defaultCountry);
        setInit(true);
    }

    /**
     * Retrieves the default language (specified in the config file).
     */
    public String getDefaultLanguage()
    {
        return defaultLanguage;
    }

    /**
     * Retrieves the default country (specified in the config file).
     */
    public String getDefaultCountry()
    {
        return defaultCountry;
    }

    /**
     * Retrieves the name of the default bundle (as specified in the
     * config file).
     */
    public String getDefaultBundleName()
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
        return getBundle(defaultBundle, (Locale) null);
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
        return getBundle(bundleName, (Locale) null);
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
        return getBundle(defaultBundle, getLocale(req));
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
     * This method returns a ResourceBundle for the given bundle name
     * and the given Locale.
     *
     * @param bundleName Name of bundle (or <code>null</code> for the
     * default bundle).
     * @param locale The locale (or <code>null</code> for the locale
     * indicated by the default language and country).
     * @return A localized ResourceBundle.
     */
    public ResourceBundle getBundle(String bundleName, Locale locale)
    {
        // Assure usable inputs.
        bundleName = (bundleName == null ? defaultBundle : bundleName.trim());
        if (locale == null)
        {
            locale = getLocale((String) null);
        }

        // Find/retrieve/cache bundle.
        ResourceBundle rb = null;
        HashMap bundlesByLocale = (HashMap) bundles.get(bundleName);
        if (bundlesByLocale != null)
        {
            // Cache of bundles by locale for the named bundle exists.
            // Check the cache for a bundle corresponding to locale.
            rb = (ResourceBundle) bundlesByLocale.get(locale);

            if (rb == null)
            {
                // Not yet cached.
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
     * for run-time speed in the usual case.
     *
     * @exception MissingResourceException Bundle not found.
     */
    private synchronized ResourceBundle cacheBundle(String bundleName,
                                                    Locale locale)
        throws MissingResourceException
    {
        HashMap bundlesByLocale = (HashMap) bundles.get(bundleName);
        ResourceBundle rb = (bundlesByLocale == null ? null :
                             (ResourceBundle) bundlesByLocale.get(bundleName));

        if (rb == null)
        {
            bundlesByLocale = (bundlesByLocale == null ? new HashMap(3) :
                               new HashMap(bundlesByLocale));
            try
            {
                rb = ResourceBundle.getBundle(bundleName, locale);
            }
            catch (MissingResourceException e)
            {
                rb = findClosestBundle(bundleName, locale, bundlesByLocale);
                if (rb == null)
                {
                    throw (MissingResourceException) e.fillInStackTrace();
                }
            }

            if (rb != null)
            {
                // Cache bundle.
                bundlesByLocale.put(rb.getLocale(), rb);

                HashMap bundlesByName = new HashMap(bundles);
                bundlesByName.put(bundleName, bundlesByLocale);
                this.bundles = bundlesByName;
            }
        }
        return rb;
    }

    /**
     * <p>Retrieves the bundle most closely matching first against the
     * supplied inputs, then against the defaults.</p>
     *
     * <p>Use case: some clients send a HTTP Accept-Language header
     * with a value of only the language to use
     * (i.e. "Accept-Language: en"), and neglect to include a country.
     * When there is no bundle for the requested language, this method
     * can be called to try the default country (checking internally
     * to assure the requested criteria matches the default to avoid
     * disconnects between language and country).</p>
     *
     * <p>Since we're really just guessing at possible bundles to use,
     * we don't ever throw <code>MissingResourceException</code>.</p>
     */
    private ResourceBundle findClosestBundle(String bundleName, Locale locale,
                                             Map bundlesByLocale)
    {
        ResourceBundle rb = null;
        if ( !StringUtils.isValid(locale.getCountry()) &&
             defaultLanguage.equals(locale.getLanguage()) )
        {
            /*
            category.debug("Requested language '" + locale.getLanguage() +
                           "' matches default: Attempting to guess bundle " +
                           "using default country '" + defaultCountry + '\'');
            */
            Locale withDefaultCountry = new Locale(locale.getLanguage(),
                                                   defaultCountry);
            rb = (ResourceBundle) bundlesByLocale.get(withDefaultCountry);
            if (rb == null)
            {
                rb = getBundleIgnoreException(bundleName, withDefaultCountry);
            }
        }
        else if ( !StringUtils.isValid(locale.getLanguage()) &&
                  defaultCountry.equals(locale.getCountry()) )
        {
            Locale withDefaultLanguage = new Locale(defaultLanguage, 
                                                    locale.getCountry());
            rb = (ResourceBundle) bundlesByLocale.get(withDefaultLanguage);
            if (rb == null)
            {
                rb = getBundleIgnoreException(bundleName, withDefaultLanguage);
            }
        }

        if (rb == null && !defaultLocale.equals(locale))
        {
            rb = getBundleIgnoreException(bundleName, defaultLocale);
        }

        return rb;
    }

    /**
     * Retrieves the bundle using the
     * <code>ResourceBundle.getBundle(String, Locale)</code> method,
     * ignoring <code>MissingResourceException</code>.
     */
    private final ResourceBundle getBundleIgnoreException(String bundleName,
                                                          Locale locale)
    {
        try
        {
            return ResourceBundle.getBundle(bundleName, locale);
        }
        catch (MissingResourceException ignored)
        {
            return null;
        }
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
     * @see org.apache.fulcrum.localization.LocalizationService#getLocale(HttpServletRequest)
     */
    public final Locale getLocale(HttpServletRequest req)
    {
        return getLocale(req.getHeader(ACCEPT_LANGUAGE));
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
        return defaultLocale;
    }
}
