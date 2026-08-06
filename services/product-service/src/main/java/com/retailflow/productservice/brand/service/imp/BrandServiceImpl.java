package com.retailflow.productservice.brand.service.imp;

import com.retailflow.productservice.brand.dto.request.BrandCreateRequest;
import com.retailflow.productservice.brand.dto.request.BrandUpdateRequest;
import com.retailflow.productservice.brand.dto.response.BrandResponse;
import com.retailflow.productservice.brand.entity.Brand;
import com.retailflow.productservice.brand.mapper.BrandMapper;
import com.retailflow.productservice.brand.repository.BrandRepository;
import com.retailflow.productservice.brand.service.BrandService;
import com.retailflow.productservice.common.exception.DuplicateResourceException;
import com.retailflow.productservice.common.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class BrandServiceImpl implements BrandService {

    private final BrandRepository brandRepository;

    private final BrandMapper brandMapper;

    @Override
    public BrandResponse createBrand(BrandCreateRequest request) {
        if (brandRepository.existsByName(request.getName())) {
            throw new DuplicateResourceException("Brand name already exists.");
        }
        if (brandRepository.existsByBrandCode(request.getBrandCode())) {
            throw new DuplicateResourceException("Brand code already exists.");
        }

        Brand brand = brandMapper.toEntity(request);
        brand.setActive(true);

        return brandMapper.toResponse(brandRepository.save(brand));
    }

    @Override
    public BrandResponse updateBrand(Long brandId, BrandUpdateRequest request) {
        Brand brand = findBrand(brandId);

        if (request.getName() != null
                && !request.getName().equals(brand.getName())
                && brandRepository.existsByName(request.getName())) {
            throw new DuplicateResourceException("Brand name already exists.");
        }
        if (request.getBrandCode() != null
                && !request.getBrandCode().equals(brand.getBrandCode())
                && brandRepository.existsByBrandCode(request.getBrandCode())) {
            throw new DuplicateResourceException("Brand code already exists.");
        }

        brandMapper.updateEntityFromDto(request, brand);

        return brandMapper.toResponse(brandRepository.save(brand));
    }

    @Override
    public BrandResponse getBrandById(Long brandId) {
        return brandMapper.toResponse(findBrand(brandId));
    }

    @Override
    public List<BrandResponse> getAllBrands() {
        return brandRepository.findAll().stream()
                .map(brandMapper::toResponse)
                .toList();
    }

    @Override
    public void deleteBrand(Long brandId) {
        Brand brand = findBrand(brandId);
        brand.setActive(false);
        brandRepository.save(brand);
    }

    private Brand findBrand(Long brandId) {
        return brandRepository.findById(brandId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Brand not found with id: " + brandId));
    }
}
