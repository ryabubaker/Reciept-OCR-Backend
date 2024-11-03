package com.example.receipt_backend.service.impl;

import com.example.receipt_backend.dto.request.CompanyRequestDTO;
import com.example.receipt_backend.dto.response.CompanyResponseDTO;
import com.example.receipt_backend.entity.Tenant;
import com.example.receipt_backend.mapper.CompanyMapper;
import com.example.receipt_backend.repository.CompanyRepository;
import com.example.receipt_backend.service.CompanyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class CompanyServiceImpl implements CompanyService {

    @Autowired
    private CompanyRepository companyRepository;

    @Autowired
    private CompanyMapper companyMapper;

    @Override
    public CompanyResponseDTO createCompany(CompanyRequestDTO companyRequest) {
        Tenant company = companyMapper.toEntity(companyRequest);
        return companyMapper.toDto(companyRepository.save(company));
    }

    @Override
    public CompanyResponseDTO updateCompany(Long id, CompanyRequestDTO companyRequest) {
        Tenant company = companyRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Tenant not found"));
        company.setName(companyRequest.getName());
        return companyMapper.toDto(companyRepository.save(company));
    }

    @Override
    public void deleteCompany(Long id) {
        companyRepository.deleteById(id);
    }

    @Override
    public CompanyResponseDTO getCompanyById(Long id) {
        Tenant company = companyRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Tenant not found"));
        return companyMapper.toDto(company);
    }

    @Override
    public List<CompanyResponseDTO> getAllCompanies() {
        return companyRepository.findAll().stream()
                .map(companyMapper::toDto)
                .collect(Collectors.toList());
    }
}
