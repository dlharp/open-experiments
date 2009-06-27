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
package org.sakaiproject.kernel.api.connections;
/**
 * 
 */
public enum ConnectionState {
  /**
   * The state is unknown
   */
  NONE(), 
  /**
   * The invite is pending.
   */
  PENDING(), 
  /**
   * The connection invited waiting for the invitee to accept.
   */
  INVITED(), 
  /**
   * The connection was accepted on both ends.
   */
  ACCEPTED(), 
  /**
   * The connection invitation was rejected.
   */
  REJECTED(), 
  /**
   * The connection invitation was ignored.
   */
  IGNORED(), 
  /**
   * The connection has been blocked from further invitations.
   */
  BLOCKED();
}