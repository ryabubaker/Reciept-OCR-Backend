package com.example.receipt_backend.controller;
import com.example.receipt_backend.dto.request.CompanyRequestDTO;
import com.example.receipt_backend.dto.response.CompanyResponseDTO;
import com.example.receipt_backend.service.CompanyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/companies")
public class CompanyController {

    @Autowired
    private CompanyService companyService;

   // @PreAuthorize("hasRole('ROLE_SYSTEM_ADMIN')")
    @PostMapping
    public ResponseEntity<CompanyResponseDTO> createCompany(@Validated @RequestBody CompanyRequestDTO companyRequest) {
        CompanyResponseDTO createdCompany = companyService.createCompany(companyRequest);
        return new ResponseEntity<>(createdCompany, HttpStatus.CREATED);
    }

   // @PreAuthorize("hasAnyRole('ROLE_SYSTEM_ADMIN', 'ROLE_COMPANY_ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<CompanyResponseDTO> updateCompany(@PathVariable Long id, @Validated @RequestBody CompanyRequestDTO companyRequest) {
        CompanyResponseDTO updatedCompany = companyService.updateCompany(id, companyRequest);
        return new ResponseEntity<>(updatedCompany, HttpStatus.OK);
    }

    //@PreAuthorize("hasRole('ROLE_SYSTEM_ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCompany(@PathVariable Long id) {
        companyService.deleteCompany(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

   // @PreAuthorize("hasAnyRole('ROLE_SYSTEM_ADMIN', 'ROLE_COMPANY_ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<CompanyResponseDTO> getCompanyById(@PathVariable Long id) {
        CompanyResponseDTO company = companyService.getCompanyById(id);
        return new ResponseEntity<>(company, HttpStatus.OK);
    }

//@PreAuthorize("hasRole('ROLE_SYSTEM_ADMIN')")
    @GetMapping
    public ResponseEntity<List<CompanyResponseDTO>> getAllCompanies() {
        List<CompanyResponseDTO> companies = companyService.getAllCompanies();
        return new ResponseEntity<>(companies, HttpStatus.OK);
    }
}
