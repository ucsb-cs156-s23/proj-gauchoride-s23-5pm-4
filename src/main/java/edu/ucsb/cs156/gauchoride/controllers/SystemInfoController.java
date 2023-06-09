package edu.ucsb.cs156.gauchoride.controllers;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import edu.ucsb.cs156.gauchoride.models.SystemInfo;
import edu.ucsb.cs156.gauchoride.services.SystemInfoService;

/**
 * SystemInfoController returns information about the application; typically
 * the values of environment variables that may be needed by the frontend.
 */

@Api(description = "System Information")
@RequestMapping("/api/systemInfo")
@RestController
public class SystemInfoController extends ApiController {

    @Autowired
    private SystemInfoService systemInfoService;

    @ApiOperation(value = "Get global information about the application")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("")
    public SystemInfo getSystemInfo() {
        return systemInfoService.getSystemInfo();
    }

}