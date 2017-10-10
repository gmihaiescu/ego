package org.overture.ego.controller;

import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.extern.slf4j.Slf4j;
import org.overture.ego.model.entity.Application;
import org.overture.ego.security.ProjectCodeScoped;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/applications")
public class ApplicationController {

    @ProjectCodeScoped
    @RequestMapping(method = RequestMethod.GET, value = "")
    @ApiResponses(
            value = {
                    @ApiResponse(code = 200, message = "List of applications", response = Application.class, responseContainer = "List")
            }
    )
    public @ResponseBody
    List<Application> getApplicationsList(
            @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = true) final String accessToken,
            @RequestParam(value = "offset", required = true) long offset,
            @RequestParam(value = "count", required = false) short count) {
        return null;
    }

    @ProjectCodeScoped
    @RequestMapping(method = RequestMethod.GET, value = "/search")
    @ApiResponses(
            value = {
                    @ApiResponse(code = 200, message = "List of applications", response = Application.class, responseContainer = "List")
            }
    )
    public @ResponseBody
    List<Application> findApplications(
            @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = true) final String accessToken,
            @RequestParam(value = "query", required = true) String query,
            @RequestParam(value = "count", required = false) short count) {
        return null;
    }

    @ProjectCodeScoped
    @RequestMapping(method = RequestMethod.POST, value = "")
    @ApiResponses(
            value = {
                    @ApiResponse(code = 200, message = "New Application", response = Application.class)
            }
    )
    public @ResponseBody
    Application createApplication(
            @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = true) final String accessToken,
            @RequestBody(required = true) Application applicationInfo) {
        return null;
    }


    @ProjectCodeScoped
    @RequestMapping(method = RequestMethod.GET, value = "/{id}")
    @ApiResponses(
            value = {
                    @ApiResponse(code = 200, message = "Application Details", response = Application.class)
            }
    )
    public @ResponseBody
    Application getApplication(
            @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = true) final String accessToken,
            @PathVariable(value = "id", required = true) String applicationId) {
        return null;
    }


    @ProjectCodeScoped
    @RequestMapping(method = RequestMethod.PUT, value = "/{id}")
    @ApiResponses(
            value = {
                    @ApiResponse(code = 200, message = "Updated application info", response = Application.class)
            }
    )
    public @ResponseBody
    Application updateApplication(
            @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = true) final String accessToken,
            @PathVariable(value = "id", required = true) String applicationId,
            @RequestBody(required = true) Application updatedApplicationInfo) {
        return null;
    }

    @ProjectCodeScoped
    @RequestMapping(method = RequestMethod.DELETE, value = "/{id}")
    @ResponseStatus(value = HttpStatus.OK)
    public void deleteApplication(
            @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = true) final String accessToken,
            @PathVariable(value = "id", required = true) String applicationId) {
    }

}