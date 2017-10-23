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

package org.overture.ego.service;

import lombok.val;
import org.overture.ego.model.Page;
import org.overture.ego.model.PageInfo;
import org.overture.ego.model.entity.Application;
import org.overture.ego.model.entity.Group;
import org.overture.ego.model.entity.User;
import org.overture.ego.repository.ApplicationRepository;
import org.overture.ego.repository.GroupAppRepository;
import org.overture.ego.repository.GroupRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class GroupService {

  @Autowired
  GroupRepository groupRepository;
  @Autowired
  ApplicationRepository applicationRepository;
  @Autowired
  GroupAppRepository groupAppRepository;

  public Group create(Group groupInfo) {
    groupRepository.create(groupInfo);
    return groupRepository.getByName(groupInfo.getName());
  }

  public void addAppsToGroups(String grpId, List<String> appNames){
    val group = groupRepository.read(Integer.parseInt(grpId));
    appNames.forEach(appName -> groupAppRepository.add(group.getName(),appName));
  }

  public Group get(String groupId, boolean fullInfo) {
    int groupID = Integer.parseInt(groupId);
    if (groupRepository.read(groupID) == null) {
      return null;
    }
    else {
      val group = groupRepository.read(groupID);
      if(fullInfo){
        addAppInfo(group);
      }
      return group;
    }
  }

  public Group getByName(String groupName, boolean fullInfo) {

    val group = groupRepository.getByName(groupName);
    if(fullInfo){
      addAppInfo(group);
    }
    return group;
  }

  public Group update(Group updatedGroupInfo) {
    groupRepository.update(updatedGroupInfo);
    return updatedGroupInfo;
  }

  public void delete(String groupId) {
    int groupID = Integer.parseInt(groupId);

    groupRepository.delete(groupID);
  }

  public Page<Group> listGroups(PageInfo pageInfo) {
    val groups = groupRepository.getAllGroups(pageInfo);
    return Page.getPageFromPageInfo(pageInfo,groups);
  }

  public void addAppInfo(Group group){
    val apps = new ArrayList<Application>();
    group.getApplicationNames().forEach(appName -> apps.add(applicationRepository.getByName(appName)));
    group.setApplications(apps);
  }



}
