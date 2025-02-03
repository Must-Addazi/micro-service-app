package com.ensas.billing_service.feign;

import com.ensas.billing_service.model.Customer;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.hateoas.PagedModel;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "customer-service")
public interface CustomerRestClient {
    @GetMapping("/customers/{id}")
    @CircuitBreaker(name = "customerServiceCB",fallbackMethod = "getDefaultCustomer")
    Customer getCustomerById(@PathVariable Long id);
    @CircuitBreaker(name = "customerServiceCB",fallbackMethod = "getDefaultAllCustomer")
    @Cacheable
    @GetMapping("/customers")
    PagedModel<Customer> getAllCustomers();
@Retry(name = "retry",fallbackMethod = "getDefaultCustomer")
    default Customer getDefaultCustomer(Long id,Exception exception){
    return  Customer.builder()
        .id(id)
        .name("Default Name")
        .email("Default Email")
        .build();
    }
    default PagedModel<Customer> getDefaultAllCustomer(Exception e){
        return PagedModel.empty();
    }
}
