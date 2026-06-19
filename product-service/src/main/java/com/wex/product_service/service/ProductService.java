package com.wex.product_service.service;

import com.wex.product_service.model.OrderItem;
import com.wex.product_service.model.Product;
import com.wex.product_service.repository.ProductRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.core.PriorityOrdered;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class ProductService {

    @Autowired
    private ProductRepository productRepo;


    @Cacheable(value = "products:list")
    public List<Product> findAllProducts(){
        log.info("Finding all products request");
        return productRepo.findAll();
    }

    @Cacheable(value = "products", key = "#id")
    public Product findProductById(String id){
        log.info("Finding product request by id{}", id);
        return productRepo.findById(id).orElse(null);
    }

    public ResponseEntity<Product> findProductByName(String name){
        log.info("Finding product request by name{}", name);
        return  new ResponseEntity<>(productRepo.findByName(name).orElse(null), HttpStatus.OK);
    }

    @CacheEvict(value = "products:list", allEntries = true)
    @CachePut(value = "products", key = "#result.id")
    public Product saveProduct(Product product){
        log.info("Saving product request {}", product.toString());
        Product saved = productRepo.save(product);
        log.info("Saved product successfully {}", saved);
        return saved;
    }

    @CachePut(value = "products", key = "#product.id")
    @CacheEvict(value = "products:list",  allEntries = true)
    public Product updateProduct(Product product){
        log.info("Updating product request {}", product.toString());
        Product updated = productRepo.save(product);
        log.info("Updated product successfully {}", updated);
        return updated;
    }

    @Caching(evict = {
            @CacheEvict(value = "products:list", allEntries = true),
            @CacheEvict(value = "products", key = "#id")
    })
    public void deleteProduct(String id){
        log.info("Deleting product request by id {}", id);
        productRepo.deleteById(id);
        log.info("Deleted product successfully {}", id);
    }

    public ResponseEntity<OrderItem> addToOrder(String productId, int quantity) {
        log.info("Adding product to order, productId{}, orderId{}", productId, quantity);
        Product product = productRepo.findById(productId).orElse(null);
        OrderItem orderItem = new OrderItem();
        orderItem.setName(product.getName());
        orderItem.setProductId(productId);
        orderItem.setQuantity(quantity);
        orderItem.setPrice(product.getPrice()*quantity);
        orderItem.setDescription(product.getDescription());

        product.setAmount(product.getAmount()-quantity);
        updateProduct(product);
        log.info("Added product successfully {}", product);

        return new ResponseEntity<>(orderItem, HttpStatus.OK);
    }
}
