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

package org.overture.ego.controller.resolver;

import org.springframework.core.MethodParameter;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.UnsatisfiedServletRequestParameterException;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import java.util.HashMap;

public class PageableResolver implements HandlerMethodArgumentResolver {
  private static final int DEFAULT_LIMIT = 20;
  private static final int DEFAULT_PAGE_NUM = 0;

  @Override
  public boolean supportsParameter(MethodParameter methodParameter) {
    return methodParameter.getParameterType().equals(Pageable.class);
  }

  @Override
  public Object resolveArgument(MethodParameter methodParameter,
                                ModelAndViewContainer modelAndViewContainer,
                                NativeWebRequest nativeWebRequest,
                                WebDataBinderFactory webDataBinderFactory) throws Exception {

    // get paging parameters
    String limitParam     = nativeWebRequest.getParameter("limit");
    String pageParam      = nativeWebRequest.getParameter("page");
    String sortParam      = nativeWebRequest.getParameter("sort");
    String sortOrderParam = nativeWebRequest.getParameter("sortOrder");

    int page;
    try {
      page = StringUtils.isEmpty(pageParam) ? DEFAULT_PAGE_NUM : Integer.parseInt(pageParam);
      page = page >= 0 ? page : DEFAULT_PAGE_NUM;
    } catch (NumberFormatException e) {
      throw new UnsatisfiedServletRequestParameterException(new String[]{"page must be an integer"}, new HashMap<>());
    }

    int limit;
    try {
      limit = StringUtils.isEmpty(limitParam) ? DEFAULT_LIMIT : Integer.parseInt(limitParam);
      limit = limit > 0 ? limit : DEFAULT_LIMIT;
    } catch (NumberFormatException e) {
      throw new UnsatisfiedServletRequestParameterException(new String[]{"limit must be an integer"}, new HashMap<>());
    }

    // If sortOrder is provided, ensure it matches DESC or ASC
    if (!(StringUtils.isEmpty(sortOrderParam) || sortOrderParam.equals("DESC") || sortOrderParam.equals("ASC"))) {
      throw new MissingServletRequestParameterException("limit", "int");
    }
    Sort.Direction direction = Sort.Direction.DESC;
    if( (! StringUtils.isEmpty(sortOrderParam)) && "asc".equals(sortOrderParam.toLowerCase())){
      direction = Sort.Direction.ASC;
    }
    // TODO: this is a hack for now to provide default sort on id field
    // ideally we should not be making assumption about field name as "id" - it will break if field doesn't exist
    Sort sort = new Sort(direction, StringUtils.isEmpty(sortParam) ? "id" : sortParam);

    return new PageRequest(page, limit, sort);
  }
}
