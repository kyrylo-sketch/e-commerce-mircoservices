package com.wex.product_service.service;

import com.wex.product_service.model.OrderItem;
import com.wex.product_service.model.Product;
import com.wex.product_service.repository.ProductRepository;
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
public class ProductService {

    @Autowired
    private ProductRepository productRepo;


    @Cacheable(value = "products:list")
    public List<Product> findAllProducts(){
        return productRepo.findAll();
    }

//    @Cacheable(value = "products", key = "#id")
//    public ResponseEntity<Product> findProductById(String id){
//        return new ResponseEntity<>(productRepo.findById(id).orElse(null), HttpStatus.OK);
//    }
    @Cacheable(value = "products", key = "#id")
    public Product findProductById(String id){
        return productRepo.findById(id).orElse(null);
    }

    public ResponseEntity<Product> findProductByName(String name){
        return  new ResponseEntity<>(productRepo.findByName(name).orElse(null), HttpStatus.OK);
    }

    @CacheEvict(value = "products:list", allEntries = true)
    @CachePut(value = "products", key = "#result.id")
    public Product saveProduct(Product product){
        Product saved = productRepo.save(product);
        return saved;
    }

    @CachePut(value = "products", key = "#product.id")
    @CacheEvict(value = "products:list",  allEntries = true)
    public Product updateProduct(Product product){
        Product updated = productRepo.save(product);
        return updated;
    }

    @Caching(evict = {
            @CacheEvict(value = "products:list", allEntries = true),
            @CacheEvict(value = "products", key = "#id")
    })
    public void deleteProduct(String id){
        productRepo.deleteById(id);
    }

    public ResponseEntity<OrderItem> addToOrder(String productId, int quantity) {
        Product product = productRepo.findById(productId).orElse(null);
        OrderItem orderItem = new OrderItem();
        orderItem.setName(product.getName());
        orderItem.setProductId(productId);
        orderItem.setQuantity(quantity);
        orderItem.setPrice(product.getPrice()*quantity);
        orderItem.setDescription(product.getDescription());

        product.setAmount(product.getAmount()-quantity);
        updateProduct(product);

        return new ResponseEntity<>(orderItem, HttpStatus.OK);
    }
}
