package org.apache.fulcrum.template.jsp;


/*
 * Copyright 2001-2004 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License")
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
