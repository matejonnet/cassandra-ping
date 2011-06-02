/*
* JBoss, Home of Professional Open Source
* Copyright $today.year Red Hat Inc. and/or its affiliates and other
* contributors as indicated by the @author tags. All rights reserved.
* See the copyright.txt in the distribution for a full listing of
* individual contributors.
* 
* This is free software; you can redistribute it and/or modify it
* under the terms of the GNU Lesser General Public License as
* published by the Free Software Foundation; either version 2.1 of
* the License, or (at your option) any later version.
* 
* This software is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
* Lesser General Public License for more details.
* 
* You should have received a copy of the GNU Lesser General Public
* License along with this software; if not, write to the Free
* Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
* 02110-1301 USA, or see the FSF site: http://www.fsf.org.
*/

package org.jboss.jgroups.cassandra.plugins;

import org.apache.cassandra.thrift.Cassandra;

/**
 * Client executor interface.
 *
 * @param <T> exact return type
 * @author <a href="mailto:ales.justin@jboss.org">Ales Justin</a>
 */
public interface ClientExecutor<T>
{
   /**
    * Execute action on Cassandra client.
    *
    * @param client the Cassandra client
    * @return op result, null if void
    * @throws Throwable for any error
    */
   T execute(Cassandra.Client client) throws Throwable;
}
