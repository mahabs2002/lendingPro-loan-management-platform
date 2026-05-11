package com.loanapp.product;

import com.loanapp.entity.LoanProduct;
import com.loanapp.exception.ResourceNotFoundException;
import com.loanapp.product.dto.LoanProductDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LoanProductService {

    private final LoanProductRepository repository;

    public LoanProduct create(LoanProductDto dto) {
        LoanProduct product = LoanProduct.builder()
                .productName(dto.getProductName())
                .minAmount(dto.getMinAmount())
                .maxAmount(dto.getMaxAmount())
                .interestRate(dto.getInterestRate())
                .tenureMonths(dto.getTenureMonths())
                .processingFee(dto.getProcessingFee())
                .activeStatus(dto.getActiveStatus())
                .build();
        return repository.save(product);
    }

    public LoanProduct update(Long id, LoanProductDto dto) {
        LoanProduct p = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Loan product not found"));
        p.setProductName(dto.getProductName());
        p.setMinAmount(dto.getMinAmount());
        p.setMaxAmount(dto.getMaxAmount());
        p.setInterestRate(dto.getInterestRate());
        p.setTenureMonths(dto.getTenureMonths());
        p.setProcessingFee(dto.getProcessingFee());
        p.setActiveStatus(dto.getActiveStatus());
        return repository.save(p);
    }

    public List<LoanProduct> getAll() { return repository.findAll(); }
    public List<LoanProduct> getActive() { return repository.findByActiveStatusTrue(); }

    public LoanProduct getById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Loan product not found: " + id));
    }

    public void toggleStatus(Long id) {
        LoanProduct p = getById(id);
        p.setActiveStatus(!p.getActiveStatus());
        repository.save(p);
    }
}
