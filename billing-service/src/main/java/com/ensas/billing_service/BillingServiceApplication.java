package com.ensas.billing_service;

import com.ensas.billing_service.entities.Bill;
import com.ensas.billing_service.entities.ProductItem;
import com.ensas.billing_service.feign.CustomerRestClient;
import com.ensas.billing_service.feign.ProductRestClient;
import com.ensas.billing_service.model.Customer;
import com.ensas.billing_service.model.Product;
import com.ensas.billing_service.repositories.BillRepository;
import com.ensas.billing_service.repositories.ProductItemRepository;
import org.bouncycastle.pqc.legacy.math.ntru.polynomial.ProductFormPolynomial;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Random;

@SpringBootApplication
@EnableFeignClients
public class BillingServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(BillingServiceApplication.class, args);
	}
 @Bean
  CommandLineRunner commandLineRunner(BillRepository billRepository, ProductItemRepository productItemRepository
 , CustomerRestClient customerRestClient , ProductRestClient productRestClient){
		return args -> {
         Collection<Customer> customers = customerRestClient.getAllCustomers().getContent();
		 Collection<Product> products = productRestClient.getAllProducts().getContent();
		 customers.forEach(customer -> {
			 Bill bill = Bill.builder().billingDate(new Date()).customerId(customer.getId()).build();
			 billRepository.save(bill);
			 products.forEach(product -> {
				 ProductItem productItem= ProductItem.builder()
						 .bill(bill)
						 .productId(product.getId())
						 .quantity(1+ new Random().nextInt(10))
						 .unitPrice(product.getPrice())
						 .build();
				 productItemRepository.save(productItem);
			 });
		 });
		};
  }
}
