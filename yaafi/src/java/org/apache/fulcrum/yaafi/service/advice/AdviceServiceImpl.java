package org.apache.fulcrum.yaafi.service.advice;

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

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;

import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.configuration.Reconfigurable;
import org.apache.avalon.framework.context.Context;
import org.apache.avalon.framework.context.ContextException;
import org.apache.avalon.framework.context.Contextualizable;
import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.avalon.framework.service.ServiceException;
import org.apache.avalon.framework.service.ServiceManager;
import org.apache.avalon.framework.service.Serviceable;
import org.apache.fulcrum.yaafi.framework.interceptor.AvalonInterceptorFactory;
import org.apache.fulcrum.yaafi.framework.interceptor.AvalonInterceptorInvocationHandler;
import org.apache.fulcrum.yaafi.framework.util.Validate;

/**
 * Simple service providing interceptor advices for ordinary POJOs. Since the
 * implementation uses Dynamic Proxies only methods invoked by an interface can
 * be advised.
 *
 * @author <a href="mailto:siegfried.goeschl@it20one.at">Siegfried Goeschl</a>
 */

public class AdviceServiceImpl extends AbstractLogEnabled
		implements AdviceService, Serviceable, Contextualizable, Reconfigurable {
	/** the service manager supplied by the Avalon framework */
	private ServiceManager serviceManager;

	/** the list of default interceptors */
	private String[] defaultInterceptorList;

	/////////////////////////////////////////////////////////////////////////
	// Avalon Service Lifecycle Implementation
	/////////////////////////////////////////////////////////////////////////

	/**
	 * @see org.apache.avalon.framework.service.Serviceable#service(org.apache.avalon.framework.service.ServiceManager)
	 */
	public void service(ServiceManager serviceManager) throws ServiceException {
		this.serviceManager = serviceManager;
	}

	/**
	 * @see org.apache.avalon.framework.context.Contextualizable#contextualize(org.apache.avalon.framework.context.Context)
	 */
	public void contextualize(Context context) throws ContextException {
		// nothing to do
	}

	/**
	 * @see org.apache.avalon.framework.configuration.Configurable#configure(org.apache.avalon.framework.configuration.Configuration)
	 */
	public void configure(Configuration configuration) throws ConfigurationException {
		Configuration[] interceptorConfigList = configuration.getChild("interceptors").getChildren("interceptor");
		this.defaultInterceptorList = new String[interceptorConfigList.length];
		for (int i = 0; i < interceptorConfigList.length; i++) {
			this.defaultInterceptorList[i] = interceptorConfigList[i].getValue();
		}
	}

	/**
	 * @see org.apache.avalon.framework.configuration.Reconfigurable#reconfigure(org.apache.avalon.framework.configuration.Configuration)
	 */
	public void reconfigure(Configuration configuration) throws ConfigurationException {
		this.configure(configuration);
	}

	/////////////////////////////////////////////////////////////////////////
	// Service interface implementation
	/////////////////////////////////////////////////////////////////////////

	/**
	 * @see org.apache.fulcrum.yaafi.service.advice.AdviceService#advice(java.lang.Object)
	 */
	public Object advice(Object object) {
		Validate.notNull(object, "object");
		return this.advice(this.getDefaultInterceptorList(), object);
	}

	/**
	 * @see org.apache.fulcrum.yaafi.service.advice.AdviceService#advice(java.lang.String,
	 *      java.lang.Object)
	 */
	public Object advice(String name, Object object) {
		Validate.notNull(object, "object");
		return this.doAdvice(name, this.getDefaultInterceptorList(), object);
	}

	/**
	 * @see org.apache.fulcrum.yaafi.service.advice.AdviceService#advice(java.lang.String[],
	 *      java.lang.Object)
	 */
	public Object advice(String[] interceptorList, Object object) {
		Validate.notNull(object, "object");
		String className = object.getClass().getName();
		return this.doAdvice(className, interceptorList, object);
	}

	/**
	 * @see org.apache.fulcrum.yaafi.service.advice.AdviceService#advice(java.lang.String,
	 *      java.lang.String[], java.lang.Object)
	 */
	public Object advice(String name, String[] interceptorList, Object object) {
		Validate.notNull(object, "object");
		return this.doAdvice(name, interceptorList, object);
	}

	/**
	 * @see org.apache.fulcrum.yaafi.service.advice.AdviceService#isAdviced(java.lang.Object)
	 */
	public boolean isAdviced(Object object) {
		InvocationHandler invocationHandler = null;

		if ((object != null) && Proxy.isProxyClass(object.getClass())) {
			invocationHandler = Proxy.getInvocationHandler(object);
			return invocationHandler instanceof AvalonInterceptorInvocationHandler;
		}

		return false;
	}

	/////////////////////////////////////////////////////////////////////////
	// Service implementation
	/////////////////////////////////////////////////////////////////////////

	/**
	 * Does the actual work of advising the object.
	 *
	 * @param name            the name of the object to be advised
	 * @param interceptorList the list of interceptor services to advise the object
	 * @param object          the object to be advised
	 * @return the advised object.
	 */
	protected Object doAdvice(String name, String[] interceptorList, Object object) {
		Validate.notEmpty(name, "name");
		Validate.notNull(interceptorList, "interceptorList");
		Validate.notNull(object, "object");

		Object result = null;
		String clazzName = object.getClass().getName();

		// do nothing if no interceptor services are requested

		if (interceptorList.length == 0) {
			if (this.getLogger().isInfoEnabled()) {
				String msg = "Skipping creation of dynamic proxy since no interceptors are requested : " + name;
				this.getLogger().info(msg);
			}

			return object;
		}

		// skip creating a dynamic proxy if it is already advised

		if (this.isAdviced(object)) {
			if (this.getLogger().isInfoEnabled()) {
				String msg = "Skipping creation of dynamic proxy since it is already advised : " + name;
				this.getLogger().info(msg);
			}

			return object;
		}

		// create the advised object

		try {
			result = AvalonInterceptorFactory.create(clazzName, name, this.getServiceManager(), interceptorList,
					object);
		} catch (ServiceException e) {
			String msg = "Unable to advice the object : " + name;
			this.getLogger().error(msg, e);
			throw new IllegalArgumentException(msg);
		}

		return result;
	}

	/**
	 * @return Returns the serviceManager.
	 */
	private ServiceManager getServiceManager() {
		return serviceManager;
	}

	/**
	 * @return Returns the defaultInterceptorList.
	 */
	private String[] getDefaultInterceptorList() {
		return defaultInterceptorList;
	}
}
