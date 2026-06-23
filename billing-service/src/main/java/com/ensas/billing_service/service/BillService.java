package com.ensas.billing_service.service;

import com.ensas.billing_service.dto.CreateBillRequest;
import com.ensas.billing_service.entities.Bill;

public interface BillService {
    Bill createBill(CreateBillRequest request);
    Bill bill( Long id);

}