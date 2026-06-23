package com.ensas.billing_service.config;

import com.ensas.billing_service.entities.Bill;
import com.ensas.billing_service.entities.Cart;
import com.ensas.billing_service.entities.CartItem;
import com.ensas.billing_service.entities.ProductItem;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.rest.core.config.RepositoryRestConfiguration;
import org.springframework.data.rest.webmvc.config.RepositoryRestConfigurer;
import org.springframework.web.servlet.config.annotation.CorsRegistry;

@Configuration
public class RestRepositoryConfig implements RepositoryRestConfigurer {
    @Override
    public void configureRepositoryRestConfiguration(RepositoryRestConfiguration config, CorsRegistry cors) {
        config.exposeIdsFor(Bill.class, ProductItem.class, Cart.class, CartItem.class);
    }
}
