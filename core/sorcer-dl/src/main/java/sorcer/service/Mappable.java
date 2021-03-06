/*
 * Copyright 2013 the original author or authors.
 * Copyright 2013 SorcerSoft.org.
 *  
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package sorcer.service;


import java.rmi.RemoteException;

/**
 * Implicit Maps such that a path indicates internal structure of composition
 * with the last inner component indicated by the path. Contexts, Exertions,
 * Vars are examples of mappables.
 * 
 * @author Mike Sobolewski
 */
public interface Mappable<T> extends Identifiable {

	/**
	 * Returns a execute at the path.
	 * 
	 * @param path
	 *            the attribute-based path
	 * @return this mappable execute
	 * @throws ContextException
	 */
	public T getValue(String path, Arg... args)
			throws ContextException, RemoteException;
	
	/**
	 * Returns a execute at the path as is with no reevaluation.
	 * 
	 * @param path
	 *            the attribute-based path
	 * @return this mappable execute with no reevaluation
	 * @throws ContextException
	 */
	public T asis(String path) throws ContextException;
		
	/**
	 * Maps the specified <code>path</code> to the specified <code>execute</code>
	 * in this mappable. Neither the path nor the execute can be
	 * <code>null</code>. <p>
	 * 
	 * The execute can be retrieved by calling the <code>execute</code> method with a
	 * path that is equal to the original path.
	 * 
	 * @param path the attribute-based path
	 * 
	 * @param value the new execute
	 * 
	 * @return the previous execute of the specified path in this mappable, or
	 * <code>null</code> if it did not have one
	 * 
	 * @throws ContextException
	 */
	public T putValue(String path, Object value) throws ContextException;

}
