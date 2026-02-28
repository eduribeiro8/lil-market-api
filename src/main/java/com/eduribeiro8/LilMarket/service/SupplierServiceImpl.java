package com.eduribeiro8.LilMarket.service;

import com.eduribeiro8.LilMarket.dto.SupplierRequestDTO;
import com.eduribeiro8.LilMarket.dto.SupplierResponseDTO;
import com.eduribeiro8.LilMarket.entity.Supplier;
import com.eduribeiro8.LilMarket.mapper.SupplierMapper;
import com.eduribeiro8.LilMarket.repository.SupplierRepository;
import com.eduribeiro8.LilMarket.rest.exception.DuplicateSupplierException;
import com.eduribeiro8.LilMarket.rest.exception.SupplierNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SupplierServiceImpl implements SupplierService{

    private final SupplierRepository supplierRepository;
    private final SupplierMapper supplierMapper;

    @Override
    @Transactional
    public SupplierResponseDTO save(SupplierRequestDTO supplierRequestDTO) {
        Supplier supplierInDB = supplierRepository.findByName(supplierRequestDTO.name());

        if (supplierInDB != null
                && supplierInDB.getName().equals(supplierRequestDTO.name())
                && supplierInDB.getDistrict().equals(supplierRequestDTO.district())){
            throw new DuplicateSupplierException("Fornecedor já cadastrado");
        }

        Supplier supplierToSave = supplierMapper.toEntity(supplierRequestDTO);

        Supplier savedSupplier = supplierRepository.save(supplierToSave);

        return supplierMapper.toResponse(savedSupplier);
    }

    @Override
    public SupplierResponseDTO findByIdDTO(Long id) {
        return supplierMapper.toResponse(findById(id));
    }

    @Override
    public Supplier findById(Long id) {
        return supplierRepository.findById(id)
                .orElseThrow(() -> new SupplierNotFoundException("Fornecedor não encontrado"));
    }

    @Override
    public Page<SupplierResponseDTO> getAll(Pageable pageable) {
        Page<Supplier> supplierPage = supplierRepository.findAll(pageable);

        return supplierPage.map(supplierMapper::toResponse);
    }
}
