package com.ensas.billing_service.entities;

import com.ensas.billing_service.enums.BillStatus;
import com.ensas.billing_service.model.Customer;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class Bill {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private LocalDateTime billingDate;

    @Enumerated(EnumType.STRING)
    private BillStatus billStatus;

    private Long customerId;
    @Builder.Default
    @OneToMany(mappedBy = "bill")
    private List<ProductItem> productItems= new ArrayList<>();
    @Transient
    private Customer customer;

    public double getTotal() {
        if (productItems == null) {
            return 0;
        }
        return productItems.stream().mapToDouble(ProductItem::getLineTotal).sum();
    }
}
