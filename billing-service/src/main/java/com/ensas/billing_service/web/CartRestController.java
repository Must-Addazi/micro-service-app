package com.ensas.billing_service.web;

import com.ensas.billing_service.dto.AddCartItemRequest;
import com.ensas.billing_service.dto.CartItemResponse;
import com.ensas.billing_service.dto.CartResponse;
import com.ensas.billing_service.dto.CheckoutCartRequest;
import com.ensas.billing_service.entities.Bill;
import com.ensas.billing_service.entities.Cart;
import com.ensas.billing_service.entities.CartItem;
import com.ensas.billing_service.enums.CartStatus;
import com.ensas.billing_service.entities.ProductItem;
import com.ensas.billing_service.feign.CustomerRestClient;
import com.ensas.billing_service.feign.ProductRestClient;
import com.ensas.billing_service.model.Product;
import com.ensas.billing_service.repositories.BillRepository;
import com.ensas.billing_service.repositories.CartItemRepository;
import com.ensas.billing_service.repositories.CartRepository;
import com.ensas.billing_service.repositories.ProductItemRepository;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Date;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class CartRestController {
    private static final String CART_COOKIE = "cart_id";
    private static final Duration CART_TTL = Duration.ofDays(30);
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final BillRepository billRepository;
    private final ProductItemRepository productItemRepository;
    private final ProductRestClient productRestClient;
    private final CustomerRestClient customerRestClient;

    @GetMapping("/cart")
    public CartResponse getCart(@CookieValue(name = CART_COOKIE, required = false) String cartToken) {
        return cartToken == null ? emptyCart() : toResponse(findActiveCart(cartToken));
    }

    @PostMapping("/cart/items")
    public CartResponse addItem(@CookieValue(name = CART_COOKIE, required = false) String cartToken,
                                @RequestBody AddCartItemRequest request,
                                HttpServletResponse response) {
        if (request.productId() == null || request.quantity() <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Product and quantity are required");
        }

        CartContext context = getOrCreateCart(cartToken, response);
        Product product = productRestClient.getProductById(request.productId());
        if (product.getQuantity() < request.quantity()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Insufficient stock");
        }

        CartItem cartItem = cartItemRepository.findByCartAndProductId(context.cart(), request.productId())
                .orElseGet(() -> CartItem.builder()
                        .cart(context.cart())
                        .productId(product.getId())
                        .unitPrice(product.getPrice())
                        .quantity(0)
                        .build());
        int nextQuantity = cartItem.getQuantity() + request.quantity();
        if (product.getQuantity() < nextQuantity) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Insufficient stock");
        }
        cartItem.setQuantity(nextQuantity);
        cartItem.setUnitPrice(product.getPrice());
        cartItemRepository.save(cartItem);
        touch(context.cart());
        return toResponse(reloadCart(context.cart()));
    }

    @PatchMapping("/cart/items/{productId}")
    public CartResponse updateItem(@CookieValue(name = CART_COOKIE, required = false) String cartToken,
                                   @PathVariable Long productId,
                                   @RequestBody AddCartItemRequest request) {
        Cart cart = requireActiveCart(cartToken);
        if (request.quantity() <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Quantity must be positive");
        }
        Product product = productRestClient.getProductById(productId);
        if (product.getQuantity() < request.quantity()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Insufficient stock");
        }
        CartItem item = cartItemRepository.findByCartAndProductId(cart, productId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cart item not found"));
        item.setQuantity(request.quantity());
        item.setUnitPrice(product.getPrice());
        cartItemRepository.save(item);
        touch(cart);
        return toResponse(reloadCart(cart));
    }

    @DeleteMapping("/cart/items/{productId}")
    public CartResponse removeItem(@CookieValue(name = CART_COOKIE, required = false) String cartToken,
                                   @PathVariable Long productId) {
        Cart cart = requireActiveCart(cartToken);
        CartItem item = cartItemRepository.findByCartAndProductId(cart, productId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cart item not found"));
        cartItemRepository.delete(item);
        touch(cart);
        return toResponse(reloadCart(cart));
    }

    @DeleteMapping("/cart")
    public CartResponse clearCart(@CookieValue(name = CART_COOKIE, required = false) String cartToken,
                                  HttpServletResponse response) {
        if (cartToken != null) {
            findActiveCart(cartToken).ifPresent(cart -> {
                cart.getItems().clear();
                touch(cart);
            });
        }
        clearCookie(response);
        return emptyCart();
    }

    @PostMapping("/cart/checkout")
    public Bill checkout(@CookieValue(name = CART_COOKIE, required = false) String cartToken,
                         @RequestBody CheckoutCartRequest request,
                         HttpServletResponse response) {
        if (request.customerId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Customer is required");
        }
        Cart cart = requireActiveCart(cartToken);
        if (cart.getItems().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cart is empty");
        }

        customerRestClient.getCustomerById(request.customerId());
        Bill bill = billRepository.save(Bill.builder()
                .billingDate(LocalDateTime.now())
                .customerId(request.customerId())
                .build());

        cart.getItems().forEach(item -> {
            Product product = productRestClient.decreaseStock(item.getProductId(), item.getQuantity());
            ProductItem productItem = ProductItem.builder()
                    .bill(bill)
                    .productId(product.getId())
                    .quantity(item.getQuantity())
                    .unitPrice(product.getPrice())
                    .build();
            productItemRepository.save(productItem);
        });

        cart.setStatus(CartStatus.CHECKED_OUT);
        touch(cart);
        clearCookie(response);
        Bill savedBill = billRepository.findById(Long.valueOf(bill.getId()))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Bill not found"));
        return enrichBill(savedBill);
    }

    private CartContext getOrCreateCart(String cartToken, HttpServletResponse response) {
        if (cartToken != null) {
            Cart existingCart = findActiveCart(cartToken).orElse(null);
            if (existingCart != null && existingCart.getExpiresAt().isAfter(Instant.now())) {
                return new CartContext(existingCart, cartToken);
            }
        }

        String newToken = generateToken();
        Instant now = Instant.now();
        Cart cart = cartRepository.save(Cart.builder()
                .tokenHash(hashToken(newToken))
                .createdAt(now)
                .updatedAt(now)
                .expiresAt(now.plus(CART_TTL))
                .status(CartStatus.ACTIVE)
                .build());
        setCookie(response, newToken);
        return new CartContext(cart, newToken);
    }

    private Cart requireActiveCart(String cartToken) {
        if (cartToken == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Cart not found");
        }
        Cart cart = findActiveCart(cartToken)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cart not found"));
        if (cart.getExpiresAt().isBefore(Instant.now())) {
            cart.setStatus(CartStatus.EXPIRED);
            cartRepository.save(cart);
            throw new ResponseStatusException(HttpStatus.GONE, "Cart expired");
        }
        return cart;
    }

    private java.util.Optional<Cart> findActiveCart(String cartToken) {
        return cartRepository.findByTokenHashAndStatus(hashToken(cartToken), CartStatus.ACTIVE);
    }

    private void touch(Cart cart) {
        cart.setUpdatedAt(Instant.now());
        cart.setExpiresAt(Instant.now().plus(CART_TTL));
        cartRepository.save(cart);
    }

    private Cart reloadCart(Cart cart) {
        return cartRepository.findById(cart.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cart not found"));
    }

    private CartResponse toResponse(java.util.Optional<Cart> cart) {
        return cart.map(this::toResponse).orElseGet(this::emptyCart);
    }

    private CartResponse toResponse(Cart cart) {
        List<CartItemResponse> items = cart.getItems().stream()
                .map(item -> {
                    Product product = productRestClient.getProductById(item.getProductId());
                    item.setProduct(product);
                    return new CartItemResponse(
                            item.getId(),
                            item.getProductId(),
                            item.getQuantity(),
                            item.getUnitPrice(),
                            item.getLineTotal(),
                            product
                    );
                })
                .toList();
        int count = items.stream().mapToInt(CartItemResponse::quantity).sum();
        double total = items.stream().mapToDouble(CartItemResponse::lineTotal).sum();
        return new CartResponse(items, count, total);
    }

    private CartResponse emptyCart() {
        return new CartResponse(List.of(), 0, 0);
    }

    private Bill enrichBill(Bill bill) {
        bill.setCustomer(customerRestClient.getCustomerById(bill.getCustomerId()));
        bill.getProductItems().forEach(productItem ->
                productItem.setProduct(productRestClient.getProductById(productItem.getProductId()))
        );
        return bill;
    }

    private void setCookie(HttpServletResponse response, String token) {
        ResponseCookie cookie = ResponseCookie.from(CART_COOKIE, token)
                .httpOnly(true)
                .secure(false)
                .sameSite("Lax")
                .path("/")
                .maxAge(CART_TTL)
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }

    private void clearCookie(HttpServletResponse response) {
        ResponseCookie cookie = ResponseCookie.from(CART_COOKIE, "")
                .httpOnly(true)
                .secure(false)
                .sameSite("Lax")
                .path("/")
                .maxAge(Duration.ZERO)
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }

    private String generateToken() {
        byte[] bytes = new byte[32];
        SECURE_RANDOM.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    private String hashToken(String token) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(token.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 is not available", e);
        }
    }

    private record CartContext(Cart cart, String token) {
    }
}
