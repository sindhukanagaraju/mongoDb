package com.microservice.organizationservice.service;

import com.microservice.commonservice.exception.BadRequestServiceAlertException;
import com.microservice.commonservice.util.Constant;
import com.microservice.organizationservice.entity.Organization;
import com.microservice.organizationservice.repository.OrganizationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class OrganizationService {

    private final OrganizationRepository organizationRepository;

    public OrganizationService(final OrganizationRepository organizationRepository) {
        this.organizationRepository = organizationRepository;
    }

    @Transactional
    public Organization createOrganization(final Organization organization) {
        Organization existingOrg = organizationRepository.findByEmail(organization.getEmail());

        if (existingOrg != null) {
            throw new BadRequestServiceAlertException("Email already exists");
        }

        return this.organizationRepository.save(organization);
    }

    public Organization retrieveOrganizationById(final String uuid) {
        return this.organizationRepository.findById(uuid).orElseThrow(() -> new BadRequestServiceAlertException(Constant.ID_DOES_NOT_EXIST));
    }

    public List<Organization> retrieveOrganization() {
        return this.organizationRepository.findAll();
    }

    @Transactional
    public Organization patchOrganizationById(final Organization organization, final String uuid) {
        final Organization existingOrganization = this.organizationRepository.findById(uuid).orElseThrow(() -> new BadRequestServiceAlertException(Constant.ID_DOES_NOT_EXIST));
        if (organization.getName() != null) {
            existingOrganization.setName(organization.getName());
        }
        if (organization.getEmail() != null) {
            existingOrganization.setEmail(organization.getEmail());
        }
        if (organization.getContactNumber() != null) {
            existingOrganization.setContactNumber(organization.getContactNumber());
        }

        if (organization.getIndustryType() != null) {
            existingOrganization.setIndustryType(organization.getIndustryType());
        }
        return this.organizationRepository.save(existingOrganization);
    }

    public String removeOrganizationById(final String uuid) {
        final Organization organization = this.organizationRepository.findById(uuid).orElseThrow(() -> new BadRequestServiceAlertException(Constant.ID_DOES_NOT_EXIST));
        this.organizationRepository.delete(organization);
        return Constant.REMOVE;
    }
}
