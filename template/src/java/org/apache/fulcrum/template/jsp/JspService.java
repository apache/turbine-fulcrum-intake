package org.apache.fulcrum.template.jsp;

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


/**
 * Implementations of the JspService interface.
 *
 * @author <a href="mailto:jmcnally@collab.net">John D. McNally</a>
 */
public interface JspService
{
    String ROLE = JspService.class.getName();

    /** The key used to store an instance of RunData in the request */
    public static final String RUNDATA = "rundata";
    /** The key used to store an instance of JspLink in the request */
    public static final String LINK = "link";

    /**
     * Adds some useful objects to the request, so they are available to the JSP.
     */
    //public void addDefaultObjects(RunData data);

    /**
     * executes the JSP given by templateName.
     */
    /*
    public void handleRequest(RunData data, String templateName, boolean isForward)
        throws ServiceException;
    */        

    /**
     * executes the JSP given by templateName.
     */
     /*
    public void handleRequest(RunData data, String templateName)
        throws ServiceException;
     */

    /**
     * The buffer size
     */
    public int getDefaultBufferSize();

    /**
     * Searchs for a template in the default.template path[s] and 
     * returns the template name with a relative path which is required
     * by <a href="http://java.sun.com/products/servlet/2.3/javadoc/javax/servlet/ServletContext.html#getRequestDispatcher(java.lang.String)">javax.servlet.RequestDispatcher</a>
     *
     * @param template The name of the template to search for.
     * @return the template with a relative path
     */
    public String getRelativeTemplateName(String template);

}