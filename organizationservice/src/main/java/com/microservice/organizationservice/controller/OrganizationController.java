package com.microservice.organizationservice.controller;

import com.microservice.commonservice.dto.ResponseDTO;
import com.microservice.commonservice.util.Constant;
import com.microservice.organizationservice.entity.Organization;
import com.microservice.organizationservice.service.OrganizationService;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
public class OrganizationController {

    private final OrganizationService organizationService;

    public OrganizationController(final OrganizationService organizationService) {
        this.organizationService = organizationService;
    }

    @PreAuthorize("hasAnyAuthority('SUPER_ADMIN')")
    @PostMapping("/organization")
    public ResponseDTO createOrganization(@RequestBody final Organization organization) {
        return new ResponseDTO(HttpStatus.OK.value(), Constant.CREATE, this.organizationService.createOrganization(organization));
    }

    @PreAuthorize("hasAnyAuthority('SUPER_ADMIN')")
    @GetMapping("/organization/{uuid}")
    public ResponseDTO retrieveOrganizationById(@PathVariable final String uuid) {
        return new ResponseDTO(HttpStatus.OK.value(), Constant.RETRIEVE, this.organizationService.retrieveOrganizationById(uuid));
    }

    @PreAuthorize("hasAnyAuthority('SUPER_ADMIN')")
    @GetMapping("/organization")
    public ResponseDTO retrieveOrganization() {
        return new ResponseDTO(HttpStatus.OK.value(), Constant.RETRIEVE, this.organizationService.retrieveOrganization());
    }

    @PreAuthorize("hasAnyAuthority('SUPER_ADMIN')")
    @PatchMapping("/organization/{uuid}")
    public ResponseDTO patchOrganization(@PathVariable final String uuid, @RequestBody final Organization organization) {
        return new ResponseDTO(HttpStatus.OK.value(), Constant.UPDATE, this.organizationService.patchOrganizationById(organization, uuid));
    }

    @PreAuthorize("hasAnyAuthority('SUPER_ADMIN')")
    @DeleteMapping("/organization/{uuid}")
    public ResponseDTO removeOrganizationById(@PathVariable final String uuid) {
        return new ResponseDTO(HttpStatus.OK.value(), Constant.REMOVE, this.organizationService.removeOrganizationById(uuid));
    }
}
