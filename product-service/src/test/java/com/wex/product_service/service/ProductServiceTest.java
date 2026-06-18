package com.wex.product_service.service;

import com.wex.product_service.model.OrderItem;
import com.wex.product_service.model.Product;
import com.wex.product_service.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepo;

    @InjectMocks
    private ProductService productService;

    Product product;

    @BeforeEach
    void setUp() {
        product = new Product();
        product.setId("1");
        product.setName("Iphone");
    }

    @Test
    void shouldFindAllProducts() {

        when(productRepo.findAll()).thenReturn(List.of(product));

        ResponseEntity<List<Product>> response = new ResponseEntity<>( productService.findAllProducts(),  HttpStatus.OK);

        assertEquals(200, response.getStatusCode().value());
        assertEquals(1, response.getBody().size());

        verify(productRepo, times(1)).findAll();
    }

    @Test
    void shouldFindProductById() {

        when(productRepo.findById("1")).thenReturn(Optional.of(product));

        ResponseEntity<Product> response = new ResponseEntity<>(productService.findProductById("1"), HttpStatus.OK);

        assertNotNull(response.getBody());
        assertEquals("Iphone", response.getBody().getName());

        verify(productRepo).findById("1");
    }

    @Test
    void shouldFindProductByName() {

        when(productRepo.findByName("Iphone")).thenReturn(Optional.of(product));

        ResponseEntity<Product> response = productService.findProductByName("Iphone");

        assertNotNull(response.getBody());
        assertEquals("Iphone", response.getBody().getName());

        verify(productRepo).findByName("Iphone");
    }

    @Test
    void shouldSaveProduct() {

        when(productRepo.save(product)).thenReturn(product);

        ResponseEntity<Product> response = new ResponseEntity<>(productService.saveProduct(product), HttpStatus.OK);

        assertNotNull(response.getBody());
        assertEquals("Iphone", response.getBody().getName());

        verify(productRepo).save(product);
    }

    @Test
    void shouldUpdateProduct() {

        when(productRepo.save(product)).thenReturn(product);

        ResponseEntity<Product> response = new ResponseEntity<>(productService.updateProduct(product), HttpStatus.OK);

        assertNotNull(response.getBody());
        assertEquals("Iphone", response.getBody().getName());

        verify(productRepo).save(product);
    }

    @Test
    void shouldDeleteProduct() {
        doNothing().when(productRepo).deleteById("1");
        productService.deleteProduct("1");
        ResponseEntity<String> response = new ResponseEntity<>("product deleted successfully",HttpStatus.OK);

        assertEquals("product deleted successfully", response.getBody());

        verify(productRepo).deleteById("1");
    }

    @Test
    void shouldAddProductToOrder() {
        Product product = new Product();
        product.setId("1");
        product.setName("Iphone");
        product.setDescription("cool");
        product.setPrice(1000);
        product.setAmount(10);

        when(productRepo.findById("1")).thenReturn(Optional.of(product));
        when(productRepo.save(any(Product.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        ResponseEntity<OrderItem> response =
                productService.addToOrder("1", 2);

        OrderItem orderItem = response.getBody();

        assertNotNull(orderItem);
        assertEquals("1", orderItem.getProductId());
        assertEquals("Iphone", orderItem.getName());
        assertEquals(2, orderItem.getQuantity());
        assertEquals(2000, orderItem.getPrice());

        assertEquals(8, product.getAmount());

        verify(productRepo).findById("1");
        verify(productRepo).save(product);
    }
}