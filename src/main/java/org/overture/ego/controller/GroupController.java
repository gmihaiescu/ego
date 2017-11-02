/*
 * Copyright (c) 2017. The Ontario Institute for Cancer Research. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
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

package org.overture.ego.controller;

import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.overture.ego.model.Page;
import org.overture.ego.model.QueryInfo;
import org.overture.ego.model.entity.Application;
import org.overture.ego.model.entity.Group;
import org.overture.ego.model.entity.User;
import org.overture.ego.security.AdminScoped;
import org.overture.ego.service.GroupApplicationService;
import org.overture.ego.service.GroupService;
import org.overture.ego.service.UserGroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/groups")
public class GroupController {


  /**
   * Dependencies
   */
  @Autowired
  GroupService groupService;
  @Autowired
  UserGroupService userGroupService;
  @Autowired
  GroupApplicationService groupApplicationService;

  @AdminScoped
  @RequestMapping(method = RequestMethod.GET, value = "")
  @ApiResponses(
      value = {
          @ApiResponse(code = 200, message = "Page of groups", response = Page.class)
      }
  )
  public @ResponseBody
  Page<Group> getGroupsList(
      @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = true) final String accessToken,
      @RequestParam(value = "query", required = false) String query,
      QueryInfo queryInfo) {
    if(query != null  && query.isEmpty() ==  false){
      return groupService.findGroups(queryInfo, query.toLowerCase());
    } else {
      return groupService.listGroups(queryInfo);
    }
  }

  @AdminScoped
  @RequestMapping(method = RequestMethod.POST, value = "")
  @ApiResponses(
      value = {
          @ApiResponse(code = 200, message = "New Group", response = Group.class)
      }
  )
  public @ResponseBody
  Group createGroup(
      @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = true) final String accessToken,
      @RequestBody(required = true) Group groupInfo) {
    return groupService.create(groupInfo);
  }


  @AdminScoped
  @RequestMapping(method = RequestMethod.GET, value = "/{id}")
  @ApiResponses(
      value = {
          @ApiResponse(code = 200, message = "Group Details", response = Group.class)
      }
  )
  public @ResponseBody
  Group getGroup(
      @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = true) final String accessToken,
      @PathVariable(value = "id", required = true) String groupId) {
    return groupService.get(groupId, false);
  }


  @AdminScoped
  @RequestMapping(method = RequestMethod.PUT, value = "/{id}")
  @ApiResponses(
      value = {
          @ApiResponse(code = 200, message = "Updated group info", response = Group.class)
      }
  )
  public @ResponseBody
  Group updateGroup(
      @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = true) final String accessToken,
      @RequestBody(required = true) Group updatedGroupInfo) {
    return groupService.update(updatedGroupInfo);
  }

  @AdminScoped
  @RequestMapping(method = RequestMethod.DELETE, value = "/{id}")
  @ResponseStatus(value = HttpStatus.OK)
  public void deleteGroup(
      @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = true) final String accessToken,
      @PathVariable(value = "id", required = true) String groupId) {
    groupService.delete(groupId);
  }

  // APPLICATIONS
  @AdminScoped
  @RequestMapping(method = RequestMethod.GET, value = "/{id}/applications")
  @ApiResponses(
          value = {
                  @ApiResponse(code = 200, message = "Page of applications of group", response = Page.class)
          }
  )
  public @ResponseBody
  Page<Application> getGroupsApplications(
          @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = true) final String accessToken,
          @PathVariable(value = "id", required = true) String groupId,
          @RequestParam(value = "query", required = false) String query,
          QueryInfo queryInfo)
  {
    if(query != null  && query.isEmpty() ==  false){
      return groupApplicationService.findGroupsApplications(queryInfo,groupId, query.toLowerCase());
    } else {
      return groupApplicationService.getGroupsApplications(queryInfo,groupId);
    }
  }

  @AdminScoped
  @RequestMapping(method = RequestMethod.POST, value = "/{id}/applications")
  @ApiResponses(
          value = {
                  @ApiResponse(code = 200, message = "Add Apps to Group", response = String.class)
          }
  )
  public @ResponseBody
  String addAppsToGroups(
          @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = true) final String accessToken,
          @PathVariable(value = "id", required = true) String grpId,
          @RequestBody(required = true) List<String> apps) {
    groupService.addAppsToGroups(grpId,apps);
    return apps.size() + " apps added successfully.";
  }


  @AdminScoped
  @RequestMapping(method = RequestMethod.DELETE, value = "/{id}/applications/{appIDs}")
  @ApiResponses(
          value = {
                  @ApiResponse(code = 200, message = "Delete Apps from Group")
          }
  )
  @ResponseStatus(value = HttpStatus.OK)
  public void deleteAppsFromGroup(
          @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = true) final String accessToken,
          @PathVariable(value = "id", required = true) String grpId,
          @PathVariable(value = "appIDs", required = true) List<String> appIDs) {
    groupService.deleteAppsFromGroup(grpId,appIDs);
  }

  // USERS
  @AdminScoped
  @RequestMapping(method = RequestMethod.GET, value = "/{id}/users")
  @ApiResponses(
          value = {
                  @ApiResponse(code = 200, message = "Page of users of group", response = Page.class)
          }
  )
  public @ResponseBody
  Page<User> getGroupsUsers(
          @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = true) final String accessToken,
          @PathVariable(value = "id", required = true) String groupId,
          @RequestParam(value = "query", required = false) String query,
          QueryInfo queryInfo)
  {
    if(query != null  && query.isEmpty() ==  false){
      return userGroupService.findGroupsUsers(queryInfo,groupId, query.toLowerCase());
    } else {
      return userGroupService.getGroupsUsers(queryInfo,groupId);
    }
  }
}
