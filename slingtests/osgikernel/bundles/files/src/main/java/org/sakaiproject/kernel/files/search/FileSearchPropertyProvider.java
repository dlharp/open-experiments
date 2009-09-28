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
package org.sakaiproject.kernel.files.search;

import org.apache.jackrabbit.api.security.user.Group;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.request.RequestParameter;
import org.sakaiproject.kernel.api.files.FilesConstants;
import org.sakaiproject.kernel.api.personal.PersonalUtils;
import org.sakaiproject.kernel.api.search.SearchPropertyProvider;
import org.sakaiproject.kernel.api.site.SiteException;
import org.sakaiproject.kernel.api.site.SiteService;
import org.sakaiproject.kernel.util.JcrUtils;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.Value;

/**
 * Provides properties to process the search
 * 
 * @scr.component immediate="true" label="FileSearchPropertyProvider"
 *                description="Property provider for file searches"
 * @scr.property name="service.vendor" value="The Sakai Foundation"
 * @scr.property name="sakai.search.provider" value="Files"
 * @scr.service interface="org.sakaiproject.kernel.api.search.SearchPropertyProvider"
 * @scr.reference name="SiteService"
 *                interface="org.sakaiproject.kernel.api.site.SiteService"
 */
public class FileSearchPropertyProvider implements SearchPropertyProvider {

  private SiteService siteService;

  protected void bindSiteService(SiteService siteService) {
    this.siteService = siteService;
  }

  protected void unbindSiteService(SiteService siteService) {
    this.siteService = null;
  }

  public void loadUserProperties(SlingHttpServletRequest request,
      Map<String, String> propertiesMap) {

    Session session = request.getResourceResolver().adaptTo(Session.class);
    String user = request.getRemoteUser();

    // Path
    // If path equals mybookmarks then the results id's have to be in the mybookmars node.
    RequestParameter pathParam = request.getRequestParameter("path");
    String path = FilesConstants.USER_FILESTORE;
    if (pathParam != null) {
      path = pathParam.getString();

      // Only check the files that are in the user his bookmarks node.
      if (path.equals("mybookmarks")) {
        path = FilesConstants.USER_FILESTORE;

        String userPath = PersonalUtils.getPrivatePath(user, "");
        String bookmarksPath = userPath + "/mybookmarks";
        String ids = "and (@sakai:id=\"somenoneexistingid\")";
        try {
          if (session.itemExists(bookmarksPath)) {
            Node node = (Node) session.getItem(bookmarksPath);
            Value[] values = JcrUtils.getValues(node, "files");

            StringBuilder sb = new StringBuilder(" and (");

            for (Value val : values) {
              sb.append("@sakai:id=\"").append(val.getString()).append("\" or ");
            }

            ids = sb.toString();
            ids = ids.substring(0, ids.lastIndexOf(" or ")) + ")";
            propertiesMap.put("_ids", ids);
          } else {
            propertiesMap.put("_ids", ids);
          }
        } catch (RepositoryException e) {
          propertiesMap.put("_ids", ids);
        }
      }

      // Only check the files that are in sites where the user is part of.
      else if (path.equals("mysites")) {

        path = "/sites";
        String sites = "and (@sakai:site=\"somenoneexistingid\")";
        try {
          // Get the user his sites.
          Map<String, List<Group>> membership = siteService.getMembership(session, user);

          StringBuilder sb = new StringBuilder(" and (");
          for (Entry<String, List<Group>> site : membership.entrySet()) {
            sb.append("@sakai:site=\"").append(site.getKey()).append("\" or ");
          }
          sites = sb.toString();
          sites = sites.substring(0, sites.lastIndexOf(" or ")) + ")";
          propertiesMap.put("_sites", sites);

        } catch (SiteException e) {
          propertiesMap.put("_sites", sites);
        }
      }
    }
    propertiesMap.put("_path", path);

    // Tags
    String tags = "";
    RequestParameter[] tagsParams = request.getRequestParameters("sakai:tags");
    if (tagsParams != null) {
      StringBuilder sb = new StringBuilder(" and (");

      for (RequestParameter tag : tagsParams) {
        sb.append("sakai:tags=\"");
        sb.append(tag.getString());
        sb.append("\" and ");
      }
      tags = sb.substring(0, sb.length() - 5) + ")";
    }
    propertiesMap.put("_tags", tags);

    // Sorting order
    RequestParameter sortOnParam = request.getRequestParameter("sortOn");
    RequestParameter sortOrderParam = request.getRequestParameter("sortOrder");
    String sortOn = "sakai:filename";
    String sortOrder = "ascending";
    if (sortOrderParam != null
        && (sortOrderParam.getString().equals("ascending") || sortOrderParam.getString()
            .equals("descending"))) {
      sortOrder = sortOrderParam.getString();
    }
    if (sortOnParam != null) {
      sortOn = sortOnParam.getString();
    }
    String order = " order by @" + sortOn + " " + sortOrder;
    propertiesMap.put("_order", order);
    
    
    // Resource types
    RequestParameter typeParam = request.getRequestParameter("resource");
    String resourceTypes = "@sling:resourceType=\"sakai/link\" or @sling:resourceType=\"sakai/folder\"";
    if(typeParam != null) {
      String type = typeParam.getString();
      if ("link".equals(type)) {
        resourceTypes = "@sling:resourceType=\"sakai/link\"";
      }
      else if ("folder".equals(type)) {
        resourceTypes = "@sling:resourceType=\"sakai/folder\"";
      }
    }
    propertiesMap.put("_resourceTypes", resourceTypes);

  }
}