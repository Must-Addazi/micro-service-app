package com.example.demo;

import com.example.demo.entities.Product;
import com.example.demo.repositories.ProductRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.UUID;

@SpringBootApplication
public class InventoryServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(InventoryServiceApplication.class, args);
	}
@Bean
	CommandLineRunner commandLineRunner(ProductRepository productRepository){
       return  args -> {
		   productRepository.save(
				   Product.builder()
				   .id(UUID.randomUUID().toString())
				   .name("computer")
				   .price(12323.0)
				   .quantity(123)
				   .build()
		   );
		   productRepository.save(
				   Product.builder()
						   .id(UUID.randomUUID().toString())
						   .name("printer")
						   .price(1323.0)
						   .quantity(13)
						   .build()
		   );
		   productRepository.save(
				   Product.builder()
						   .id(UUID.randomUUID().toString())
						   .name("SmartPhone")
						   .price(1423.0)
						   .quantity(12)
						   .build()
		   );
		   productRepository.findAll().forEach(p->
				   System.out.println(p.toString()));
	   };
	}

}
