package com.ensas.demo.web;

import com.ensas.demo.entities.Customer;
import com.ensas.demo.repositories.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@CrossOrigin("*")
public class CustomerRestController {
    private final CustomerRepository customerRepository;

    @GetMapping("/customers/all")
    public ResponseEntity<List<Customer>> getCustomers(){
         return ResponseEntity.ok(customerRepository.findAll());
    }
}
