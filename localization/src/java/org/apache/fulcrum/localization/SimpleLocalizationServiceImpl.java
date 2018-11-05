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

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.apache.avalon.framework.activity.Initializable;
import org.apache.avalon.framework.configuration.Configurable;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.commons.lang3.StringUtils;

/**
 * <p>This class is the single point of access to all localization
 * resources.  It caches different ResourceBundles for different
 * Locales.</p>
 *
 * <p>Usage example:</p>
 *
 * <pre>
 * <code>
 * SimpleLocalizationService ls = (SimpleLocalizationService) TurbineServices
 *     .getInstance().getService(SimpleLocalizationService.SERVICE_NAME);
 * </code>
 * </pre>
 *
 * <p>Then call {@link #getString(String, Locale, String)}, or one of
 * two methods to retrieve a ResourceBundle:</p>
 *
 * <ul>
 * <li>getBundle("MyBundleName")</li>
 * <li>getBundle("MyBundleName", Locale)</li>
 * <li>etc.</li>
 * </ul>
 *
 * @author <a href="mailto:jm@mediaphil.de">Jonas Maurus</a>
 * @author <a href="mailto:jon@latchkey.com">Jon S. Stevens</a>
 * @author <a href="mailto:novalidemail@foo.com">Frank Y. Kim</a>
 * @author <a href="mailto:dlr@finemaltcoding.com">Daniel Rall</a>
 * @author <a href="mailto:leonardr@collab.net">Leonard Richardson</a>
 * @author <a href="mailto:mcconnell@apache.org">Stephen McConnell</a>
 * @author <a href="mailto:tv@apache.org">Thomas Vandahl</a>
 * @version $Id: DefaultLocalizationService.java 535465 2007-05-05 06:58:06Z tv $
 * 
 * avalon.component name="localization" lifestyle="singleton"
 * avalon.service type="org.apache.fulcrum.localization.SimpleLocalizationService"
 */
public class SimpleLocalizationServiceImpl
    extends AbstractLogEnabled
    implements SimpleLocalizationService, Configurable, Initializable
{
    /** Key Prefix for our bundles */
    private static final String BUNDLES = "bundles";
    /**
     * The value to pass to <code>MessageFormat</code> if a
     * <code>null</code> reference is passed to <code>format()</code>.
     */
    private static final Object[] NO_ARGS = new Object[0];
    /**
     * Bundle name keys a HashMap of the ResourceBundles in this
     * service (which is in turn keyed by Locale).
     */
    private HashMap<String, HashMap<Locale, ResourceBundle>> bundles = null;
    /**
     * The list of default bundles to search.
     */
    private String[] bundleNames = null;
    /**
     * The default bundle name to use if not specified.
     */
    private String defaultBundleName = null;
    /**
     * The name of the default locale to use (includes language and
     * country).
     */
    private Locale defaultLocale = null;
    /** The name of the default language to use. */
    private String defaultLanguage;
    /** The name of the default country to use. */
    private String defaultCountry = null;

    /**
     * Creates a new instance.
     */
    public SimpleLocalizationServiceImpl()
    {
        bundles = new HashMap<String, HashMap<Locale, ResourceBundle>>();
    }

    /**
     * Avalon lifecycle method
     * 
     * {@link org.apache.avalon.framework.configuration.Configurable}
     * 
     * @param conf the configuration
     * @throws ConfigurationException if failed to configure
     */
    @Override
	public void configure(Configuration conf) throws ConfigurationException
    {
        Locale jvmDefault = Locale.getDefault();
        defaultLanguage =
            conf
                .getAttribute(
                    "locale-default-language",
                    jvmDefault.getLanguage())
                .trim();
        defaultCountry =
            conf
                .getAttribute("locale-default-country", jvmDefault.getCountry())
                .trim();
        // FIXME! need to add bundle names
        getLogger().info(
            "initialized lang="
                + defaultLanguage
                + " country="
                + defaultCountry);
        final Configuration bundles = conf.getChild(BUNDLES, false);
        if (bundles != null)
        {
            Configuration[] nameVal = bundles.getChildren();
            String bundleName[] = new String[nameVal.length];
            for (int i = 0; i < nameVal.length; i++)
            {
                String val = nameVal[i].getValue();
                getLogger().debug("Registered bundle " + val);
                bundleName[i] = val;
            }
            initBundleNames(bundleName);
        }
    }
    
    /**
     * Called the first time the Service is used.
     * 
     * @throws Exception generic exception
     */
    @Override
	public void initialize() throws Exception
    {
        // initBundleNames(null);
        defaultLocale = new Locale(defaultLanguage, defaultCountry);
        if (getLogger().isInfoEnabled())
        {
            getLogger().info("Localization Service is Initialized now..");
        }
    }
    
    /**
     * Initialize list of default bundle names.
     *
     * @param intBundleNames set bundle names
     */
    protected void initBundleNames(String[] intBundleNames)
    {
        //System.err.println("cfg=" + getConfiguration());
        if (defaultBundleName != null && defaultBundleName.length() > 0)
        {
            // Using old-style single bundle name property.
            if (intBundleNames == null || intBundleNames.length <= 0)
            {
                bundleNames = new String[] { defaultBundleName };
            }
            else
            {
                // Prepend "default" bundle name.
                String[] array = new String[intBundleNames.length + 1];
                array[0] = defaultBundleName;
                System.arraycopy(
                    intBundleNames,
                    0,
                    array,
                    1,
                    intBundleNames.length);
                bundleNames = array;
            }
        }
        if (intBundleNames == null)
        {
            bundleNames = new String[0];
        }
        bundleNames = intBundleNames;
    }
    
    /**
     * Retrieves the default language (specified in the config file).
     * 
     * @return the default language
     */
    @Override
	public String getDefaultLanguage()
    {
        return defaultLanguage;
    }
    
    /**
     * Retrieves the default country (specified in the config file).
     * 
     * @return the default country
     */
    @Override
	public String getDefaultCountry()
    {
        return defaultCountry;
    }
    
    /**
     * Retrieves the default Locale (as created from default
     * language and default country).
     * 
     * @return the default locale
     */
    @Override
	public Locale getDefaultLocale()
    {
        return defaultLocale;
    }

    /**
     * {@link org.apache.fulcrum.localization.SimpleLocalizationService#getDefaultBundleName()}
     * 
     * @return the default bundle name
     */
    @Override
	public String getDefaultBundleName()
    {
        return (bundleNames.length > 0 ? bundleNames[0] : "");
    }
    
    /**
     * {@link org.apache.fulcrum.localization.SimpleLocalizationService#getBundleNames()}
     * 
     * @return list of bundle names available
     */
    @Override
	public String[] getBundleNames()
    {
        return bundleNames.clone();
    }
    
    /**
     * {@link org.apache.fulcrum.localization.SimpleLocalizationService#getBundle()}
     * 
     * @return the default resource bundle
     */
    @Override
	public ResourceBundle getBundle()
    {
        return getBundle(getDefaultBundleName(), (Locale) null);
    }
    
    /**
     * {@link org.apache.fulcrum.localization.SimpleLocalizationService#getBundle(String)}
     * 
     * @param bundleName the name of a bundle
     * @return the resource bundle
     */
    @Override
	public ResourceBundle getBundle(String bundleName)
    {
        return getBundle(bundleName, (Locale) null);
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
    @Override
	public ResourceBundle getBundle(String bundleName, Locale locale)
    {
        // Assure usable inputs.
        bundleName =
            (bundleName == null ? getDefaultBundleName() : bundleName.trim());
        if (locale == null)
        {
            locale = getDefaultLocale();
        }
        // Find/retrieve/cache bundle.
        ResourceBundle rb = null;
        HashMap<?, ?> bundlesByLocale = (HashMap<?, ?>) bundles.get(bundleName);
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
     * relatively expensive in terms of memory use, but is optimized
     * for run-time speed in the usual case.
     *
     * @param bundleName Name of bundle (or <code>null</code> for the
     * default bundle).
     * @param locale The locale (or <code>null</code> for the locale
     * indicated by the default language and country).
     * @throws MissingResourceException Bundle not found.
     * @return a localized resource bundle
     */
    private synchronized ResourceBundle cacheBundle(
        String bundleName,
        Locale locale)
        throws MissingResourceException
    {
        HashMap<Locale, ResourceBundle> bundlesByLocale = (HashMap<Locale, ResourceBundle>) bundles.get(bundleName);
        ResourceBundle rb =
            (bundlesByLocale == null
                ? null
                : (ResourceBundle) bundlesByLocale.get(locale));
        if (rb == null)
        {
            bundlesByLocale =
                (bundlesByLocale == null
                    ? new HashMap<Locale, ResourceBundle>(3)
                    : new HashMap<Locale, ResourceBundle>(bundlesByLocale));
            try
            {
                rb = ResourceBundle.getBundle(bundleName, locale);
            }
            catch (MissingResourceException e)
            {
                rb = findBundleByLocale(bundleName, locale, bundlesByLocale);
                if (rb == null)
                {
                    throw (MissingResourceException) e.fillInStackTrace();
                }
            }
            if (rb != null)
            {
                // Cache bundle.
                bundlesByLocale.put(rb.getLocale(), rb);
                HashMap<String, HashMap<Locale, ResourceBundle>> bundlesByName = new HashMap<String, HashMap<Locale, ResourceBundle>>(bundles);
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
     * 
     * @param bundleName Name of bundle (or <code>null</code> for the
     * default bundle).
     * @param locale The locale (or <code>null</code> for the locale
     * indicated by the default language and country).
     * @param bundleByLocale map of locales and resource bundles
     * @return a localized resource bundle
     * 
     */
    private ResourceBundle findBundleByLocale(
        String bundleName,
        Locale locale,
        Map<Locale, ResourceBundle> bundlesByLocale)
    {
        ResourceBundle rb = null;
        if (!StringUtils.isNotEmpty(locale.getCountry())
            && defaultLanguage.equals(locale.getLanguage()))
        {
            /*
             *            category.debug("Requested language '" + locale.getLanguage() +
             *                           "' matches default: Attempting to guess bundle " +
             *                           "using default country '" + defaultCountry + '\'');
             */
            Locale withDefaultCountry =
                new Locale(locale.getLanguage(), defaultCountry);
            rb = (ResourceBundle) bundlesByLocale.get(withDefaultCountry);
            if (rb == null)
            {
                rb = getBundleIgnoreException(bundleName, withDefaultCountry);
            }
        }
        else if (
            !StringUtils.isNotEmpty(locale.getLanguage())
                && defaultCountry.equals(locale.getCountry()))
        {
            Locale withDefaultLanguage =
                new Locale(defaultLanguage, locale.getCountry());
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
     * returning <code>null</code> instead of throwing
     * <code>MissingResourceException</code>.
     * 
     * @param bundleName Name of bundle (or <code>null</code> for the
     * default bundle).
     * @param locale The locale (or <code>null</code> for the locale
     * indicated by the default language and country).
     */
    private final ResourceBundle getBundleIgnoreException(
        String bundleName,
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
     * This method sets the name of the first bundle in the search
     * list (the "default" bundle).
     *
     * @param defaultBundle Name of default bundle.
     */
    @Override
	public void setBundle(String defaultBundle)
    {
        if (bundleNames.length > 0)
        {
            bundleNames[0] = defaultBundle;
        }
        else
        {
            synchronized (this)
            {
                if (bundleNames.length <= 0)
                {
                    bundleNames = new String[] { defaultBundle };
                }
            }
        }
    }
    
    /**
     * 
     * {@link org.apache.fulcrum.localization.SimpleLocalizationService#getString(String, Locale, String)}
     * @throws MissingResourceException Specified key cannot be matched.
     */
    @Override
	public String getString(String bundleName, Locale locale, String key)
      throws MissingResourceException
    {
        String value = null;
        if (locale == null)
        {
            locale = getDefaultLocale();
        }
        // Look for text in requested bundle.
        ResourceBundle rb = getBundle(bundleName, locale);
        value = getStringOrNull(rb, key);
        // Look for text in list of default bundles.
        if (value == null && bundleNames.length > 0)
        {
            String name;
            for (int i = 0; i < bundleNames.length; i++)
            {
                name = bundleNames[i];
                //System.out.println("getString(): name=" + name +
                //                   ", locale=" + locale + ", i=" + i);
                if (!name.equals(bundleName))
                {
                    rb = getBundle(name, locale);
                    value = getStringOrNull(rb, key);
                    if (value != null)
                    {
                        locale = rb.getLocale();
                        break;
                    }
                }
            }
        }
        if (value == null)
        {
            String loc = locale.toString();
            String mesg =
                LocalizationService.SERVICE_NAME
                    + " noticed missing resource: "
                    + "bundleName="
                    + bundleName
                    + ", locale="
                    + loc
                    + ", key="
                    + key;
            getLogger().debug(mesg);
            // Text not found in requested or default bundles.
            throw new MissingResourceException(mesg, bundleName, key);
        }
        return value;
    }
    
    /**
     * Returns the value for the key in the default bundle and the default locale.
     * 
     * @param key The key to retrieve the value for.
     * @return The value mapped to the key.
     */
    @Override
	public String getString(String key)
    {
        return getString(getDefaultBundleName(), getDefaultLocale(), key);
    }
    
    
    /**
     * Gets localized text from a bundle if it's there.  Otherwise,
     * returns <code>null</code> (ignoring a possible
     * <code>MissingResourceException</code>).
     * 
     * @param rb resource bundle 
     * @param key The key to retrieve the value for.
     * @return name of resource
     */
    protected final String getStringOrNull(ResourceBundle rb, String key)
    {
        if (rb != null)
        {
            try
            {
                return rb.getString(key);
            }
            catch (MissingResourceException ignored)
            {
                // ignore
            }
        }
        return null;
    }
    
    /**
     * {@link org.apache.fulcrum.localization.SimpleLocalizationService#format(String, Locale, String, Object)}
     * @param bundleName the bundle name
     * @param locale locale
     * @param key key to lookup
     * @param arg1 bundle arguments
     */
    @Override
	public String format(
        String bundleName,
        Locale locale,
        String key,
        Object arg1)
    {
        return format(bundleName, locale, key, new Object[] { arg1 });
    }
    
    /**
     * {@link org.apache.fulcrum.localization.SimpleLocalizationService#format(String, Locale, String, Object, Object)}
     */
    @Override
	public String format(
        String bundleName,
        Locale locale,
        String key,
        Object arg1,
        Object arg2)
    {
        return format(bundleName, locale, key, new Object[] { arg1, arg2 });
    }
    
    /**
     * Looks up the value for <code>key</code> in the
     * <code>ResourceBundle</code> referenced by
     * <code>bundleName</code>, then formats that value for the
     * specified <code>Locale</code> using <code>args</code>.
     * 
     * If <code>locale</code> is <code>null</code>, {@link #getDefaultLocale()} will be checked.
     * If <code>bundleName</code> is <code>null</code>, {@link #getDefaultBundleName()} will be checked (cft. {@link #getBundle(String, Locale)}.
     *
     * @return Localized, formatted text identified by
     * <code>key</code>.
     */
    @Override
	public String format(
        String bundleName,
        Locale locale,
        String key,
        Object[] args)
    {
        // When formatting Date objects and such, MessageFormat
        // cannot have a null Locale.
        Locale formatLocale = (locale == null) ? getDefaultLocale() : locale; 
        String value = getString(bundleName, locale, key);
        
        Object[] formatArgs = (args == null) ? NO_ARGS : args;
        
        MessageFormat messageFormat = new MessageFormat(value, formatLocale);
        return messageFormat.format(formatArgs);
    }
}
