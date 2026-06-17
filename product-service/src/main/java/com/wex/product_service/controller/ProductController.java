package com.wex.product_service.controller;

import com.wex.product_service.model.OrderItem;
import com.wex.product_service.model.Product;
import com.wex.product_service.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
@CrossOrigin
public class ProductController {

    @Autowired
    ProductService productService;

    @GetMapping
    public ResponseEntity<List<Product>> getAllProducts(){
        return productService.findAllProducts();
    }

    @GetMapping("/{productId}")
    public ResponseEntity<Product> getProductById(@PathVariable String productId){
        return productService.findProductById(productId);
    }

    @PostMapping
    public ResponseEntity<Product> createProduct(@RequestBody Product product){
        return productService.saveProduct(product);
    }

    @PutMapping
    public ResponseEntity<Product> updateProduct(@RequestBody Product product){
        return productService.updateProduct(product);
    }

    @DeleteMapping("/{productId}")
    public ResponseEntity<String> deleteProduct(@PathVariable String productId){
        return productService.deleteProduct(productId);
    }

    @PostMapping("/addToOrder")
    public ResponseEntity<OrderItem> addToOrder(@RequestParam String productId, @RequestParam int quantity){
        return productService.addToOrder(productId, quantity);
    }

}
