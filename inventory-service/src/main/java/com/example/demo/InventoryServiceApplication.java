package com.example.demo;


import com.example.demo.entities.Product;
import com.example.demo.enums.Category;
import com.example.demo.repositories.ProductRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.math.BigDecimal;
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
				   .name("computer")
				   .description("Business laptop for office and study")
				   .category(Category.ELECTRONICS)
				   .imageUrl("https://images.unsplash.com/photo-1496181133206-80ce9b88a853?auto=format&fit=crop&w=800&q=80")
				   .price(BigDecimal.valueOf(12323.0))
				   .quantity(123)
				   .build()
		   );
		   productRepository.save(
				   Product.builder()
						   .name("printer")
						   .description("Wireless printer for home and small business")
						   .category(Category.ELECTRONICS)
						   .imageUrl("https://images.unsplash.com/photo-1612815154858-60aa4c59eaa6?auto=format&fit=crop&w=800&q=80")
						   .price(BigDecimal.valueOf(1323.0))
						   .quantity(13)
						   .build()
		   );
		   productRepository.save(
				   Product.builder()
						   .name("SmartPhone")
						   .description("Smartphone with fast performance and modern display")
						   .category(Category.ELECTRONICS)
						   .imageUrl("https://images.unsplash.com/photo-1511707171634-5f897ff02aa9?auto=format&fit=crop&w=800&q=80")
						   .price(BigDecimal.valueOf(1423.0))
						   .quantity(12)
						   .build()
		   );
		   productRepository.findAll().forEach(p->
				   System.out.println(p.toString()));
	   };
	}

}
