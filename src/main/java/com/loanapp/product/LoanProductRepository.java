package com.loanapp.product;

import com.loanapp.entity.LoanProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface LoanProductRepository extends JpaRepository<LoanProduct, Long> {
    List<LoanProduct> findByActiveStatusTrue();
}
