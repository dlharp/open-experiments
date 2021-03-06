/*
 * Licensed to the Sakai Foundation (SF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The SF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package org.sakaiproject.kernel.api.ldap;

import com.novell.ldap.LDAPConnection;

import java.util.List;

/**
 * Central broker to handle connection managers.
 */
public interface LdapConnectionBroker {
  /**
   * Gets a connection from a named manager. If the manager does not already
   * exist, a new one is created using system level configuration defaults. Same
   * as calling getConnection(name, null).
   *
   * @param name
   *          The name of the connection manager.
   */
  LdapConnectionManager create(String name) throws LdapException;

  /**
   * Gets a connection from a named manager. If the manager does not already
   * exist, a new one is created using the provided configuration settings. Same
   * as calling getConnection(name, config, false).
   *
   * @param name
   *          The name of the connection manager.
   * @param config
   * @return {@link LDAPConnection} from the named manager.
   */
  LdapConnectionManager create(String name, LdapConnectionManagerConfig config)
      throws LdapException;

  /**
   * Destroy a named connection manager.
   *
   * @param name
   *          The name of the connection manager to destroy.
   */
  void destroy(String name);

  /**
   * Check if a named manager exists.
   *
   * @param name
   *          The name of the manager to check.
   * @return true if exists, false otherwise.
   */
  boolean exists(String name);

  /**
   * Gets a connection from a named manager. If the manager does not already
   * exist, {@link create(String)} is called then a connection taken from it.
   *
   * @param name
   *          The name of the connection manager.
   * @return {@link LDAPConnection} from the named manager.
   * @throws LdapException
   *           If named connection manager is not found. Also wraps underlying
   *           exceptions.
   */
  LDAPConnection getConnection(String name) throws LdapException;

  /**
   * Gets a bound connection from a named manager. If the manager does not
   * already exist, {@link create(String)} is called then a connection taken
   * from it.
   *
   * @param name
   *          The name of the connection manager.
   * @return {@link LDAPConnection} from the named manager.
   * @throws LdapException
   *           If named connection manager is not found. Also wraps underlying
   *           exceptions.
   */
  LDAPConnection getBoundConnection(String name) throws LdapException;

  /**
   * Gets a copy of the default configuration settings for LDAP connections.
   * Editing the returned value does not modify the default configuration
   * settings of the broker.
   *
   * @return The default configuration settings as set in the central property
   *         manager.
   */
  LdapConnectionManagerConfig getDefaultConfig();

  /**
   * Get the validators to be used to validate the liveness of a connection.
   * 
   * @return A list of connection liveness validators, or an empty list if none
   *         available. Does not return null.
   */
  List<LdapConnectionLivenessValidator> getLivenessValidators();
}
