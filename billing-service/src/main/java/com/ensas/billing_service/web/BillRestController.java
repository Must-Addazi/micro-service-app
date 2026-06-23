package com.ensas.billing_service.web;

import com.ensas.billing_service.dto.CreateBillRequest;
import com.ensas.billing_service.entities.Bill;
import com.ensas.billing_service.service.BillService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class BillRestController {

    private final BillService billService;

    public BillRestController( BillService billService) {
        this.billService = billService;
    }
    @GetMapping("/bills/{id}")
    public ResponseEntity<Bill> bill(@PathVariable Long id){
       return ResponseEntity.ok(billService.bill(id));
    }

    @PostMapping("/bills/create")
    public ResponseEntity<Bill> createBill(@RequestBody CreateBillRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(billService.createBill(request));
    }
}
