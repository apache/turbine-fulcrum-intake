package org.apache.fulcrum;

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

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Properties;
import org.apache.log4j.Category;
import org.apache.log4j.Priority;
import org.apache.log4j.PropertyConfigurator;

/**
 * This is a stopgap measure to try and separate the services
 * from Turbine. All these little bits and pieces should be
 * components in a component model. Same idea as Avalon where
 * a component can have certain values set. For example, the
 * connection pool and db service classes should all a logging
 * category to be set so that we don't have to use these
 * static classes for logging.
 *
 * @author <a href="jvanzyl@apache.org">Jason van Zyl</a>
 */
public class Log
{
    /**
     * This is the default logger.
     */
    private static Category defaultLogger;

    /**
     * This is a collection of log4j categories.
     */
    private static Hashtable loggers;

    /**
     * The name of the category that will be
     * used for default logging.
     */
    private static final String DEFAULT_CATEGORY = "default";

    public static void setCategory(Category category)
    {
        defaultLogger = category;
    }

    public static void setCategoryTable(Hashtable h)
    {
        loggers = h;
    }

    /**
     * Retrieve a category from our configured set
     * of Categories.
     *
     * @param String logger name
     * @return Category
     */
    public static Category getLogger(String logger)
    {
        return (Category) loggers.get(logger);
    }

    /**
     * This is a log method with logLevel == DEBUG, printing is done by
     * the default logger
     */
    public static void debug(String message)
    {
        defaultLogger.debug(message);
    }

    /**
     * This is a log method with logLevel == DEBUG, printing is done by
     * the default logger
     */
    public static void debug(String message, Throwable t)
    {
        defaultLogger.debug(message,t);
    }

    /**
     * This is a log method with logLevel == DEBUG, printing is done by
     * the given logger
     */
    public static void debug(String logName, String message)
    {
        Category logger = getLogger(logName);

        if (logger == null)
        {
            debug(message);
        }
        else
        {
            logger.debug(message);
        }
    }

    /**
     * This is a log method with logLevel == DEBUG, printing is done by
     * the given logger
     */
    public static void debug(String logName ,String message, Throwable t)
    {
        Category logger = getLogger(logName);

        if (logger == null)
        {
            debug(message,t);
        }
        else
        {
            logger.debug(message,t);
        }
    }

    /**
     * This is a log method with logLevel == INFO, printing is done by
     * the default logger
     */
    public static void info(String message)
    {
        defaultLogger.info(message);
    }

    /**
     * This is a log method with logLevel == INFO, printing is done by
     * the default logger
     */
    public static void info(String message, Throwable t)
    {
        defaultLogger.info(message,t);
    }

    /**
     * This is a log method with logLevel == INFO, printing is done by
     * the given logger
     */
    public static void info(String logName, String message)
    {
        Category logger = getLogger(logName);

        if (logger == null)
        {
            info(message);
        }
        else
        {
            logger.info(message);
        }
    }

    /**
     * This is a log method with logLevel == INFO, printing is done by
     * the given logger
     */
    public static void info(String logName, String message, Throwable t)
    {
        Category logger = getLogger(logName);

        if (logger == null)
        {
            info(message,t);
        }
        else
        {
            logger.info(message,t);
        }
    }

    /**
     * This is a log method with logLevel == WARN, printing is done by
     * the default logger
     */
    public static void warn(String message)
    {
        defaultLogger.warn(message);
    }

    /**
     * This is a log method with logLevel == WARN, printing is done by
     * the default logger
     */
    public static void warn(String message, Throwable t)
    {
        defaultLogger.warn(message,t);
    }

    /**
     * This is a log method with logLevel == WARN, printing is done by
     * the given logger
     */
    public static void warn(String logName, String message)
    {
        Category logger = getLogger(logName);

        if (logger == null)
        {
            warn(message);
        }
        else
        {
            logger.warn(message);
        }
    }

    /**
     * This is a log method with logLevel == WARN, printing is done by
     * the given logger
     */
    public static void warn(String logName, String message, Throwable t)
    {
        Category logger = getLogger(logName);

        if (logger == null)
        {
            warn(message,t);
        }
        else
        {
            logger.warn(message,t);
        }
    }

    /**
     * This is a log method with logLevel == ERROR, printing is done by
     * the default logger
     */
    public static void error(String message)
    {
        defaultLogger.error(message);
    }

    /**
     * This is a log method with logLevel == ERROR, printing is done by
     * the default logger
     */
    public static void error(String message, Throwable t)
    {
        defaultLogger.error(message,t);
    }

    /**
     * This is a log method with logLevel == ERROR, printing is done by
     * the given logger
     */
    public static void error(String logName, String message)
    {
        Category logger = getLogger(logName);

        if (logger == null)
        {
            error(message);
        }
        else
        {
            logger.error(message);
        }
    }

    /**
     * This is a log method with logLevel == ERROR, printing is done by
     * the given logger
     */
    public static void error(String logName, String message, Throwable t)
    {
        Category logger = getLogger(logName);

        if (logger == null)
        {
            error(message,t);
        }
        else
        {
            logger.error(message,t);
        }
    }

    /**
     * This is a log method with logLevel == ERROR, printing is done by
     * the default logger
     */
    public static void error(Throwable e)
    {
        error("", e);
    }
}
