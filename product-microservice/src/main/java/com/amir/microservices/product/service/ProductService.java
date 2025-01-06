package com.amir.microservices.product.service;

import com.amir.microservices.product.dto.ProductRequest;
import com.amir.microservices.product.dto.ProductResponse;
import com.amir.microservices.product.model.Product;
import com.amir.microservices.product.repository.ProductRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductService {

    private final ProductRepository productRepository;

    public ProductResponse createProduct(ProductRequest productRequest) {
        Product product = Product.builder()
                .name(productRequest.name())
                .description(productRequest.description())
                .price(productRequest.price())
                .build();
        productRepository.insert(product);
        log.info("Product created successfully");
        return new ProductResponse
                (product.getId(), productRequest.name(), product.getDescription(), product.getPrice());
    }

    public List<ProductResponse> getAllProducts() {

        return productRepository.findAll()
                .stream()
                .map(pr -> new ProductResponse(
                        pr.getId(),
                        pr.getName(),
                        pr.getDescription(),
                        pr.getPrice()))
                .toList();
    }
}
