/****************************************************************
 * Copyright (C) 2005 LAMS Foundation (http://lamsfoundation.org)
 * =============================================================
 * License Information: http://lamsfoundation.org/licensing/lams/2.0/
 * 
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 2.0 
 * as published by the Free Software Foundation.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301
 * USA
 * 
 * http://www.gnu.org/licenses/gpl.txt
 * ****************************************************************
 */
/* $$Id$$ */

package org.lamsfoundation.lams.cache;

/** Wraps up the JBOSS Cache in some simple methods to add, get and 
 * remove an object to/from the cache. Used for manually caching objects,
 * rather than Hibernate managed objects.
 * 
 * Each item in the cache is specified using a Fqn based on the object's classpath
 * a unique value. The Fqn can be generated by the CacheManager from the object's
 * class or the calling code can pass the object's type split into parts. The
 * latter may be handy if the object's type is always known. Use getPartsFromClass(Class clasz)
 * to generate the appropriate string array.
 * 
 * It is expected that it will be available to modules via a spring bean (singleton).
 * Therefore we will have one CacheManager per web-app. However the underlying cache 
 * will be the shared across the whole system so objects that are cached by one
 * module will be available via the cache to another module (should it know the right Class
 * and key).
 * 
 * If errors occur putting values in the cache, or getting values from the cache,
 * then they will be logged in the server log. Exceptions are not thrown - better
 * to have the system degrade if the cache isn't available, rather than failing.
 */
public interface ICacheManager {

	/** Get the String[] version of the objects class name. */
	public String[] getPartsFromClass(Class clasz); 

	/**
	 * Get an item based on key. Works out the Fqn from classNameParts.
	 * If key or classNameParts is null, then null is returned.
	 */
	public abstract Object getItem(String[] classNameParts, Object key);

	/**
	 * Get an item, of type clasz from the jboss cache, based on key. Works out the Fqn from the supplied Class type.
	 * If key or clasz is null, then null is returned.
	 */
	public abstract Object getItem(Class clasz, Object key);

	/**
	 * Cache an item, with the supplied key. Works out the Fqn from classNameParts.
	 * Will only cache if all parameters are not null.
	 */
	public abstract void addItem(String[] classNameParts, Object key,
			Object item);

	/**
	 * Cache an item, with the supplied key. Works out the Fqn from the clasz.
	 * Will only cache if all parameters are not null.
	 */
	public abstract void addItem(Class clasz, Object key, Object item);

	/** Clear all the nodes in the cache with the given key. 
	 * Works on nodes starting with /org, /com and /net 
	 */
	public abstract void clearCache(String node);
	
	/**
	 * Remove a particular item from the cache. 
	 */
	public abstract void removeItem(String[] classNameParts, Object key);

}