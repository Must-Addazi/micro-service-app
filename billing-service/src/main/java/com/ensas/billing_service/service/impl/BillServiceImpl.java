package com.ensas.billing_service.service.impl;

import com.ensas.billing_service.dto.CreateBillRequest;
import com.ensas.billing_service.entities.Bill;
import com.ensas.billing_service.entities.ProductItem;
import com.ensas.billing_service.enums.BillStatus;
import com.ensas.billing_service.execeptions.IllegalQuantityException;
import com.ensas.billing_service.execeptions.NotFoundException;
import com.ensas.billing_service.feign.CustomerRestClient;
import com.ensas.billing_service.feign.ProductRestClient;
import com.ensas.billing_service.model.Product;
import com.ensas.billing_service.repositories.BillRepository;
import com.ensas.billing_service.repositories.ProductItemRepository;
import com.ensas.billing_service.service.BillService;
import jakarta.ws.rs.BadRequestException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class BillServiceImpl implements BillService {


    private final CustomerRestClient customerRestClient;
    private final ProductRestClient productRestClient;
    private final BillRepository billRepository;
    private final ProductItemRepository productItemRepository;

    @Override
    public Bill createBill( CreateBillRequest request) {
        if (request.customerId() == null || request.items() == null || request.items().isEmpty()) {
            throw new BadRequestException("Customer and items are required");
        }

        customerRestClient.getCustomerById(request.customerId());
        Bill bill = Bill.builder()
                .billingDate(LocalDateTime.now())
                .billStatus(BillStatus.PENDING)
                .customerId(request.customerId())
                .build();
        billRepository.save(bill);
        List<ProductItem> productItemList = new ArrayList<>();

        request.items().forEach(item -> {
            if (item.quantity() <= 0) {
                throw new IllegalQuantityException("Item quantity must be positive");
            }

            Product product = productRestClient.decreaseStock(item.productId(), item.quantity());
            ProductItem productItem = ProductItem.builder()
                    .bill(bill)
                    .productId(product.getId())
                    .quantity(item.quantity())
                    .unitPrice(product.getPrice())
                    .build();
            ProductItem saved =  productItemRepository.save(productItem);
            productItemList.add(saved);
        });
          bill.setProductItems(productItemList);
        return bill(bill.getId());
    }

    @Override
    public Bill bill(Long id) {
        Bill bill= billRepository.findById(id).orElseThrow(
                () -> new NotFoundException("Bill not found with Id "+id)
        );
        bill.setCustomer(customerRestClient.getCustomerById(bill.getCustomerId()));
        bill.getProductItems().forEach(productItem -> {
            productItem.setProduct(productRestClient.getProductById(productItem.getProductId()));
        });
        return bill;    }
}
