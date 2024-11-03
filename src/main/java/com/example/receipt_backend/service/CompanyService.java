package com.example.receipt_backend.service;

import com.example.receipt_backend.dto.request.CompanyRequestDTO;
import com.example.receipt_backend.dto.response.CompanyResponseDTO;

import java.util.List;

public interface CompanyService {
    CompanyResponseDTO createCompany(CompanyRequestDTO companyRequest);

    CompanyResponseDTO updateCompany(Long id, CompanyRequestDTO companyRequest);

    void deleteCompany(Long id);

    CompanyResponseDTO getCompanyById(Long id);

    List<CompanyResponseDTO> getAllCompanies();
}
