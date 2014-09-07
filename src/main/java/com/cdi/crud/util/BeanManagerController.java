/*
 * Copyright 2011-2014 Conventions Framework.
 * 
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
 * 
 */
package com.cdi.crud.util;

import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.lang.annotation.Annotation;

/**
 *
 * @author rpestano
 */
public class BeanManagerController {
	
	private static BeanManager beanManager;

    public static BeanManager getBeanManager() {
    	
    	if(beanManager == null){
    		 try {
    	            InitialContext initialContext = new InitialContext();
    	           beanManager = (BeanManager) initialContext.lookup("java:comp/BeanManager");
    	        } catch (NamingException e) {
    	            e.printStackTrace();
    	            return null;
    	        }
    	}
    	return beanManager;
       
    }
    
    public static void setBeanManager(String jndi){
    	 try {
	            InitialContext initialContext = new InitialContext();
	            beanManager = (BeanManager) initialContext.lookup(jndi);
	        } catch (NamingException e) {
	            e.printStackTrace();
	        }
    }


    public static <T> T getBeanByType(Class<T> type) {

        BeanManager bm = getBeanManager();
        Bean bean = bm.getBeans(type).iterator().next();
        CreationalContext ctx = bm.createCreationalContext(bean); // could be inlined below
        T o = (T) bm.getReference(bean, type, ctx); // could be inlined with return
        return o;
    }

    public static <T> T getBeanByName(String name) {
        BeanManager bm = getBeanManager();
        Bean bean = bm.getBeans(name).iterator().next();
        CreationalContext ctx = bm.createCreationalContext(bean); // could be inlined below
        T o = (T) bm.getReference(bean, bean.getBeanClass(), ctx); // could be inlined with return
        return o;
    }

    public static <T> T getBeanByNameAndType(String name,Class<T>type ) {
        BeanManager bm = getBeanManager();
        Bean bean = bm.getBeans(name).iterator().next();
        CreationalContext ctx = bm.createCreationalContext(bean); // could be inlined below
        T o = (T) bm.getReference(bean, type, ctx); // could be inlined with return
        return o;
    }


    public static <T> T getBeanByTypeAndQualifier(Class<T> type, final Class<? extends Annotation> qualifier) {
        BeanManager bm = getBeanManager();
        Bean bean = bm.getBeans(type,new Annotation() {
            @Override
            public Class<? extends Annotation> annotationType() {
                return qualifier;
            }
        }).iterator().next();
        CreationalContext ctx = bm.createCreationalContext(bean); // could be inlined below
        T o = (T) bm.getReference(bean, type, ctx); // could be inlined with return
        return o;
    }

}
