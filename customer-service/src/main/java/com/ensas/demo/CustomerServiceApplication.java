package com.ensas.demo;


import com.ensas.demo.config.CustomerConfigParams;
import com.ensas.demo.entities.Customer;
import com.ensas.demo.repositories.CustomerRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;


@SpringBootApplication
@EnableConfigurationProperties(CustomerConfigParams.class)
public class CustomerServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(CustomerServiceApplication.class, args);
	}

	@Bean
	CommandLineRunner commandLineRunner(CustomerRepository customerRepository){
		return args -> {
			customerRepository.save(Customer.builder().name("mustapha").email("must@gmail.com").build());
			customerRepository.save(Customer.builder().name("Amine").email("must@gmail.com").build());
			customerRepository.save(Customer.builder().name("Hasna").email("must@gmail.com").build());
       customerRepository.findAll().forEach(c->{
		   System.out.println("****************");
		   System.out.println(c.getId());
		   System.out.println(c.getName());
		   System.out.println(c.getEmail());
	   });
		};
	}

}
