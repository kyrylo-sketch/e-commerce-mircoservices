package com.wex.product_service.service;

import com.wex.product_service.model.OrderItem;
import com.wex.product_service.model.Product;
import com.wex.product_service.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.PriorityOrdered;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepo;


    public ResponseEntity<List<Product>> findAllProducts(){
        return new ResponseEntity<>(productRepo.findAll(), HttpStatus.OK);
    }

    public ResponseEntity<Product> findProductById(String id){
        return new ResponseEntity<>(productRepo.findById(id).orElse(null), HttpStatus.OK);
    }

    public ResponseEntity<Product> findProductByName(String name){
        return  new ResponseEntity<>(productRepo.findByName(name).orElse(null), HttpStatus.OK);
    }

    public ResponseEntity<String> saveProduct(Product product){
        productRepo.save(product);
        return new ResponseEntity<>("product saved successfully",HttpStatus.OK);
    }

    public ResponseEntity<String> updateProduct(Product product){
        productRepo.save(product);
        return new ResponseEntity<>("product updated successfully",HttpStatus.OK);
    }

    public ResponseEntity<String> deleteProduct(String id){
        productRepo.deleteById(id);
        return new ResponseEntity<>("product deleted successfully",HttpStatus.OK);
    }

    public ResponseEntity<OrderItem> addToOrder(String productId, int quantity) {
        Product product = productRepo.findById(productId).orElse(null);
        OrderItem orderItem = new OrderItem();
        orderItem.setProductId(productId);
        orderItem.setQuantity(quantity);
        orderItem.setPrice(product.getPrice()*quantity);
        orderItem.setDescription(product.getDescription());

        return new ResponseEntity<>(orderItem, HttpStatus.OK);
    }
}
